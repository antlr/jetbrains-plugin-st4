package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import org.antlr.jetbrains.st4plugin.parsing.ParsingUtils;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public class STGroupFileEditorListener extends DocumentAdapter {
	public Project project;

	public STGroupFileEditorListener(Project project) {
		this.project = project;
	}

	@Override
	public void documentChanged(DocumentEvent e) {
		Document doc = e.getDocument();
		String docText = doc.getCharsSequence().toString();
		STGroupPluginController ctrl = STGroupPluginController.getInstance(project);
		if ( ctrl==null ) return;
		Editor editor = ctrl.getEditor(doc);
		MarkupModel markupModel = editor.getMarkupModel();
		CommonTokenStream tokens = ParsingUtils.tokenize(docText);
		System.out.println("tokens: "+tokens.getTokens().toString());
		final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();
		final EditorColorsScheme scheme =
			editorColorsManager.getScheme(EditorColorsScheme.DEFAULT_SCHEME_NAME);
		final TextAttributes attr =
			scheme.getAttributes(DefaultLanguageHighlighterColors.STRING);
		for (int i=0; i<tokens.size(); i++) {
			Token t = tokens.get(i);
			markupModel.addRangeHighlighter(
				t.getStartIndex(),
				t.getStopIndex()+1,
				HighlighterLayer.SYNTAX, // layer
				attr,
				HighlighterTargetArea.EXACT_RANGE);
		}
	}
}
