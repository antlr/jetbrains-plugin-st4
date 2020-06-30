package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class STGroupStructureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider {

    public STGroupStructureViewModel(PsiFile psiFile) {
        super(psiFile, new STGroupRootTreeElement(psiFile));
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof STGroupTemplateDefTreeElement;
    }
}
