package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;

public class STGroupStructureViewModel extends TextEditorBasedStructureViewModel {
	protected Editor editor;
	protected VirtualFile file;
	protected ParseTree parseTree;

	public STGroupStructureViewModel(Editor editor, VirtualFile file) {
		super(editor);
		this.editor = editor;
		this.file = file;
		setTreeFromText(editor.getDocument().getText());
	}

	public void setTreeFromText(String text) {
		final ANTLRInputStream input;
		try {
//			System.out.println("structview parse "+text.substring(0,5)+"...");
			input = new ANTLRInputStream(new StringReader(text));
			final STGLexer lexer = new STGLexer(input);
			lexer.removeErrorListeners(); // do your best to get a tree despite errors
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			STGParser parser = new STGParser(tokens);
			parser.removeErrorListeners();
			this.parseTree = parser.group();
		}
		catch (IOException ioe) {
			System.err.println("huh? can't happen");
		}
	}

	/** force rebuild; see {@link #getRoot()} */
	public void invalidate() {
		parseTree = null;
	}

	/** If parseTree==null, this will return a StructureViewTreeElement with
	 * getValue()==null, which forces rebuild in {@link com.intellij.ide.impl.StructureViewWrapperImpl#rebuild()}
	 */
	@NotNull
	@Override
	public StructureViewTreeElement getRoot() {
		return new STGroupRootStructureViewTreeElement(parseTree,file);
	}

	@NotNull
	@Override
	public Sorter[] getSorters() {
		return new Sorter[] {Sorter.ALPHA_SORTER};
	}
}
