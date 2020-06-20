package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

public class STBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
            new BracePair(getTokenElementType(STLexer.LBRACK), getTokenElementType(STLexer.RBRACK), true),
            new BracePair(getTokenElementType(STLexer.LDELIM), getTokenElementType(STLexer.RDELIM), true),
            new BracePair(getTokenElementType(STLexer.LBRACE), getTokenElementType(STLexer.RBRACE), true),
            new BracePair(getTokenElementType(STLexer.LPAREN), getTokenElementType(STLexer.RPAREN), true)
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
