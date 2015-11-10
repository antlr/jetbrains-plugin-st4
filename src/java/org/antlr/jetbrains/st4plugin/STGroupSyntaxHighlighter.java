package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public STGroupSyntaxHighlighter() {
		super(new STGLexer(null));
		setAttributes(STGLexer.DOC_COMMENT, DefaultLanguageHighlighterColors.DOC_COMMENT);
		setAttributes(STGLexer.LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);
		setAttributes(STGLexer.BLOCK_COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
		setAttributes(STGLexer.STRING, DefaultLanguageHighlighterColors.STRING);
		setAttributes(STGLexer.BIGSTRING, DefaultLanguageHighlighterColors.STRING);

		setAttributes(new int[] {
			              STGLexer.DELIMITERS,
			              STGLexer.IMPORT,
			              STGLexer.DEFAULT,
			              STGLexer.KEY,
			              STGLexer.VALUE,
			              STGLexer.FIRST,
			              STGLexer.LAST,
			              STGLexer.REST,
			              STGLexer.TRUNC,
			              STGLexer.STRIP,
			              STGLexer.TRIM,
			              STGLexer.LENGTH,
			              STGLexer.STRLEN,
			              STGLexer.REVERSE,
			              STGLexer.GROUP,
			              STGLexer.WRAP,
			              STGLexer.ANCHOR,
			              STGLexer.SEPARATOR,
		              },
		              DefaultLanguageHighlighterColors.KEYWORD);
	}
}
