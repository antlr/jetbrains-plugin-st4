package org.antlr.jetbrains.st4plugin.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.jetbrains.st4plugin.psi.STGroupFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;
import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

public class STGroupFoldingBuilder extends CustomFoldingBuilder {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors,
                                            @NotNull PsiElement root,
                                            @NotNull Document document,
                                            boolean quick) {
        if (!(root instanceof STGroupFile)) {
            return;
        }

        foldTemplates(descriptors, root);
        foldDicts(descriptors, root);
        foldComments(descriptors, root);
    }

    private void foldTemplates(List<FoldingDescriptor> descriptors, PsiElement root) {
        PsiTreeUtil.processElements(root, element -> {
            if (element.getNode().getElementType() == getRuleElementType(STGParser.RULE_template)) {
                ASTNode bigString = element.getNode().findChildByType(getTokenElementType(STGLexer.BIGSTRING));

                if (bigString != null) {
                    descriptors.add(new FoldingDescriptor(bigString, bigString.getTextRange()));
                }
            }

            return true;
        });
    }

    private void foldDicts(List<FoldingDescriptor> descriptors, PsiElement root) {
        PsiTreeUtil.processElements(root, element -> {
            if (element.getNode().getElementType() == getRuleElementType(STGParser.RULE_dict)) {
                ASTNode lbrack = element.getNode().findChildByType(getTokenElementType(STGLexer.LBRACK));
                ASTNode rbrack = element.getNode().findChildByType(getTokenElementType(STGLexer.RBRACK));

                if (lbrack != null && rbrack != null) {
                    TextRange range = lbrack.getTextRange().union(rbrack.getTextRange());
                    descriptors.add(new FoldingDescriptor(element, range));
                }
            }

            return true;
        });
    }

    private void foldComments(List<FoldingDescriptor> descriptors, PsiElement root) {
        PsiTreeUtil.processElements(root, element -> {
            if (element.getNode().getElementType() == getTokenElementType(STGLexer.DOC_COMMENT)
                    || element.getNode().getElementType() == getTokenElementType(STGLexer.BLOCK_COMMENT)
                    || element.getNode().getElementType() == getTokenElementType(STGLexer.TMPL_COMMENT)) {
                descriptors.add(new FoldingDescriptor(element, element.getTextRange()));
            }

            return true;
        });

    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        if (node.getElementType() == getTokenElementType(STGLexer.BIGSTRING)) {
            return "<<...>>";
        } else if (node.getElementType() == getRuleElementType(STGParser.RULE_dict)) {
            return "[...]";
        } else if (node.getElementType() == getTokenElementType(STGLexer.DOC_COMMENT)) {
            return "/**...*/";
        } else if (node.getElementType() == getTokenElementType(STGLexer.BLOCK_COMMENT)) {
            return "/*...*/";
        } else if (node.getElementType() == getTokenElementType(STGLexer.TMPL_COMMENT)) {
            return "<!...!>";
        }
        return "...";
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
