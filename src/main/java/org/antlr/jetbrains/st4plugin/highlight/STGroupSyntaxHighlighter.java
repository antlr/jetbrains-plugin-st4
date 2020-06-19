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

    public static final TextAttributesKey STGroup_TEMPLATE_NAME =
            createTextAttributesKey("STGroup_TEMPLATE_NAME", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("STGroup_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey DOC_COMMENT =
            createTextAttributesKey("STGroup_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("STGroup_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{LINE_COMMENT, DOC_COMMENT, BLOCK_COMMENT};

    private static final List<IElementType> KEYWORDS = Stream.of(
            STGLexer.DELIMITERS, STGLexer.IMPORT, STGLexer.DEFAULT, STGLexer.KEY, STGLexer.VALUE,
            STGLexer.FIRST, STGLexer.LAST, STGLexer.REST, STGLexer.TRUNC, STGLexer.STRIP,
            STGLexer.TRIM, STGLexer.LENGTH, STGLexer.STRLEN, STGLexer.REVERSE, STGLexer.GROUP,
            STGLexer.WRAP, STGLexer.ANCHOR, STGLexer.SEPARATOR
    ).map(STGroupTokenTypes::getTokenElementType).collect(Collectors.toList());

    private static final List<IElementType> COMMENTS = Stream.of(
            STGLexer.DOC_COMMENT, STGLexer.LINE_COMMENT, STGLexer.BLOCK_COMMENT
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
        if (COMMENTS.contains(tokenType)) {
            return COMMENT_KEYS;
        } else if (getTokenElementType(STGLexer.STRING).equals(tokenType)) {
            return new TextAttributesKey[]{DefaultLanguageHighlighterColors.STRING};
        } else if (getTokenElementType(STGLexer.ID).equals(tokenType)) {
            return new TextAttributesKey[]{STGroup_TEMPLATE_NAME};
        } else if (KEYWORDS.contains(tokenType)) {
            return new TextAttributesKey[]{DefaultLanguageHighlighterColors.KEYWORD};
        } else if (tokenType instanceof TokenIElementType && ((TokenIElementType) tokenType).getANTLRTokenType() == Token.INVALID_TYPE) {
            return new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};
        }
        return NO_ATTRIBUTES;
    }
}
