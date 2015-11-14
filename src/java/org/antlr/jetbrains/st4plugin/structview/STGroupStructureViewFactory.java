package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class STGroupStructureViewFactory implements PsiStructureViewFactory {
	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
		return new TreeBasedStructureViewBuilder() {
			@NotNull
			@Override
			public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
				VirtualFile grammarFile = psiFile.getVirtualFile();
				if ( grammarFile==null || !grammarFile.getName().endsWith(".stg") ) {
//					return new StructureViewModelBase(psiFile, new DummyViewTreeElement(psiFile));
				}
				return new STGroupStructureViewModel(psiFile, editor);
			}
		};
	}
}
