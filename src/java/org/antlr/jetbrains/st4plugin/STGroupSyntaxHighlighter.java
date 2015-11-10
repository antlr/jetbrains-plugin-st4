package org.antlr.jetbrains.st4plugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.SimpleAntlrLexerAdapter;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.jetbrains.annotations.NotNull;

/** A highlighter is really just a mapping from token type to
 *  some text attributes using {@link #getTokenHighlights(IElementType)}.
 *  The reason that it returns an array, TextAttributesKey[], is
 *  that you might want to mix the attributes of a few known highlighters.
 *  A {@link TextAttributesKey} is just a name for that a theme
 *  or IDE skin can set. For example, {@link com.intellij.openapi.editor.DefaultLanguageHighlighterColors#KEYWORD}
 *  is the key that maps to what identifiers look like in the editor.
 *  To change it, see dialog: Editor > Colors & Fonts > Language Defaults.
 *
 *  From <a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html">doc</a>:
 *  "The mapping of the TextAttributesKey to specific attributes used
 *  in an editor is defined by the EditorColorsScheme class, and can
 *  be configured by the user if the plugin provides an appropriate
 *  configuration interface.
 *  ...
 *  The syntax highlighter returns the {@link TextAttributesKey}
 * instances for each token type which needs special highlighting.
 * For highlighting lexer errors, the standard TextAttributesKey
 * for bad characters HighlighterColors.BAD_CHARACTER can be used."
 */
public class STGroupSyntaxHighlighter extends SyntaxHighlighterBase {
	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		System.out.println("new lexer");
		STGLexer lexer = new STGLexer(null);
		return new SimpleAntlrLexerAdapter(STGroupLanguage.INSTANCE, lexer);
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		System.out.println(tokenType);
		return new TextAttributesKey[]{DefaultLanguageHighlighterColors.LABEL};
	}
}
