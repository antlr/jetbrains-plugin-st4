package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
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

public class STSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey STGroup_TEMPLATE_TEXT =
            createTextAttributesKey("STGroup_TEMPLATE_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey ST_ID = STGroupSyntaxHighlighter.STGroup_TEMPLATE_NAME;

    private static final List<IElementType> KEYWORDS = Stream.of(
            STLexer.IF, STLexer.ELSE, /* TODO STLexer.REGION_END, */ STLexer.TRUE,
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
            key = DefaultLanguageHighlighterColors.KEYWORD;
        } else if (STTokenTypes.getTokenElementType(STLexer.ID).equals(tokenType)) {
            key = ST_ID;
        } else if (STTokenTypes.getTokenElementType(STLexer.STRING).equals(tokenType)
                || STTokenTypes.getTokenElementType(STLexer.TEXT).equals(tokenType)) {
            key = STGroup_TEMPLATE_TEXT;
        } else if (STTokenTypes.getTokenElementType(STLexer.TMPL_COMMENT).equals(tokenType)) {
            key = DefaultLanguageHighlighterColors.BLOCK_COMMENT;
//        } else if (STTokenTypes.getTokenElementType(STLexer.SUB_ERR_CHAR).equals(tokenType)) {
//            key = HighlighterColors.BAD_CHARACTER;
        } else {
            return STGroupSyntaxHighlighter.NO_ATTRIBUTES;
        }

        return new TextAttributesKey[]{key};
    }
}
