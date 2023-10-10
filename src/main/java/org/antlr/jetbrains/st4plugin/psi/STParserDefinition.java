package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class STParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(STLanguage.INSTANCE);

    public STParserDefinition() {
        STTokenTypes.initIElementTypes();
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ANTLRLexerAdaptor(STLanguage.INSTANCE, new STLexer(null)) {
            @Override
            public void advance() {
                Token before = super.getCurrentToken();
                super.advance();
                Token after = super.getCurrentToken();

                if (before != null && after != null) {
                    if (after.getStartIndex() != before.getStopIndex() + 1) {
                        System.err.println("Lexer gap between tokens\n" + before + "\n\nand\n\n" + after);
                    }
                }
            }
        };
    }

    @Override
    public PsiParser createParser(Project project) {
        return new ANTLRParserAdaptor(STLanguage.INSTANCE, new STParser(null)) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                return ((STParser) parser).template();
            }
        };
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return PSIElementTypeFactory.createTokenSet(
                STLanguage.INSTANCE,
                STLexer.TMPL_COMMENT
        );
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return PSIElementTypeFactory.createTokenSet(
                STLanguage.INSTANCE,
                STLexer.STRING
        );
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new ASTWrapperPsiElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new STFile(viewProvider);
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return PSIElementTypeFactory.createTokenSet(
                STLanguage.INSTANCE,
                STLexer.VERT_WS,
                STLexer.HORZ_WS
        );
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
