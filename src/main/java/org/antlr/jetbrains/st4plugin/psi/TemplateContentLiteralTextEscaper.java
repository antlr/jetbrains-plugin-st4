package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.jetbrains.annotations.NotNull;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

/**
 * Removes escaped quotes from subtemplates when parsing them as {@link org.antlr.jetbrains.st4plugin.STLanguage}
 * files.
 */
class TemplateContentLiteralTextEscaper extends LiteralTextEscaper<PsiLanguageInjectionHost> {

    private int[] offsetsFromDecodedToHost = new int[0];

    public TemplateContentLiteralTextEscaper(TemplateContentElement templateContentElement) {
        super(templateContentElement);
    }

    @Override
    public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        String subTemplate = rangeInsideHost.substring(myHost.getText());

        if (myHost.getNode().getFirstChildNode().getElementType() == getTokenElementType(STGLexer.STRING)
                && subTemplate.indexOf('\\') >= 0) {

            offsetsFromDecodedToHost = new int[subTemplate.length() + 1];

            for (int indexInHost = 0, indexInDecoded = 0; indexInHost < subTemplate.length(); indexInHost++) {
                if (subTemplate.charAt(indexInHost) == '\\'
                        && indexInHost + 1 < subTemplate.length()
                        && subTemplate.charAt(indexInHost + 1) == '"') {

                    indexInHost++; // skip '\'
                }

                outChars.append(subTemplate.charAt(indexInHost));

                offsetsFromDecodedToHost[indexInDecoded++] = indexInHost;
                offsetsFromDecodedToHost[indexInDecoded] = indexInHost + 1;
            }
        } else {
            offsetsFromDecodedToHost = new int[subTemplate.length() + 1];
            for (int i = 0; i < offsetsFromDecodedToHost.length; i++) {
                offsetsFromDecodedToHost[i] = i;
            }
            outChars.append(subTemplate);
        }

        return true;
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
        int result = offsetInDecoded < offsetsFromDecodedToHost.length ? offsetsFromDecodedToHost[offsetInDecoded] : -1;
        if (result == -1) {
            return -1;
        }

        return Math.min(result, rangeInsideHost.getLength()) + rangeInsideHost.getStartOffset();
    }

    @Override
    public boolean isOneLine() {
        return false;
    }
}
