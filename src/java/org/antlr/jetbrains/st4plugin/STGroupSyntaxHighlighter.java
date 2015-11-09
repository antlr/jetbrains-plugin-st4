package org.antlr.jetbrains.st4plugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.SimpleAntlrLexerAdapter;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.jetbrains.annotations.NotNull;

public class STGroupSyntaxHighlighter extends SyntaxHighlighterBase {
	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		STGLexer lexer = new STGLexer(null);
		return new SimpleAntlrLexerAdapter(STGroupLanguage.INSTANCE, lexer);
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		return new TextAttributesKey[0];
	}
}
