package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;

/**
 * Highlights a group's formal args differently from its name.
 */
public class STGroupSemanticHighlightAnnotator implements Annotator {

    public static final TextAttributesKey TEMPLATE_PARAM = createTextAttributesKey("STGroup_TEMPLATE_PARAM", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() == STGroupTokenTypes.getTokenElementType(STGLexer.ID)) {
            if (element.getParent().getNode().getElementType() == getRuleElementType(STGParser.RULE_formalArg)) {
                holder.createInfoAnnotation(element, null)
                        .setTextAttributes(TEMPLATE_PARAM);
            }
        }
    }
}
