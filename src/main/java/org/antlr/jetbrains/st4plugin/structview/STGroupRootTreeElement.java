package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;

public class STGroupRootTreeElement extends STGroupStructureViewTreeElement {

    public STGroupRootTreeElement(PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new STGroupRootItemPresentation((PsiFile) psiElement);
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return Arrays.stream(this.psiElement.getChildren())
                .filter(e -> e.getNode().getElementType() == getRuleElementType(STGParser.RULE_group))
                .findFirst()
                .map(group -> Arrays.stream(group.getChildren())
                        .filter(this::shouldShowInStructureView)
                        .map(e -> new STGroupTemplateDefTreeElement((ASTWrapperPsiElement) e))
                        .toArray(TreeElement[]::new))
                .orElse(TreeElement.EMPTY_ARRAY);
    }

    private boolean shouldShowInStructureView(@NotNull PsiElement child) {
        return child.getNode().getElementType() == getRuleElementType(STGParser.RULE_template)
                || child.getNode().getElementType() == getRuleElementType(STGParser.RULE_dict);
    }
}
