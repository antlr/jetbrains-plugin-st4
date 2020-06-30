package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.antlr.jetbrains.st4plugin.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class STGroupStructureViewTreeElement
        implements StructureViewTreeElement, ItemPresentation, SortableTreeElement {

    protected NavigatablePsiElement psiElement;

    public STGroupStructureViewTreeElement(NavigatablePsiElement psiElement) {
        this.psiElement = psiElement;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return Icons.STG_FILE;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "TODO";
    }

    @Override
    public void navigate(boolean requestFocus) {
        psiElement.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Override
    public NavigatablePsiElement getValue() {
        return psiElement;
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return getPresentation().getPresentableText();
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return EMPTY_ARRAY;
    }
}
