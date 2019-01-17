package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class STGroupFileTypeFactory extends FileTypeFactory {
	@Override
	public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
		fileTypeConsumer.consume(STGroupFileType.INSTANCE, "stg");
	}
}
