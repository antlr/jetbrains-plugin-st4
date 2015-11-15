package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.jetbrains.st4plugin.STGroupPluginController;
import org.jetbrains.annotations.NotNull;

public class STGroupStructureViewBuilder extends TreeBasedStructureViewBuilder {
	public final VirtualFile file;
	public final Project project;

	public STGroupStructureViewBuilder(@NotNull VirtualFile file,
	                                   @NotNull Project project)
	{
		this.file = file;
		this.project = project;
	}

	@NotNull
	@Override
	public StructureViewModel createStructureViewModel(final Editor editor) {
		final STGroupStructureViewModel model = new STGroupStructureViewModel(editor, file);
		System.out.println("new StructureViewModel");
		STGroupPluginController controller = STGroupPluginController.getInstance(project);
		if ( controller!=null ) {
			controller.registerStructureViewModel(editor, model);
		}

		return model;
	}
}
