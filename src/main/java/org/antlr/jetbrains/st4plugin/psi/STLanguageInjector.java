package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.jetbrains.annotations.NotNull;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

/**
 * Inject the {@link STLanguage} in {@link org.antlr.jetbrains.st4plugin.STGroupLanguage} subtemplates.
 */
public class STLanguageInjector implements LanguageInjector {

    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host,
                                     @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {

        if (host instanceof TemplateContentElement) {
            PsiElement firstChild = host.getFirstChild();

            if (firstChild != null
                    && (firstChild.getNode().getElementType() == getTokenElementType(STGLexer.BIGSTRING) || firstChild.getNode().getElementType() == getTokenElementType(STGLexer.BIGSTRING_NO_NL) )
                    && host.getTextLength() > 4) {
                TextRange textRange = TextRange.create(2, host.getTextLength() - 2);
                injectionPlacesRegistrar.addPlace(STLanguage.INSTANCE, textRange, null, null);
            } else if (firstChild != null
                    && firstChild.getNode().getElementType() == getTokenElementType(STGLexer.STRING)
                    && host.getTextLength() > 2) {
                TextRange textRange = TextRange.create(1, host.getTextLength() - 1);
                injectionPlacesRegistrar.addPlace(STLanguage.INSTANCE, textRange, null, null);
            }
        }
    }
}
