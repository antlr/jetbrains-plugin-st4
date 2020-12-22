package org.antlr.jetbrains.st4plugin.structview;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getRuleElementType;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import javax.swing.Icon;
import org.antlr.jetbrains.st4plugin.Icons;
import org.antlr.jetbrains.st4plugin.STGroupFileType;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.jetbrains.annotations.NotNull;

public class STGroupTemplateDefItemPresentationTest extends CodeInsightFixtureTestCase {

    public void testIssue37() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("A() ::= ");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);

        // Then
        assertSame(template.getNode().getElementType(), getRuleElementType(STGParser.RULE_template));
        assertEquals(Icons.BIGSTRING, icon);
    }

    public void testDict() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("dict ::= [] ");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);
        String text = presentation.getPresentableText();

        // Then
        assertEquals(Icons.DICT, icon);
        assertEquals("dict", text);
    }

    public void testString() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("stringTpl() ::= \"\" ");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);
        String text = presentation.getPresentableText();

        // Then
        assertEquals(Icons.STRING, icon);
        assertEquals("stringTpl", text);
    }

    public void testBigString() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("bigStringTpl() ::= <<>> ");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);
        String text = presentation.getPresentableText();

        // Then
        assertEquals(Icons.BIGSTRING, icon);
        assertEquals("bigStringTpl", text);
    }

    public void testBigStringNoNlWithParams() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("bigStringNoNl(foo, bar) ::= <%%> ");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);
        String text = presentation.getPresentableText();

        // Then
        assertEquals(Icons.BIGSTRING_NONL, icon);
        assertEquals("bigStringNoNl(foo, bar)", text);
    }

    public void testTemplateAlias() {
        // Given
        ASTWrapperPsiElement template = parseTemplate("foo ::= bar");

        STGroupTemplateDefItemPresentation presentation = new STGroupTemplateDefItemPresentation(template);

        // When
        Icon icon = presentation.getIcon(false);
        String text = presentation.getPresentableText();

        // Then
        assertEquals(Icons.BIGSTRING, icon);
        assertEquals("foo", text);
    }

    @NotNull
    private ASTWrapperPsiElement parseTemplate(String content) {
        PsiFile file = PsiFileFactory.getInstance(myFixture.getProject())
                .createFileFromText("a.stg", STGroupFileType.INSTANCE, content);

        PsiElement template = file.getFirstChild().getFirstChild();

        return (ASTWrapperPsiElement) template;
    }
}
