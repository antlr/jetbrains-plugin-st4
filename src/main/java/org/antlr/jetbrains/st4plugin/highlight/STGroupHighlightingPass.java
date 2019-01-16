package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;

public class STGroupHighlightingPass extends TextEditorHighlightingPass {
	protected Editor editor;

	public STGroupHighlightingPass(Project project, Editor editor) {
		super(project, editor.getDocument());
		this.editor = editor;
	}

	@Override
	public void doApplyInformationToEditor() {
		if ( editor==null ) return;
		STGroupSyntaxHighlighter groupHighlighter = new STGroupSyntaxHighlighter(editor,0);
		groupHighlighter.highlight();
	}

	@Override
	public void doCollectInformation(ProgressIndicator progress) {
	}
}
