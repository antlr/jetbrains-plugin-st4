package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.Nullable;

public class STGroupRootItemPresentation extends STGroupItemPresentation {
	protected final VirtualFile file;
	public STGroupRootItemPresentation(ParseTree node, VirtualFile file) {
		super(node);
		this.file = file;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return file.getPresentableName();
	}
}
