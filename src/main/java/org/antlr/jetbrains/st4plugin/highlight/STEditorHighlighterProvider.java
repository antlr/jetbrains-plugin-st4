package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import org.antlr.jetbrains.st4plugin.STGroupFileType;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

public class STEditorHighlighterProvider implements EditorHighlighterProvider {

    @Override
    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        return new STEditorHighlighter(virtualFile, project, fileType, colors);
    }
}

/**
 * Highlights the "outer language", i.e. the target language.
 * The target language can be configured in the Template Data Languages settings.
 */
class STEditorHighlighter extends LayeredLexerEditorHighlighter {

    public STEditorHighlighter(@Nullable VirtualFile virtualFile,
                               @Nullable Project project,
                               @NotNull FileType fileType,
                               @NotNull EditorColorsScheme scheme) {
        super(fileType == STGroupFileType.INSTANCE ? new STGroupSyntaxHighlighter() : new STSyntaxHighlighter(), scheme);

        if (project != null && virtualFile != null) {
            Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);

            if (language != null) {
                SyntaxHighlighter outerHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(language.getAssociatedFileType(), project, virtualFile);

                if (outerHighlighter != null) {
                    registerLayer(getTokenElementType(STLexer.TEXT), new LayerDescriptor(outerHighlighter, ""));
                }
            }
        }
    }
}
