package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.TemplateLanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STGroupFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final STGroupFileType INSTANCE = new STGroupFileType();

    protected STGroupFileType() {
        super(STGroupLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "StringTemplate v4 template group file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "StringTemplate v4 template group file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "stg";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.STG_FILE;
    }
}
