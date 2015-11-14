package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.FileEditorPositionListener;
import com.intellij.ide.structureView.ModelListener;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;

public class STGroupStructureViewModel implements StructureViewModel {
	protected Editor editor;
	protected VirtualFile file;
	protected ParseTree parseTree;

	public STGroupStructureViewModel(Editor editor, VirtualFile file) {
		this.editor = editor;
		this.file = file;
		this.parseTree = createTree(editor.getDocument().getText());
	}

	public ParserRuleContext createTree(String text) {
		final ANTLRInputStream input;
		try {
//			System.out.println("structview parse "+text.substring(0,5)+"...");
			input = new ANTLRInputStream(new StringReader(text));
			final STGLexer lexer = new STGLexer(input);
			lexer.removeErrorListeners(); // do your best to get a tree despite errors
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			STGParser parser = new STGParser(tokens);
			parser.removeErrorListeners();
			return parser.group();
		}
		catch (IOException ioe) {
			System.err.println("huh? can't happen");
		}
		return null;
	}

	@Override
	public void addEditorPositionListener(@NotNull FileEditorPositionListener listener) {
	}

	@Nullable
	@Override
	public Object getCurrentEditorElement() {
		return null;
	}

	@Override
	public void removeEditorPositionListener(@NotNull FileEditorPositionListener listener) {

	}

	@Override
	public void addModelListener(@NotNull ModelListener modelListener) {

	}

	@Override
	public void removeModelListener(@NotNull ModelListener modelListener) {

	}

	@NotNull
	@Override
	public StructureViewTreeElement getRoot() {
		return new STGroupRootStructureViewTreeElement(parseTree,file);
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean shouldEnterElement(Object element) {
		return false;
	}

	@NotNull
	@Override
	public Grouper[] getGroupers() {
		return new Grouper[0];
	}

	@NotNull
	@Override
	public Sorter[] getSorters() {
		return new Sorter[0];
	}

	@NotNull
	@Override
	public Filter[] getFilters() {
		return new Filter[0];
	}
}
