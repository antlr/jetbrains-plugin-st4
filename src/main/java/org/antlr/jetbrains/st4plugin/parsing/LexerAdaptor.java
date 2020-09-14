package org.antlr.jetbrains.st4plugin.parsing;

import com.intellij.psi.PsiLanguageInjectionHost;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public abstract class LexerAdaptor extends Lexer {

    public static final char DELIMITERS_PREFIX = '\u0001';

    private final java.util.Queue<Token> queue = new java.util.LinkedList<>();

    private char lDelim = '<';
    private char rDelim = '>';

    private int subtemplateDepth;

    public LexerAdaptor(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        if (_input.index() == 0 && _input.LA(1) == DELIMITERS_PREFIX) {
            return lexDelimitersPrefix();
        }

        if (!queue.isEmpty()) {
            return queue.poll();
        }

        Token next = super.nextToken();

        return next.getType() == STLexer.TEXT ? mergeConsecutiveTextTokens(next) : next;
    }

    private Token mergeConsecutiveTextTokens(Token next) {
        StringBuilder builder = new StringBuilder();
        Token startToken = next;

        while (next.getType() == STLexer.TEXT) {
            builder.append(next.getText());
            next = super.nextToken();
        }

        // The `next` will _not_ be a TEXT-token, store it in
        // the queue to return the next time!
        queue.offer(next);

        CommonToken token = new CommonToken(startToken);
        token.setStopIndex(startToken.getStartIndex() + builder.length() - 1);

        return token;
    }

    /**
     * @see org.antlr.jetbrains.st4plugin.psi.STLanguageInjector#detectDelimiters(PsiLanguageInjectionHost)
     */
    private Token lexDelimitersPrefix() {
        int _lDelim = _input.LA(2);
        int _rDelim = _input.LA(3);

        if (_lDelim != -1 && _rDelim != -1) {
            setDelimiters((char) _lDelim, (char) _rDelim);

            // Consume the prefix and the delimiters
            _input.consume();
            _input.consume();
            _input.consume();

            return getTokenFactory().create(_tokenFactorySourcePair, STLexer.HORZ_WS, "xxx", HIDDEN, 0, 2, 1, 0);
        }

        return super.nextToken();
    }

    public void startSubTemplate() {
        subtemplateDepth++;

        // look for "{ args ID (',' ID)* '|' ..."
        if (isSubTemplateWithArgs()) {
            mode(STLexer.SubTemplate);
        } else {
            mode(STLexer.DEFAULT_MODE);
        }
    }

    private boolean isSubTemplateWithArgs() {
        int position = _input.index();
        int mark = _input.mark();

        boolean isSubTemplateWithArgs = matchSubTemplateWithArgs();

        _input.seek(position);
        _input.release(mark);

        return isSubTemplateWithArgs;
    }

    private boolean matchSubTemplateWithArgs() {
        matchWs();
        if (!matchId()) {
            return false;
        }
        matchWs();

        while (_input.LA(1) == ',') {
            _input.consume();
            matchWs();
            if (!matchId()) {
                return false;
            }
            matchWs();
        }

        return _input.LA(1) == '|';
    }

    private boolean matchId() {
        boolean isId = false;

        while (isIDLetter((char) _input.LA(1))) {
            _input.consume();
            isId = true;
        }

        return isId;
    }

    private void matchWs() {
        int c = _input.LA(1);
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            _input.consume();
            c = _input.LA(1);
        }
    }

    // if last RBrace, continue with mode Outside
    public boolean endsSubTemplate() {
        if (subtemplateDepth > 0) {
            subtemplateDepth--;
            mode(1); // STLexer.Inside

            return true;
        }

        return false;
    }

    public void setDelimiters(char lDelim, char rDelim) {
        this.lDelim = lDelim;
        this.rDelim = rDelim;
    }

    public boolean isLDelim() {
        return lDelim == _input.LA(-1);
    }

    public boolean isRDelim() {
        return rDelim == _input.LA(-1);
    }

    public boolean isLTmplComment() {
        return isLDelim() && _input.LA(1) == '!';
    }

    public boolean isRTmplComment() {
        return isRDelim() && _input.LA(-2) == '!';
    }

    public void adjText() {
        int c1 = _input.LA(-1);
        if (c1 == '\\') {
            int c2 = _input.LA(1);
            if (c2 == '\\') {
                _input.consume(); // convert \\ to \
            } else if (c2 == lDelim || c2 == '}') {
                _input.consume();
            }
        }
    }

    public Token newTokenFromPreviousChar(int ttype) {
        return _factory.create(_tokenFactorySourcePair, ttype, _text, _channel, getCharIndex() - 1, getCharIndex() - 1,
                _tokenStartLine, _tokenStartCharPositionInLine);
    }

    @Override
    public void setInputStream(IntStream input) {
        queue.clear();
        super.setInputStream(input);
    }

    private static boolean isIDLetter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '-' || c == '_';
    }
}
