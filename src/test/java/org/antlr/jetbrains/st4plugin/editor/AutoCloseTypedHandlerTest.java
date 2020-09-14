package org.antlr.jetbrains.st4plugin.editor;

import com.intellij.testFramework.EditorTestUtil;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;

public class AutoCloseTypedHandlerTest extends LightPlatformCodeInsightTestCase {

    public void testParensInFormalArgs() {
        configureFromFileText("test.stg", "tpl<caret>");

        EditorTestUtil.performTypingAction(getEditor(), '(');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl()", actual);
    }

    public void testAnonymousTemplateInArg() {
        configureFromFileText("test.stg", "tpl(foo = <caret>) ::= << >>");

        EditorTestUtil.performTypingAction(getEditor(), '{');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl(foo = {}) ::= << >>", actual);
    }

    public void testBigString() {
        configureFromFileText("test.stg", "tpl() ::= <<caret>");

        EditorTestUtil.performTypingAction(getEditor(), '<');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl() ::= <<>>", actual);
    }

    public void testNotBigString() {
        configureFromFileText("test.stg", "tpl() ::= < <caret>");

        EditorTestUtil.performTypingAction(getEditor(), '<');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl() ::= < <", actual);
    }

    public void testBigStringNoNl() {
        configureFromFileText("test.stg", "tpl() ::= <<caret>");

        EditorTestUtil.performTypingAction(getEditor(), '%');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl() ::= <%%>", actual);
    }

    public void testNotBigStringNoNl() {
        configureFromFileText("test.stg", "tpl() ::= < <caret>");

        EditorTestUtil.performTypingAction(getEditor(), '%');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl() ::= < %", actual);
    }

    public void testStringTemplate() {
        configureFromFileText("test.stg", "tpl() ::= <caret>");

        EditorTestUtil.performTypingAction(getEditor(), '"');

        String actual = getEditor().getDocument().getText();
        assertEquals("tpl() ::= \"\"", actual);
    }

    public void testTagCustomDelimiter() {
        configureFromFileText("test.stg", "delimiters \"[\", \"]\" tpl() ::= <<foo <caret>>>");

        EditorTestUtil.performTypingAction(getEditor(), '[');

        String actual = getEditor().getDocument().getText()
                .substring(3); // trim the custom delimiters that were passed to the sub-lexer
        assertEquals("foo []", actual);
    }

    public void testAnonymousTemplateInTag() {
        configureFromFileText("test.st", "blah <foo:<caret> blah");

        EditorTestUtil.performTypingAction(getEditor(), '{');

        String actual = getEditor().getDocument().getText();
        assertEquals("blah <foo:{} blah", actual);
    }

    public void testTag() {
        configureFromFileText("test.st", "foo <caret>");

        EditorTestUtil.performTypingAction(getEditor(), '<');

        String actual = getEditor().getDocument().getText();
        assertEquals("foo <>", actual);
    }

    public void testQuotesInTemplate() {
        configureFromFileText("test.st", "foo <test; attr=<caret>>");

        EditorTestUtil.performTypingAction(getEditor(), '"');

        String actual = getEditor().getDocument().getText();
        assertEquals("foo <test; attr=\"\">", actual);
    }
}
