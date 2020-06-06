package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class STGroupHighlightingPassFactory
	implements TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar
{

	@Nullable
	@Override
	public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
		if ( editor==null ) return null;
		Document doc = editor.getDocument();
		Project project = editor.getProject();
		VirtualFile vfile = FileDocumentManager.getInstance().getFile(doc);
		if ( project == null || vfile==null || !vfile.getName().endsWith(".stg") ) return null;
		return new STGroupHighlightingPass(project, editor);
	}

	@Override
	public void registerHighlightingPassFactory(@NotNull TextEditorHighlightingPassRegistrar registrar,
												@NotNull Project project) {
		registrar.registerTextEditorHighlightingPass(this, null, null, true, -1);
	}

}
