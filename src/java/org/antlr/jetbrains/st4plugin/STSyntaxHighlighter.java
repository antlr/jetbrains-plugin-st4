package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Key;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

public class STSyntaxHighlighter extends SyntaxHighlighter {
	public static final Key<Token> TEMPLATE_HIGHLIGHT = Key.create("TEMPLATE_HIGHLIGHT");
	@NotNull
	@Override
	public TextAttributesKey[] getAttributesKey(int tokenType) {
		TextAttributesKey key;
		switch ( tokenType ) {
			case STLexer.IF :
			case STLexer.ELSE :
			case STLexer.REGION_END :
			case STLexer.TRUE :
			case STLexer.FALSE :
			case STLexer.ELSEIF :
			case STLexer.ENDIF :
			case STLexer.SUPER :
				key = DefaultLanguageHighlighterColors.KEYWORD;
				break;
			case STLexer.TEXT :
				key = DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR;
				break;
			case STLexer.ID :
				key = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
				break;
			case STLexer.STRING :
				key = DefaultLanguageHighlighterColors.STRING;
				break;
			case STLexer.COMMENT :
				key = DefaultLanguageHighlighterColors.LINE_COMMENT;
				break;
			default:
				key = DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR;
				break;
		}
		return new TextAttributesKey[] {key, DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR};
	}

	@NotNull
	@Override
	public Key<Token> getHighlightCategoryKey() {
		return TEMPLATE_HIGHLIGHT;
	}

	@NotNull
	@Override
	public CommonTokenStream tokenize(String text) {
		STLexer lexer = new STLexer(text);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens;
	}
}
