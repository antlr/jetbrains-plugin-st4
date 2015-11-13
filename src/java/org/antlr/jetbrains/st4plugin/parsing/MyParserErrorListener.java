package org.antlr.jetbrains.st4plugin.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/** Track all issues found in an input file for use by the syntax
 *  highlighter.
 */
public class MyParserErrorListener extends BaseErrorListener {
	public static class Issue {
		public String annotation;
		public Token offendingToken;

		public Issue(String annotation, Token offendingToken) {
			this.annotation = annotation;
			this.offendingToken = offendingToken;
		}
	}

	public final List<Issue> issues = new ArrayList<Issue>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
	                        Object offendingSymbol,
	                        int line,
	                        int charPositionInLine,
	                        String msg,
	                        RecognitionException e)
	{
		System.out.println("error: "+offendingSymbol);
		issues.add(new Issue(msg, (Token)offendingSymbol));
	}
}
