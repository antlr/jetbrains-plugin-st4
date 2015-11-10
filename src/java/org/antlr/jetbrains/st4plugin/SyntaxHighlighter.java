package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public class SyntaxHighlighter {
	protected Lexer lexer;
	protected TextAttributesKey[] tokenTypeToAttrMap;

	public SyntaxHighlighter(Lexer lexer) {
		this.lexer = lexer;
		if ( lexer!=null ) {
			String[] tokenNames = lexer.getRuleNames(); // lexer rules are tokens
			tokenTypeToAttrMap = new TextAttributesKey[tokenNames.length+1];
		}
	}

	public void highlight(Editor editor) {
		Document doc = editor.getDocument();
		String docText = doc.getCharsSequence().toString();
		MarkupModel markupModel = editor.getMarkupModel();

		// tokenize
		ANTLRInputStream input = new ANTLRInputStream(docText);
		lexer.setInputStream(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		System.out.println("tokens: "+tokens.getTokens().toString());
		final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();
		final EditorColorsScheme scheme =
			editorColorsManager.getScheme(EditorColorsScheme.DEFAULT_SCHEME_NAME);
		for (int i=0; i<tokens.size(); i++) {
			Token t = tokens.get(i);
			TextAttributes attr = null;
			if ( t.getType()>=Token.MIN_USER_TOKEN_TYPE ) {
				TextAttributesKey key = tokenTypeToAttrMap[t.getType()];
				if ( key!=null ) {
					attr = scheme.getAttributes(key);
					markupModel.addRangeHighlighter(
						t.getStartIndex(),
						t.getStopIndex()+1,
						HighlighterLayer.SYNTAX, // layer
						attr,
						HighlighterTargetArea.EXACT_RANGE);
				}
			}
		}
	}

	public void setAttributes(int tokenType, TextAttributesKey attr) {
		tokenTypeToAttrMap[tokenType] = attr;
	}

	public void setAttributes(int[] tokenTypes, TextAttributesKey attr) {
		for (int t : tokenTypes) {
			tokenTypeToAttrMap[t] = attr;
		}
	}
}
