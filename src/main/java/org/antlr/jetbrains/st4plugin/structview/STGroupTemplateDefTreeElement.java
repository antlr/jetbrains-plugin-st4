package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.ScrollType;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;

public class STGroupTemplateDefTreeElement extends STGroupStructureViewTreeElement {
	public STGroupTemplateDefTreeElement(STGroupStructureViewModel model, ParseTree node) {
		super(model, node);
	}

	@NotNull
	@Override
	public ItemPresentation getPresentation() {
		return new STGroupTemplateDefItemPresentation(node);
	}

	@Override
	public boolean canNavigate() {
		return true;
	}

	@Override
	public boolean canNavigateToSource() {
		return true;
	}

	@Override
	public void navigate(boolean requestFocus) {
		CaretModel caretModel = model.editor.getCaretModel();
		model.editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
		caretModel.moveToOffset(((TerminalNode)node).getSymbol().getStartIndex());
	}
}
