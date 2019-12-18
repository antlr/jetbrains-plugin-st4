package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.jetbrains.st4plugin.STGroupFileType;
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STGroupFile extends PsiFileBase {

    protected STGroupFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, STGroupLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return STGroupFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "String Template group file";
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.STG_FILE;
    }
}
