package org.antlr.jetbrains.st4plugin.parsing;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParsingResult {
	public Parser parser;
	public ParserRuleContext tree;
	public ParserErrorListener syntaxErrorListener;

	public ParsingResult(Parser parser, ParserRuleContext tree, ParserErrorListener syntaxErrorListener) {
		this.parser = parser;
		this.tree = tree;
		this.syntaxErrorListener = syntaxErrorListener;
	}
}
