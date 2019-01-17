/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// copied from ST4 source and updated to be useable as ANTLR4 token source

package org.antlr.jetbrains.st4plugin.parsing;


import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import org.stringtemplate.v4.compiler.STParser;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents the tokenizer for templates. It operates in two modes:
 * inside and outside of expressions. It implements the {@link TokenSource}
 * interface so it can be used with ANTLR parsers. Outside of expressions, we
 * can return these token types: {@link #TEXT}, {@link #INDENT}, {@link #LDELIM}
 * (start of expression), {@link #RCURLY} (end of subtemplate), and
 * {@link #NEWLINE}. Inside of an expression, this lexer returns all of the
 * tokens needed by {@link STParser}. From the parser's point of view, it can
 * treat a template as a simple stream of elements.
 * <p>
 * This class defines the token types and communicates these values to
 * {@code STParser.g} via {@code STLexer.tokens} file (which must remain
 * consistent).</p>
 */
public class STLexer extends Lexer {
    public static final int EOF = -1;              // EOF char
    public static final int EOF_TYPE = Token.EOF;  // EOF token type
    public static final int ERROR_TYPE = 99999999;

    public static final Token SKIP = new CommonToken(-1, "<skip>");

    // !!! must follow STLexer.tokens file that STParser.g loads !!!
    public static final int RBRACK=17;
    public static final int LBRACK=16;
    public static final int ELSE=5;
    public static final int ELLIPSIS=11;
    public static final int LCURLY=20;
    public static final int BANG=10;
    public static final int EQUALS=12;
    public static final int TEXT=22;
    public static final int ID=25;
    public static final int SEMI=9;
    public static final int LPAREN=14;
    public static final int IF=4;
    public static final int ELSEIF=6;
    public static final int COLON=13;
    public static final int RPAREN=15;
    public static final int COMMA=18;
    public static final int RCURLY=21;
    public static final int ENDIF=7;
    public static final int RDELIM=24;
    public static final int SUPER=8;
    public static final int DOT=19;
    public static final int LDELIM=23;
    public static final int STRING=26;
	public static final int PIPE=28;
	public static final int OR=29;
	public static final int AND=30;
	public static final int INDENT=31;
    public static final int NEWLINE=32;
    public static final int AT=33;
    public static final int REGION_END=34;
	public static final int TRUE=35;
	public static final int FALSE=36;
	public static final int COMMENT=37;


    /** The char which delimits the start of an expression. */
    char delimiterStartChar = '<';
    /** The char which delimits the end of an expression. */
    char delimiterStopChar = '>';

	/**
	 * This keeps track of the current mode of the lexer. Are we inside or
	 * outside an ST expression?
	 */
    boolean scanningInsideExpr = false;

    /** To be able to properly track the inside/outside mode, we need to
     *  track how deeply nested we are in some templates. Otherwise, we
     *  know whether a <code>'}'</code> and the outermost subtemplate to send this
	 *  back to outside mode.
     */
	public int subtemplateDepth = 0; // start out *not* in a {...} subtemplate

	/** template embedded in a group file? this is the template */
	Token templateToken;

	ANTLR3StringStream input;

	/** current character */
    int c;

    /** When we started token, track initial coordinates so we can properly
     *  build token objects.
     */
    int startCharIndex;
    int startLine;
    int startCharPositionInLine;

    /** Our lexer routines might have to emit more than a single token. We
     *  buffer everything through this list.
     */
    protected List<Token> tokens = new ArrayList<Token>();

	protected TokenFactory tokenFactory = new CommonTokenFactory();

	public STLexer(String input) { this(new ANTLR3StringStream(input), null, '<', '>'); }

    public STLexer(String input, Token templateToken) {
		this(new ANTLR3StringStream(input), templateToken, '<', '>');
	}

	public STLexer(ANTLR3StringStream input,
				   Token templateToken,
				   char delimiterStartChar,
				   char delimiterStopChar)
	{
		this.input = input; // set local specialized input for use in this subclass
		setInputStream(input);
		c = input.LA(1); // prime lookahead
		this.templateToken = templateToken;
		this.delimiterStartChar = delimiterStartChar;
		this.delimiterStopChar = delimiterStopChar;
	}

	@Override
	public void reset() {
		// don't use this; just to prevent errors when treated as a proper ANTLR lexer
	}

	@Override
	public int getCharPositionInLine() {
		return input.getCharPositionInLine();
	}

	@Override
	public int getLine() {
		return input.getLine();
	}

	@Override
	public CharStream getInputStream() {
		return input;
	}

	@Override
	public void setTokenFactory(TokenFactory<?> tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	@Override
	public TokenFactory<?> getTokenFactory() {
		return tokenFactory;
	}

	public void lexerError(int start, int stop) {
		System.err.println("ST lexerError " + start + ".." + stop);
		ANTLRErrorListener listener = getErrorListenerDispatch();
		final String text = input.getText(Interval.of(start, stop));
		final Token t = newToken(Token.INVALID_TYPE, text, start, stop, getLine(), getCharPositionInLine());
		listener.syntaxError(this, t, getLine(), getCharPositionInLine(),
							 "Bad char or token error in template: "+ text, null);
	}

	@Override
	public Token nextToken() {
		Token t;
		if ( tokens.size()>0 ) { t = tokens.remove(0); }
		else t = _nextToken();
//		System.out.println(t);
		return t;
	}

    /** Consume if {@code x} is next character on the input stream.
	 */
    public void match(char x) {
        if ( c != x ) {
	        lexerError(startCharIndex,input.index());
		}
		consume();
    }

	protected void consume() {
        input.consume();
        c = input.LA(1);
    }

    public void emit(Token token) { tokens.add(token); }

    public Token _nextToken() {
		//System.out.println("nextToken: c="+(char)c+"@"+input.index());
        while ( true ) { // lets us avoid recursion when skipping stuff
            startCharIndex = input.index();
            startLine = input.getLine();
            startCharPositionInLine = input.getCharPositionInLine();

            if ( c==EOF ) return newToken(EOF_TYPE);
            Token t;
            if ( scanningInsideExpr ) t = inside();
            else t = outside();
            if ( t!=SKIP ) return t;
        }
    }

    protected Token outside() {
        if ( input.getCharPositionInLine()==0 && (c==' '||c=='\t') ) {
            while ( c==' ' || c=='\t' ) consume(); // scarf indent
            if ( c!=EOF ) return newToken(INDENT);
            return newToken(TEXT);
        }
        if ( c==delimiterStartChar ) {
            consume();
            if ( c=='!' ) return COMMENT();
            if ( c=='\\' ) return ESCAPE(); // <\\> <\uFFFF> <\n> etc...
            scanningInsideExpr = true;
            return newToken(LDELIM);
        }
        if ( c=='\r' ) { consume(); consume(); return newToken(NEWLINE); } // \r\n -> \n
        if ( c=='\n') {	consume(); return newToken(NEWLINE); }
        if ( c=='}' && subtemplateDepth>0 ) {
            scanningInsideExpr = true;
            subtemplateDepth--;
            consume();
            return newTokenFromPreviousChar(RCURLY);
        }
        return mTEXT();
    }

    protected Token inside() {
        while ( true ) {
            switch ( c ) {
                case ' ': case '\t': case '\n': case '\r':
					consume();
					return SKIP;
                case '.' :
					consume();
					if ( input.LA(1)=='.' && input.LA(2)=='.' ) {
						consume();
						match('.');
						return newToken(ELLIPSIS);
					}
					return newToken(DOT);
                case ',' : consume(); return newToken(COMMA);
				case ':' : consume(); return newToken(COLON);
				case ';' : consume(); return newToken(SEMI);
                case '(' : consume(); return newToken(LPAREN);
                case ')' : consume(); return newToken(RPAREN);
                case '[' : consume(); return newToken(LBRACK);
                case ']' : consume(); return newToken(RBRACK);
				case '=' : consume(); return newToken(EQUALS);
                case '!' : consume(); return newToken(BANG);
                case '@' :
                    consume();
                    if ( c=='e' && input.LA(2)=='n' && input.LA(3)=='d' ) {
                        consume(); consume(); consume();
                        return newToken(REGION_END);
                    }
                    return newToken(AT);
                case '"' : return mSTRING();
                case '&' : consume(); match('&'); return newToken(AND); // &&
                case '|' : consume(); match('|'); return newToken(OR); // ||
				case '{' : return subTemplate();
				default:
					if ( c==delimiterStopChar ) {
						consume();
						scanningInsideExpr =false;
						return newToken(RDELIM);
					}
                    if ( isIDStartLetter(c) ) {
						Token id = mID();
						String name = id.getText();
						if ( name.equals("if") ) return newToken(IF);
						else if ( name.equals("endif") ) return newToken(ENDIF);
						else if ( name.equals("else") ) return newToken(ELSE);
						else if ( name.equals("elseif") ) return newToken(ELSEIF);
						else if ( name.equals("super") ) return newToken(SUPER);
						else if ( name.equals("true") ) return newToken(TRUE);
						else if ( name.equals("false") ) return newToken(FALSE);
						return id;
					}
					lexerError(startCharIndex, input.index());
					if (c==EOF) {
						return newToken(EOF_TYPE);
					}
					consume();
            }
        }
    }

    Token subTemplate() {
        // look for "{ args ID (',' ID)* '|' ..."
		subtemplateDepth++;
        int m = input.mark();
        int curlyStartChar = startCharIndex;
        int curlyLine = startLine;
        int curlyPos = startCharPositionInLine;
        List<Token> argTokens = new ArrayList<Token>();
        consume();
		Token curly = newTokenFromPreviousChar(LCURLY);
        WS();
        argTokens.add( mID() );
        WS();
        while ( c==',' ) {
			consume();
            argTokens.add( newTokenFromPreviousChar(COMMA) );
            WS();
            argTokens.add( mID() );
            WS();
        }
        WS();
        if ( c=='|' ) {
			consume();
            argTokens.add( newTokenFromPreviousChar(PIPE) );
            if ( isWS((char)c) ) consume(); // ignore a single whitespace after |
            //System.out.println("matched args: "+argTokens);
            for (Token t : argTokens) emit(t);
			input.release(m);
			scanningInsideExpr = false;
			startCharIndex = curlyStartChar; // reset state
			startLine = curlyLine;
			startCharPositionInLine = curlyPos;
			return curly;
		}
		input.rewind(m);
		startCharIndex = curlyStartChar; // reset state
		startLine = curlyLine;
        startCharPositionInLine = curlyPos;
		consume();
		scanningInsideExpr = false;
        return curly;
    }

    Token ESCAPE() {
		startCharIndex = input.index();
		startCharPositionInLine = input.getCharPositionInLine();
		consume(); // kill \\
		if ( c=='u') return UNICODE();
		String text = null;
        switch ( c ) {
            case '\\' : LINEBREAK(); return SKIP;
			case 'n'  : text = "\n"; break;
			case 't'  : text = "\t"; break;
			case ' '  : text = " "; break;
            default :
	            lexerError(startCharIndex, input.index());
				if ( c==EOF ) {
                    return SKIP;
                }
                consume();
				match(delimiterStopChar);
				return SKIP;
        }
        consume();
        match(delimiterStopChar);
        Token t = newToken(TEXT, text, input.getCharPositionInLine()-2);
        return t;
    }

    Token UNICODE() {
        consume();
        char[] chars = new char[4];
        if ( !isUnicodeLetter(c) ) {
	        lexerError(startCharIndex, input.index());
        }
        chars[0] = (char)c;
        consume();
        if ( !isUnicodeLetter(c) ) {
	        lexerError(startCharIndex, input.index());
        }
        chars[1] = (char)c;
        consume();
        if ( !isUnicodeLetter(c) ) {
	        lexerError(startCharIndex, input.index());
        }
        chars[2] = (char)c;
        consume();
        if ( !isUnicodeLetter(c) ) {
	        lexerError(startCharIndex, input.index());
        }
        chars[3] = (char)c;
        // ESCAPE kills >
        consume();
        char uc;
        try {
            uc = (char)Integer.parseInt(new String(chars), 16);
        }
        catch (NumberFormatException nfe) {
            uc = Character.MAX_VALUE;
        }
        Token t = newToken(TEXT, String.valueOf(uc), input.getCharPositionInLine()-6);
        match(delimiterStopChar);
        return t;
    }

    Token mTEXT() {
		boolean modifiedText = false;
        StringBuilder buf = new StringBuilder();
        while ( c != EOF && c != delimiterStartChar ) {
			if ( c=='\r' || c=='\n') break;
			if ( c=='}' && subtemplateDepth>0 ) break;
            if ( c=='\\' ) {
                if ( input.LA(2)=='\\' ) { // convert \\ to \
                    consume(); consume(); buf.append('\\');
                    modifiedText = true;
                    continue;
                }
                if ( input.LA(2)==delimiterStartChar ||
					 input.LA(2)=='}' )
				{
                    modifiedText = true;
                    consume(); // toss out \ char
                    buf.append(c); consume();
                }
                else {
                    buf.append(c);
                    consume();
                }
                continue;
            }
            buf.append(c);
            consume();
        }
        if ( modifiedText )	return newToken(TEXT, buf.toString());
        else return newToken(TEXT);
    }

    /** <pre>
	 *  ID  : ('a'..'z'|'A'..'Z'|'_'|'/')
	 *        ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')*
	 *      ;
	 *  </pre>
	 */
    Token mID() {
        // called from subTemplate; so keep resetting position during speculation
        startCharIndex = input.index();
        startLine = input.getLine();
        startCharPositionInLine = input.getCharPositionInLine();
        consume();
        while ( isIDLetter(c) ) {
            consume();
        }
        return newToken(ID);
    }

    /** <pre>
	 *  STRING : '"'
	 *           (   '\\' '"'
	 *           |   '\\' ~'"'
	 *           |   ~('\\'|'"')
	 *           )*
	 *           '"'
	 *         ;
	 * </pre>
	 */
    Token mSTRING() {
    	//{setText(getText().substring(1, getText().length()-1));}
        boolean sawEscape = false;
        StringBuilder buf = new StringBuilder();
        buf.append(c); consume();
        while ( c != '"' ) {
            if ( c=='\\' ) {
                sawEscape = true;
                consume();
				switch ( c ) {
					case 'n' : buf.append('\n'); break;
					case 'r' : buf.append('\r'); break;
					case 't' : buf.append('\t'); break;
                	default : buf.append(c); break;
				}
				consume();
                continue;
            }
            buf.append(c);
            consume();
			if ( c==EOF ) {
				lexerError(startCharIndex, input.index());
				break;
			}
        }
        buf.append(c);
        consume();
        if ( sawEscape ) return newToken(STRING, buf.toString());
        else return newToken(STRING);
    }

    void WS() {
        while ( c==' ' || c=='\t' || c=='\n' || c=='\r' ) consume();
    }

    Token COMMENT() {
        match('!');
        while ( !(c=='!' && input.LA(2)==delimiterStopChar) ) {
			if (c==EOF) {
				lexerError(startCharIndex, input.index());
				break;
			}
			consume();
		}
        consume(); consume(); // grab !>
		return newToken(COMMENT);
    }

    void LINEBREAK() {
        match('\\'); // only kill 2nd \ as ESCAPE() kills first one
        match(delimiterStopChar);
        while ( c==' ' || c=='\t' ) consume(); // scarf WS after <\\>
		if ( c==EOF ) {
			lexerError(startCharIndex, input.index());
			return;
		}
		if ( c=='\r' ) consume();
        match('\n');
        while ( c==' ' || c=='\t' ) consume(); // scarf any indent
    }

    public static boolean isIDStartLetter(int c) { return isIDLetter(c); }
	public static boolean isIDLetter(int c) { return c>='a'&&c<='z' || c>='A'&&c<='Z' || c>='0'&&c<='9' || c=='_' || c=='/'; }
    public static boolean isWS(int c) { return c==' ' || c=='\t' || c=='\n' || c=='\r'; }
    public static boolean isUnicodeLetter(int c) { return c>='a'&&c<='f' || c>='A'&&c<='F' || c>='0'&&c<='9'; }

    public int getChannel(int ttype) {
        switch ( ttype ) {
            case NEWLINE :
            case COMMENT :
            case INDENT :
                return Token.HIDDEN_CHANNEL;
	        default :
		        return Token.DEFAULT_CHANNEL;
        }
    }

    public Token newToken(int ttype) {
	    String text = input.substring(startCharIndex, input.index()-1);
	    return newToken(ttype, text, startCharIndex, input.index()-1, startLine, startCharPositionInLine);
	}

    public Token newTokenFromPreviousChar(int ttype) {
	    String text = input.substring(input.index()-1, input.index()-1);
	    return newToken(ttype, text, input.index()-1, input.index()-1, input.getLine(), input.getCharPositionInLine()-1);
    }

    public Token newToken(int ttype, String text, int pos) {
	    return newToken(ttype, text, startCharIndex, input.index()-1, input.getLine(), pos);
    }

	public Token newToken(int ttype, String text) {
		return newToken(ttype, text, startCharIndex, input.index()-1, startLine, startCharPositionInLine);
	}

	public Token newToken(int ttype, String text, int start, int stop, int line, int charPosInLine) {
		Pair<TokenSource, CharStream> source = new Pair<TokenSource, CharStream>(this, input);
		return tokenFactory.create(source, ttype, text, getChannel(ttype), start, stop, line, charPosInLine);
	}

    @Override
    public String getSourceName() {
        return "no idea";
    }

	public static String str(int c) {
		if ( c==EOF ) return "<EOF>";
		return String.valueOf((char)c);
	}

    // Satisfy Lexer interface to hook into syntax highlighter
    @Override
    public ATN getATN() {
        return null;
    }

    @Override
    public String[] getRuleNames() {
        return new String[0];
    }

    @Override
    public String getGrammarFileName() {
        return "STLexer";
    }
}
