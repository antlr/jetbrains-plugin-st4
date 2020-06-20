package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

public class STGroupBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
            new BracePair(getTokenElementType(STGLexer.LBRACK), getTokenElementType(STGLexer.RBRACK), true),
            new BracePair(getTokenElementType(STGLexer.LPAREN), getTokenElementType(STGLexer.RPAREN), true)
    };

    @NotNull
    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
