package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class STGroupRootTreeElement extends STGroupStructureViewTreeElement {
	protected final VirtualFile file;

	public STGroupRootTreeElement(STGroupStructureViewModel model, ParseTree root, VirtualFile file) {
		super(model, root);
		this.file = file;
	}

	@NotNull
	@Override
	public ItemPresentation getPresentation() {
		return new STGroupRootItemPresentation(node,file);
	}

	@NotNull
	@Override
	public TreeElement[] getChildren() {
		ParserRuleContext root = (ParserRuleContext) this.node;
		Collection<ParseTree> rules = Trees.findAllRuleNodes(root, STGParser.RULE_template);
		if ( rules.size()==0 ) return EMPTY_ARRAY;
		List<TreeElement> treeElements = new ArrayList<TreeElement>(rules.size());
		for (ParseTree t : rules) {
			ParseTree nameNode = t.getChild(0);
			treeElements.add(new STGroupTemplateDefTreeElement(model, nameNode));
		}
//		System.out.println("rules="+rules);
		return treeElements.toArray(new TreeElement[treeElements.size()]);
	}
}
