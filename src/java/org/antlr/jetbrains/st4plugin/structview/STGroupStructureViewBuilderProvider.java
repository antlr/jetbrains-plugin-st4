package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewBuilderProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.jetbrains.st4plugin.STGroupFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class STGroupStructureViewBuilderProvider implements StructureViewBuilderProvider {
	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(@NotNull FileType fileType,
	                                                    @NotNull VirtualFile file,
	                                                    @NotNull Project project)
	{
		if ( fileType instanceof STGroupFileType ) {
			return new STGroupStructureViewBuilder(file, project);
		}
		return null;
	}
}
