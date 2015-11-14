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

/** Track all parser issues found in an input file for use by the syntax
 *  highlighter.
 */
public class ParserErrorListener extends BaseErrorListener {
	public final List<Issue> issues = new ArrayList<Issue>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
//		System.out.println("parse error: " + offendingSymbol);
		issues.add(new Issue(msg, (Token)offendingSymbol));
	}
}
