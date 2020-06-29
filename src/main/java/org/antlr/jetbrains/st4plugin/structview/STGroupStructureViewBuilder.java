package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class STGroupStructureViewBuilder extends TreeBasedStructureViewBuilder {

    private final PsiFile psiFile;

    public STGroupStructureViewBuilder(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    @NotNull
    @Override
    public StructureViewModel createStructureViewModel(final Editor editor) {
        return new STGroupStructureViewModel(psiFile);
    }
}
