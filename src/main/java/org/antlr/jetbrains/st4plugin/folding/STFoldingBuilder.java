package org.antlr.jetbrains.st4plugin.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.jetbrains.st4plugin.psi.STFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getRuleElementType;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

public class STFoldingBuilder extends CustomFoldingBuilder {
    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors,
                                            @NotNull PsiElement root,
                                            @NotNull Document document,
                                            boolean quick) {
        if (!(root instanceof STFile)) {
            return;
        }

        foldRegions(descriptors, root);
        foldIf(descriptors, root);
    }

    private void foldRegions(List<FoldingDescriptor> descriptors, PsiElement root) {
        PsiTreeUtil.processElements(root, element -> {
            if (element.getNode().getElementType() == getRuleElementType(STParser.RULE_region)) {
                descriptors.add(new FoldingDescriptor(element, element.getTextRange()));
            }

            return true;
        });
    }

    private void foldIf(List<FoldingDescriptor> descriptors, PsiElement root) {
        PsiTreeUtil.processElements(root, element -> {
            if (element.getNode().getElementType() == getRuleElementType(STParser.RULE_ifstat)) {
                descriptors.add(new FoldingDescriptor(element, element.getTextRange()));
            }

            return true;
        });
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        if (node.getElementType() == getRuleElementType(STParser.RULE_region)) {
            ASTNode id = node.findChildByType(getTokenElementType(STLexer.ID));

            return "<@" + (id == null ? "??" : id.getText()) + ">";
        } else if (node.getElementType() == getRuleElementType(STParser.RULE_ifstat)) {
            ASTNode lDelim = node.findChildByType(getTokenElementType(STLexer.LDELIM));
            ASTNode rDelim = node.findChildByType(getTokenElementType(STLexer.RDELIM));

            if (lDelim != null && rDelim != null) {
                int startOffset = node.getStartOffset();
                return node.getText().substring(lDelim.getStartOffset() - startOffset, rDelim.getStartOffset() + rDelim.getTextLength() - startOffset);
            } else {
                return "<if>";
            }
        }

        return "...";
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
