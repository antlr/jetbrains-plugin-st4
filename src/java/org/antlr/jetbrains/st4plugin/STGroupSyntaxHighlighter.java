package org.antlr.jetbrains.st4plugin;

import org.antlr.jetbrains.st4plugin.parsing.STGLexer;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public STGroupSyntaxHighlighter() {
		super(new STGLexer(null));
	}
}
