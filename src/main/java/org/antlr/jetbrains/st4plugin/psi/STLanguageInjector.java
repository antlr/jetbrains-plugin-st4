package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.jetbrains.annotations.NotNull;

import static org.antlr.jetbrains.st4plugin.parsing.LexerAdaptor.DELIMITERS_PREFIX;
import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;
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

            String delimiters = detectDelimiters(host);

            if (firstChild != null
                    && (firstChild.getNode().getElementType() == getTokenElementType(STGLexer.BIGSTRING) || firstChild.getNode().getElementType() == getTokenElementType(STGLexer.BIGSTRING_NO_NL))
                    && host.getTextLength() > 4) {
                TextRange textRange = TextRange.create(2, host.getTextLength() - 2);
                injectionPlacesRegistrar.addPlace(STLanguage.INSTANCE, textRange, delimiters, null);
            } else if (firstChild != null
                    && firstChild.getNode().getElementType() == getTokenElementType(STGLexer.STRING)
                    && host.getTextLength() > 2) {
                TextRange textRange = TextRange.create(1, host.getTextLength() - 1);
                injectionPlacesRegistrar.addPlace(STLanguage.INSTANCE, textRange, delimiters, null);
            }
        }
    }

    /**
     * If the STGroup file contains a {@code delimiters "x", "y"} section, we pass those delimiters as a special
     * prefix to the lexer. The lexer will then detect this prefix and reconfigure itself to support the new
     * delimiters.
     */
    private String detectDelimiters(@NotNull PsiLanguageInjectionHost host) {
        ASTNode root = host.getContainingFile().getFirstChild().getNode();

        if (root.getElementType() == getRuleElementType(STGParser.RULE_group)) {
            ASTNode delimitersStatement = root.findChildByType(getRuleElementType(STGParser.RULE_delimiters));

            if (delimitersStatement != null) {
                ASTNode[] strings = delimitersStatement.getChildren(TokenSet.create(getTokenElementType(STGLexer.STRING)));

                if (strings.length == 2 && strings[0].getTextLength() == 3 && strings[1].getTextLength() == 3) {
                    return "" + DELIMITERS_PREFIX + strings[0].getText().charAt(1) + strings[1].getText().charAt(1);
                }
            }
        }
        return null;
    }
}
