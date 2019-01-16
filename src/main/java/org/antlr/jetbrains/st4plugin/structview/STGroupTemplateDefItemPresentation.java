package org.antlr.jetbrains.st4plugin.structview;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Nullable;

public class STGroupTemplateDefItemPresentation extends STGroupItemPresentation {
	public STGroupTemplateDefItemPresentation(ParseTree node) {
		super(node);
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return ((TerminalNode)node).getSymbol().getText();
	}
}
