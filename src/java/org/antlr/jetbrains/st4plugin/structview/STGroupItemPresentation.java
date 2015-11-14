package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.navigation.ItemPresentation;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STGroupItemPresentation implements ItemPresentation {
	protected ParseTree node;

	public STGroupItemPresentation(ParseTree node) {
		this.node = node;
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		if ( node.getParent()==null ) return null;
		return Icons.STG_FILE;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		if ( node instanceof TerminalNode ) {
			return ((TerminalNode) node).getSymbol().getText();
		}
		return "n/a";
	}

	@Nullable
	@Override
	public String getLocationString() {
		return null;
	}
}
