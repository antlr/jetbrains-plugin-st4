package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;
import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

public class STGroupTemplateDefItemPresentation implements ItemPresentation {

    private final ASTWrapperPsiElement psiElement;

    public STGroupTemplateDefItemPresentation(ASTWrapperPsiElement psiElement) {
        this.psiElement = psiElement;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        ASTNode id = psiElement.getNode().findChildByType(getTokenElementType(STGLexer.ID));

        if (id == null) {
            return null;
        }

        StringBuilder text = new StringBuilder(id.getText());

        ASTNode args = psiElement.getNode().findChildByType(getRuleElementType(STGParser.RULE_formalArgs));

        if (args != null) {
            text.append('(');

            ASTNode[] argList = args.getChildren(TokenSet.create(getRuleElementType(STGParser.RULE_formalArg)));

            text.append(Arrays.stream(argList).map(ASTNode::getText).collect(Collectors.joining(", ")));

            text.append(')');
        }

        return text.toString();
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        if (psiElement.getNode().getElementType() == getRuleElementType(STGParser.RULE_dict)) {
            return Icons.DICT;
        }

        ASTNode content = psiElement.getNode().findChildByType(getRuleElementType(STGParser.RULE_templateContent));
        IElementType elementType = null;

        if (content != null) {
            ASTNode firstChild = content.getFirstChildNode();

            if (firstChild != null) {
                elementType = firstChild.getElementType();
            }
        }

        if (elementType == getTokenElementType(STGLexer.STRING)) {
            return Icons.STRING;
        } else if (elementType == getTokenElementType(STGLexer.BIGSTRING_NO_NL)) {
            return Icons.BIGSTRING_NONL;
        }

        return Icons.BIGSTRING;
    }
}
