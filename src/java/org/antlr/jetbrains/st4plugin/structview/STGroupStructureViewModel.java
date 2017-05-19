package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	/** From editor's cursor, find associated parse tree node so we can highlight
	 *  in structure view. It wants the parse tree node not a
	 *  StructureViewTreeElement. It will try to find a path from root to
	 *  that node and highlight it.
	 */
	@Nullable
	public Object getCurrentEditorElement() {
		if (editor==null) return null;
		final int offset = editor.getCaretModel().getOffset();
		if ( parseTree==null ) return null;
		Tree selectedNode = Trees.findNodeSuchThat(parseTree, new Predicate<Tree>() {
			@Override
			public boolean test(Tree node) {
				if ( !(node instanceof TerminalNode) ) return false;
				Token t = ((TerminalNode) node).getSymbol();
				return offset>=t.getStartIndex() && offset<=t.getStopIndex();
			}
		});
		if ( selectedNode==null ) return null;
		// now walk up looking for template def node
		ParseTree p = (ParseTree)selectedNode;
		while ( p!=null && !(p instanceof STGParser.TemplateContext) ) {
			p = p.getParent();
		}
		if ( p!=null ) {
			return ((STGParser.TemplateContext)p).ID(0);
		}
		return null;
	}


	@Nullable
	protected Object findAcceptableElement(PsiElement element) {
		while (element != null && !(element instanceof PsiFile)) {
			if (isSuitable(element)) return element;
			element = element.getParent();
		}
		return null;
	}


	/** If parseTree==null, this will return a StructureViewTreeElement with
	 * getValue()==null, which forces rebuild in {@link com.intellij.ide.impl.StructureViewWrapperImpl#rebuild()}
	 */
	@NotNull
	@Override
	public StructureViewTreeElement getRoot() {
		return new STGroupRootTreeElement(this,parseTree,file);
	}

	@NotNull
	@Override
	public Sorter[] getSorters() {
		return new Sorter[] {Sorter.ALPHA_SORTER};
	}
}
