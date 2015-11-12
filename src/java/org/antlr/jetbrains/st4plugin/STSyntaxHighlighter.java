package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.ui.JBColor;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class STSyntaxHighlighter extends SyntaxHighlighter {
	public static final TextAttributesKey TEMPLATE_TEXT =
		createTextAttributesKey("TEMPLATE_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
	static {
		TEMPLATE_TEXT.getDefaultAttributes().setForegroundColor(JBColor.gray);
	}

	protected STLexer lexer = null;

	public STSyntaxHighlighter(Editor editor) {
		super(editor);
	}

	@NotNull
	@Override
	public CommonTokenStream tokenize(String text) {
		lexer = new STLexer(text);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens;
	}

	@Override
	protected void highlightToken(Token t, int start) {
		if ( t.getType()==STLexer.STRING ) { // don't highlight start/end quotes as emitted text
			CommonToken t2 = new CommonToken(t);
			t2.setStartIndex(t.getStartIndex()+1);
			t2.setStopIndex(t.getStopIndex()-1);
			super.highlightToken(t2, start);
		}
		else {
			super.highlightToken(t, start);
		}
	}

	@NotNull
	@Override
	public TextAttributesKey[] getAttributesKey(int tokenType) {
		TextAttributesKey key;
		switch ( tokenType ) {
			case STLexer.IF:
			case STLexer.ELSE:
			case STLexer.REGION_END:
			case STLexer.TRUE:
			case STLexer.FALSE:
			case STLexer.ELSEIF:
			case STLexer.ENDIF:
			case STLexer.SUPER:
				key = DefaultLanguageHighlighterColors.KEYWORD;
				break;
			case STLexer.STRING:
			case STLexer.TEXT:
				key = TEMPLATE_TEXT;
				return new TextAttributesKey[]{key, DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR};
			case STLexer.ID:
				key = DefaultLanguageHighlighterColors.LOCAL_VARIABLE;
				break;
			case STLexer.COMMENT:
				key = DefaultLanguageHighlighterColors.LINE_COMMENT;
				break;
			case STLexer.ERROR_TYPE :
				key = HighlighterColors.BAD_CHARACTER;
				break;
			default:
//				key = DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR;
//				break;
				return NO_ATTR;
		}
		return new TextAttributesKey[]{key};//, DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR};
	}
}
