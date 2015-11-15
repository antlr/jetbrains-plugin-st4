package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.navigation.ItemPresentation;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class STGroupTemplateDefTreeElement extends STGroupStructureViewTreeElement {
	public STGroupTemplateDefTreeElement(ParseTree node) {
		super(node);
	}

	@NotNull
	@Override
	public ItemPresentation getPresentation() {
		return new STGroupTemplateDefItemPresentation(node);
	}
}
