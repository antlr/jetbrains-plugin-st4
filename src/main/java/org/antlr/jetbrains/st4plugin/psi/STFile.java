package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.jetbrains.st4plugin.STFileType;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STFile extends PsiFileBase {

    protected STFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, STLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return STFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "String Template file";
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.STG_FILE;
    }
}
