package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.jetbrains.st4plugin.psi.STTokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getRuleElementType;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

/**
 * Semantic highlighting for .st files.
 */
public class STSemanticHighlightAnnotator implements Annotator {

    private static final TextAttributesKey ST_TAG = createTextAttributesKey("ST_TAG");
    public static final TextAttributesKey OPTION = createTextAttributesKey("ST_OPTION", DefaultLanguageHighlighterColors.INSTANCE_METHOD);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() == STTokenTypes.getTokenElementType(STLexer.ID)) {
            if (isPrimary(element) || isSubtemplate(element)) {
                holder.createInfoAnnotation(element, null)
                        .setTextAttributes(STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM);
            } else if (isOptionId(element)) {
                holder.createInfoAnnotation(element, null)
                        .setTextAttributes(OPTION);
            } else if (isCall(element)) {
                holder.createInfoAnnotation(element, null)
                        .setTextAttributes(STGroupSyntaxHighlighter.TEMPLATE_NAME);
            }
        }

        if (isTag(element)) {
            // use a white/dark background instead of the default green from injected languages
            holder.createInfoAnnotation(element, null)
                    .setTextAttributes(ST_TAG);
        }

        if (element.getNode().getElementType() == getRuleElementType(STParser.RULE_ifstat)) {
            for (ASTNode ldelim : element.getNode().getChildren(TokenSet.create(getTokenElementType(STLexer.LDELIM)))) {
                ASTNode rdelim = element.getNode().findChildByType(getTokenElementType(STLexer.RDELIM), ldelim);

                if (rdelim != null) {
                    // use a white/dark background instead of the default green from injected languages
                    holder.createInfoAnnotation(TextRange.create(ldelim.getStartOffset(), rdelim.getStartOffset() + rdelim.getTextLength()), null)
                            .setTextAttributes(ST_TAG);
                }
            }
        }
    }

    private boolean isSubtemplate(@NotNull PsiElement element) {
        return element.getParent().getNode().getElementType() == getRuleElementType(STParser.RULE_subtemplate);
    }

    private boolean isTag(@NotNull PsiElement element) {
        return element.getNode().getElementType() == STTokenTypes.getRuleElementType(STParser.RULE_exprTag);
    }

    private boolean isPrimary(@NotNull PsiElement element) {
        return element.getParent().getNode().getElementType() == getRuleElementType(STParser.RULE_primary);
    }

    private boolean isCall(PsiElement element) {
        PsiElement nextVisibleLeaf = PsiTreeUtil.nextVisibleLeaf(element);

        return nextVisibleLeaf != null && nextVisibleLeaf.getNode().getElementType() == STTokenTypes.getTokenElementType(STLexer.LPAREN);
    }

    private boolean isOptionId(@NotNull PsiElement element) {
        return element.getParent().getNode().getElementType() == getRuleElementType(STParser.RULE_option);
    }
}
