package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

public class STGroupSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey TEMPLATE_NAME =
            createTextAttributesKey("STGroup_TEMPLATE_NAME", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("STGroup_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("STGroup_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("STGroup_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("STGroup_STRING", DefaultLanguageHighlighterColors.STRING);

    private static final List<IElementType> KEYWORDS = Stream.of(
            STGLexer.DELIMITERS, STGLexer.IMPORT, STGLexer.DEFAULT, STGLexer.KEY, STGLexer.GROUP
    ).map(STGroupTokenTypes::getTokenElementType).collect(Collectors.toList());

    public static final TextAttributesKey[] NO_ATTRIBUTES = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ANTLRLexerAdaptor(STGroupLanguage.INSTANCE, new STGLexer(null));
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (getTokenElementType(STGLexer.COMMENT).equals(tokenType)) {
            return new TextAttributesKey[]{BLOCK_COMMENT};
        } else if (getTokenElementType(STGLexer.LINE_COMMENT).equals(tokenType)) {
            return new TextAttributesKey[]{LINE_COMMENT};
        } else if (getTokenElementType(STGLexer.STRING).equals(tokenType)) {
            return new TextAttributesKey[]{STRING};
        } else if (getTokenElementType(STGLexer.ID).equals(tokenType)) {
            return new TextAttributesKey[]{TEMPLATE_NAME};
        } else if (KEYWORDS.contains(tokenType)) {
            return new TextAttributesKey[]{KEYWORD};
        } else if (tokenType instanceof TokenIElementType && ((TokenIElementType) tokenType).getANTLRTokenType() == Token.INVALID_TYPE) {
            return new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};
        }
        return NO_ATTRIBUTES;
    }
}
