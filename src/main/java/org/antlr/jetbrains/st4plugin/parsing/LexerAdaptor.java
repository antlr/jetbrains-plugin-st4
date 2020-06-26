package org.antlr.jetbrains.st4plugin.parsing;

import com.intellij.psi.PsiLanguageInjectionHost;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import static org.stringtemplate.v4.compiler.STLexer.isIDLetter;

public abstract class LexerAdaptor extends Lexer {

	public static final char DELIMITERS_PREFIX = '\u0001';

	public char lDelim = '<';
	public char rDelim = '>';

	public int subtemplateDepth;

	public LexerAdaptor(CharStream input) {
		super(input);
	}

	@Override
	public Token nextToken() {
		if (_input.index() == 0 && _input.LA(1) == DELIMITERS_PREFIX) {
			return lexDelimitersPrefix();
		}
		return super.nextToken();
	}

	/**
	 * @see org.antlr.jetbrains.st4plugin.psi.STLanguageInjector#detectDelimiters(PsiLanguageInjectionHost)
	 */
	private Token lexDelimitersPrefix() {
		int _lDelim = _input.LA(2);
		int _rDelim = _input.LA(3);

		if (_lDelim != -1 && _rDelim != -1) {
			lDelim = (char) _lDelim;
			rDelim = (char) _rDelim;

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

		while ( isIDLetter((char) _input.LA(1)) ) {
			_input.consume();
			isId = true;
		}

		return isId;
	}

	private void matchWs() {
		int c = _input.LA(1);
		while ( c==' ' || c=='\t' || c=='\n' || c=='\r' ) {
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

	public boolean adjText() {
		int c1 = _input.LA(-1);
		if (c1 == '\\') {
			int c2 = _input.LA(1);
			if (c2 == '\\') {
				_input.consume(); // convert \\ to \
			} else if (c2 == lDelim || c2 == '}') {
				_input.consume();
			}
		}
		return true;
	}

	public Token newTokenFromPreviousChar(int ttype) {
		return _factory.create(_tokenFactorySourcePair, ttype, _text, _channel, getCharIndex()-1, getCharIndex()-1,
				_tokenStartLine, _tokenStartCharPositionInLine);
	}
}
