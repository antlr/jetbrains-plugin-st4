package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class STGroupStructureViewTreeElement
	implements StructureViewTreeElement, ItemPresentation, SortableTreeElement
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
		if ( node instanceof TerminalNode ) {
			return ((TerminalNode) node).getSymbol().getText();
		}
		return node.getClass().getSimpleName();
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

	@NotNull
	@Override
	public String getAlphaSortKey() {
		return getPresentableText();
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
