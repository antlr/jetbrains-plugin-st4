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
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class STGroupParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(STGroupLanguage.INSTANCE);

    public STGroupParserDefinition() {
        STGroupTokenTypes.initIElementTypes();
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        STGLexer lexer = new STGLexer(null);
        return new ANTLRLexerAdaptor(STGroupLanguage.INSTANCE, lexer);
    }

    @Override
    public PsiParser createParser(Project project) {
        return new ANTLRParserAdaptor(STGroupLanguage.INSTANCE, new STGParser(null)) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                return ((STGParser) parser).group();
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
                STGroupLanguage.INSTANCE,
                STGLexer.COMMENT,
                STGLexer.LINE_COMMENT
        );
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return PSIElementTypeFactory.createTokenSet(
                STGroupLanguage.INSTANCE,
                STGLexer.STRING
        );
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        if (node.getElementType() == STGroupTokenTypes.getRuleElementType(STGParser.RULE_templateContent)) {
            return new TemplateContentElement(node);
        }

        return new ASTWrapperPsiElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new STGroupFile(viewProvider);
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return PSIElementTypeFactory.createTokenSet(
                STGroupLanguage.INSTANCE,
                STGLexer.WS
        );
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
