package org.antlr.jetbrains.st4plugin.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ReflectionUtil;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.psi.STFile;
import org.antlr.jetbrains.st4plugin.psi.STGroupFile;
import org.antlr.jetbrains.st4plugin.psi.STTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;

/**
 * Automatically closes tag delimiters, template delimiters etc.
 */
public class AutoCloseTypedHandler extends TypedHandlerDelegate {

    @Override
    public @NotNull Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        List<IElementType> validBeforeLDelim = Arrays.asList(
                STTokenTypes.getTokenElementType(STLexer.TEXT),
                STTokenTypes.getTokenElementType(STLexer.RDELIM),
                STTokenTypes.getTokenElementType(STLexer.HORZ_WS)
        );

        if (file instanceof STFile) {
            int offset = editor.getCaretModel().getOffset();

            EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
            if (highlighter instanceof LexerEditorHighlighter) {
                Lexer lexer = ReflectionUtil.getField(LexerEditorHighlighter.class, highlighter, Lexer.class, "myLexer");
                org.antlr.v4.runtime.Lexer antlrLexer = ReflectionUtil.getField(ANTLRLexerAdaptor.class, lexer, org.antlr.v4.runtime.Lexer.class, "lexer");

                if (antlrLexer instanceof STLexer) {
                    char ldelim = ((STLexer) antlrLexer).getlDelim();
                    char rdelim = ((STLexer) antlrLexer).getrDelim();

                    if (c == ldelim) {
                        IElementType previousToken = offset == 0 ? null : highlighter.createIterator(offset - 1).getTokenType();

                        if (offset == 0 || validBeforeLDelim.contains(previousToken)) {
                            EditorModificationUtil.insertStringAtCaret(editor, "" + c + rdelim, false, 1);
                            return Result.STOP;
                        }
                    }
                }
            }
        }

        return super.beforeCharTyped(c, project, editor, file, fileType);
    }

    @Override
    public @NotNull Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (file instanceof STGroupFile) {
            int offset = editor.getCaretModel().getOffset();

            if ((c == '<' ||c == '%') && offset > 1) {
                autoCloseTemplate(c, editor, file, offset);
            }

            // TODO we could let IntelliJ handle this automatically if anonymous templates were parsed as `LBRACE ANON_TEMPLATE_CONTENT RBRACE`
            //  => we'd also have braces matching
            if (c == '{') {
                PsiElement previousElement = file.findElementAt(offset - 1);
                previousElement = previousElement == null ? null : PsiTreeUtil.prevVisibleLeaf(previousElement);

                if (previousElement != null && previousElement.getNode().getElementType() == getTokenElementType(STGLexer.ASSIGN)) {
                    EditorModificationUtil.insertStringAtCaret(editor, "}", false, 0);
                }
            }
        }

        return super.charTyped(c, project, editor, file);
    }

    private void autoCloseTemplate(char c, @NotNull Editor editor, @NotNull PsiFile file, int offset) {
        char previous = editor.getDocument().getCharsSequence().charAt(offset - 2);

        if (previous == '<') {
            PsiElement previousElement = file.findElementAt(offset - 2);
            previousElement = previousElement == null ? null : PsiTreeUtil.prevVisibleLeaf(previousElement);

            if (previousElement != null && previousElement.getNode().getElementType() == getTokenElementType(STGLexer.TMPL_ASSIGN)) {
                String closing = c == '<' ? ">>" : "%>";
                EditorModificationUtil.insertStringAtCaret(editor, closing, false, 0);
            }
        }
    }
}
