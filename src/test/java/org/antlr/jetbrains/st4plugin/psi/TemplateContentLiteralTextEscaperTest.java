package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import static org.antlr.jetbrains.st4plugin.psi.STGroupTokenTypes.getTokenElementType;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class TemplateContentLiteralTextEscaperTest {

    private TemplateContentLiteralTextEscaper escaper;

    @BeforeClass
    public static void beforeClass() {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                STGroupLanguage.INSTANCE,
                STGLexer.tokenNames,
                STGParser.ruleNames
        );
    }

    @Test
    public void testDecodeNoQuotes() {
        // Given
        String templateText = "\"some template\"";

        TemplateContentElement template = parse(templateText);
        escaper = new TemplateContentLiteralTextEscaper(template);

        TextRange rangeInsideHost = TextRange.create(1, templateText.length() - 1);

        StringBuilder outBuilder = new StringBuilder();

        // When
        escaper.decode(rangeInsideHost, outBuilder);

        // Then
        assertEquals(rangeInsideHost.substring(templateText), outBuilder.toString());

        assertEquals(1, escaper.getOffsetInHost(0, rangeInsideHost));
        assertEquals(2, escaper.getOffsetInHost(1, rangeInsideHost));
        assertEquals(3, escaper.getOffsetInHost(2, rangeInsideHost));
        assertEquals(13, escaper.getOffsetInHost(12, rangeInsideHost));
    }

    @Test
    public void testDecodeQuotes() {
        // Given
        String templateText = "\"format=\\\"cap\\\";\"";

        TemplateContentElement template = parse(templateText);
        escaper = new TemplateContentLiteralTextEscaper(template);

        TextRange rangeInsideHost = TextRange.create(1, templateText.length() - 1);

        StringBuilder outBuilder = new StringBuilder();

        // When
        escaper.decode(rangeInsideHost, outBuilder);

        // Then
        assertEquals("format=\"cap\";", outBuilder.toString());

        assertEquals(1, escaper.getOffsetInHost(0, rangeInsideHost)); // f
        assertEquals(2, escaper.getOffsetInHost(1, rangeInsideHost)); // o
        assertEquals(3, escaper.getOffsetInHost(2, rangeInsideHost)); // r
        assertEquals(9, escaper.getOffsetInHost(7, rangeInsideHost)); // "
        assertEquals(10, escaper.getOffsetInHost(8, rangeInsideHost)); // c
        assertEquals(14, escaper.getOffsetInHost(11, rangeInsideHost)); // "
        assertEquals(15, escaper.getOffsetInHost(12, rangeInsideHost)); // ;
    }

    private TemplateContentElement parse(String raw) {
        TemplateContentElement template = mock(TemplateContentElement.class, new ThrowsException(new UnsupportedOperationException()));
        ASTNode astNode = mock(ASTNode.class);

        doReturn(raw).when(template).getText();
        doReturn(astNode).when(template).getNode();
        doReturn(astNode).when(astNode).getFirstChildNode();
        doReturn(getTokenElementType(STGLexer.STRING)).when(astNode).getElementType();

        return template;
    }
}