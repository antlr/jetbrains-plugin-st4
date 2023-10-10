package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.TemplateLanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final STFileType INSTANCE = new STFileType();

    protected STFileType() {
        super(STLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "StringTemplate v4 template file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "StringTemplate v4 template file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "st";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.STG_FILE;
    }
}
