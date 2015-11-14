package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;

public class STGroupStructureViewModel
	extends StructureViewModelBase implements
    StructureViewModel.ElementInfoProvider
{
	public STGroupStructureViewModel(PsiFile psiFile, Editor editor) {
		super(psiFile, editor, new STGroupStructureViewElement());
	}

	@Override
	public boolean isAlwaysLeaf(StructureViewTreeElement element) {
		return false;
	}

	@Override
	public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
		return true;
	}
}
