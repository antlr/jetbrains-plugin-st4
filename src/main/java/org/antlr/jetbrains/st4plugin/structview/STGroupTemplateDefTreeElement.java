package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;

public class STGroupTemplateDefTreeElement extends STGroupStructureViewTreeElement {
    public STGroupTemplateDefTreeElement(ASTWrapperPsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new STGroupTemplateDefItemPresentation((ASTWrapperPsiElement) psiElement);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public void navigate(boolean requestFocus) {
        psiElement.navigate(requestFocus);
    }
}
