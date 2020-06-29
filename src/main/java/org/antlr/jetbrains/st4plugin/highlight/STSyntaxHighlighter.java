package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.psi.STTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

public class STSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey STGroup_TEMPLATE_TEXT =
            createTextAttributesKey("STGroup_TEMPLATE_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);

    private static final List<IElementType> KEYWORDS = Stream.of(
            STLexer.IF, STLexer.ELSE, STLexer.END, STLexer.TRUE,
            STLexer.FALSE, STLexer.ELSEIF, STLexer.ENDIF, STLexer.SUPER
    ).map(STTokenTypes::getTokenElementType).collect(Collectors.toList());

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ANTLRLexerAdaptor(STLanguage.INSTANCE, new STLexer(null));
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey key;

        if (KEYWORDS.contains(tokenType)) {
            key = STGroupSyntaxHighlighter.KEYWORD;
        } else if (getTokenElementType(STLexer.STRING).equals(tokenType)
                || getTokenElementType(STLexer.TEXT).equals(tokenType)) {
            key = STGroup_TEMPLATE_TEXT;
        } else if (getTokenElementType(STLexer.TMPL_COMMENT).equals(tokenType)) {
            key = STGroupSyntaxHighlighter.BLOCK_COMMENT;
        } else if (getTokenElementType(STLexer.SUB_ERR_CHAR).equals(tokenType)) {
            key = HighlighterColors.BAD_CHARACTER;
        } else if (getTokenElementType(STLexer.ESCAPE).equals(tokenType)) {
            key = DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE;
        } else {
            return STGroupSyntaxHighlighter.NO_ATTRIBUTES;
        }

        return new TextAttributesKey[]{key};
    }
}
