package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STGroupStructureViewTreeElement
	implements StructureViewTreeElement, ItemPresentation
{
	protected ParseTree node;

	public STGroupStructureViewTreeElement(ParseTree node) {
		this.node = node;
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return Icons.STG_FILE;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return node.getPayload().toString();
	}

	@Nullable
	@Override
	public String getLocationString() {
		return null;
	}

	@Override
	public Object getValue() {
		return node;
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

	@NotNull
	@Override
	public ItemPresentation getPresentation() {
		return new STGroupItemPresentation(node);
	}

	@NotNull
	@Override
	public TreeElement[] getChildren() {
		return EMPTY_ARRAY;
	}
}
