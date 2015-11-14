package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;

public class STGroupStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public void navigate(boolean requestFocus) {

	}

	@Override
	public boolean canNavigate() {
		return false;
	}

	@Override
	public boolean canNavigateToSource() {
		return false;
	}

	@Override
	public String getAlphaSortKey() {
		return null;
	}

	@Override
	public ItemPresentation getPresentation() {
		return null;
	}

	@Override
	public TreeElement[] getChildren() {
		return new TreeElement[0];
	}
}
