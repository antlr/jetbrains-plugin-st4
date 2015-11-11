package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Key;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public static final Key<Token> GROUP_HIGHLIGHT = Key.create("GROUP_HIGHLIGHT");
	public CommonTokenStream tokens;

	@NotNull
	@Override
	public CommonTokenStream tokenize(String text) {
		ANTLRInputStream input = new ANTLRInputStream(text);
		STGLexer lexer = new STGLexer(input);
		tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens;
	}

	@Override
	@NotNull
	public TextAttributesKey[] getAttributesKey(CommonTokenStream tokens, int tokenIndex) {
		TextAttributesKey[] key = getAttributesKey(tokens.get(tokenIndex).getType());
		if ( key == NO_ATTR ) return NO_ATTR;
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
			return new TextAttributesKey[] {DefaultLanguageHighlighterColors.INSTANCE_FIELD};
		}
		return key;
	}

	@NotNull
	@Override
	public Key<Token> getHighlightCategoryKey() {
		return GROUP_HIGHLIGHT;
	}

	@NotNull
	public TextAttributesKey[] getAttributesKey(int tokenType) {
		switch ( tokenType ) {
			case STGLexer.DOC_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.DOC_COMMENT};
			case STGLexer.LINE_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.LINE_COMMENT};
			case STGLexer.BLOCK_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.BLOCK_COMMENT};
//			case STGLexer.STRING :
//				return DefaultLanguageHighlighterColors.STRING;
//			case STGLexer.BIGSTRING :
//				return DefaultLanguageHighlighterColors.STRING;
			case STGLexer.ID :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.IDENTIFIER};

			case STGLexer.DELIMITERS :
			case STGLexer.IMPORT :
			case STGLexer.DEFAULT :
			case STGLexer.KEY :
			case STGLexer.VALUE :
			case STGLexer.FIRST :
			case STGLexer.LAST :
			case STGLexer.REST :
			case STGLexer.TRUNC :
			case STGLexer.STRIP :
			case STGLexer.TRIM :
			case STGLexer.LENGTH :
			case STGLexer.STRLEN :
			case STGLexer.REVERSE :
			case STGLexer.GROUP :
			case STGLexer.WRAP :
			case STGLexer.ANCHOR :
			case STGLexer.SEPARATOR :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.KEYWORD};
			default :
				return NO_ATTR;
		}
	}
}
