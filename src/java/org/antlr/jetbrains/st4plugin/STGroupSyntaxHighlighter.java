package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public STGroupSyntaxHighlighter() {
		super(new STGLexer(null));
		setAttributes();
	}

	protected void setAttributes() {
		setAttributesKey(STGLexer.DOC_COMMENT, DefaultLanguageHighlighterColors.DOC_COMMENT);
		setAttributesKey(STGLexer.LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);
		setAttributesKey(STGLexer.BLOCK_COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
		setAttributesKey(STGLexer.STRING, DefaultLanguageHighlighterColors.STRING);
		setAttributesKey(STGLexer.BIGSTRING, DefaultLanguageHighlighterColors.STRING);
		setAttributesKey(STGLexer.ID, DefaultLanguageHighlighterColors.IDENTIFIER);

		setAttributesKey(
			new int[]{
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

	@Override
	public TextAttributesKey getAttributesKey(CommonTokenStream tokens, int tokenIndex) {
		TextAttributesKey key = super.getAttributesKey(tokens, tokenIndex);
		if ( tokenIndex==tokens.size()-1 ) return key; // at end
		List<Token> afterTokens = tokens.getHiddenTokensToRight(tokenIndex, Token.DEFAULT_CHANNEL);
		if ( afterTokens==null || afterTokens.size()==0 ) {
			return key;
		}
		Token after = afterTokens.get(0);
		Token t = tokens.get(tokenIndex);
		if ( t.getType()==STGLexer.ID &&
			(after.getType()==STGLexer.TMPL_ASSIGN||after.getType()==STGLexer.LPAREN) )
		{
			return DefaultLanguageHighlighterColors.INSTANCE_FIELD;
		}
		return key;
	}
}
