package org.antlr.jetbrains.st4plugin.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;

/** Track all lexical issues found in an input file for use by the syntax
 *  highlighter.
 */
public class LexerErrorListener extends BaseErrorListener {
	public final List<Issue> issues = new ArrayList<Issue>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
		if ( offendingSymbol==null ) {
			final Lexer lexer = (Lexer) recognizer;
			int i = lexer.getCharIndex();
			final int n = lexer.getInputStream().size();
			if (i >= n) {
				i = n - 1;
			}
			final String text = lexer.getInputStream().getText(new Interval(i, i));
			CommonToken t = (CommonToken) lexer.getTokenFactory().create(Token.INVALID_TYPE, text);
			t.setStartIndex(i);
			t.setStopIndex(i);
			t.setLine(line);
			t.setCharPositionInLine(charPositionInLine);
			offendingSymbol = t;
		}
//		System.out.println("lex error: " + offendingSymbol);
		issues.add(new Issue(msg, (Token)offendingSymbol));
	}
}
