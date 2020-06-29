package org.antlr.jetbrains.st4plugin.structview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import org.antlr.jetbrains.st4plugin.Icons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class STGroupRootItemPresentation implements ItemPresentation {
    private final PsiFile file;

    public STGroupRootItemPresentation(PsiFile file) {
        this.file = file;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return file.getName();
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return Icons.STG_FILE;
    }
}
