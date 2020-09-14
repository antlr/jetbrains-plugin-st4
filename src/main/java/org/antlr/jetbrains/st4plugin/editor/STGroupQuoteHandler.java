package org.antlr.jetbrains.st4plugin.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;

import static org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory.createTokenSet;

/**
 * Automatically closes {@link STGLexer#STRING} quotes.
 */
public class STGroupQuoteHandler extends SimpleTokenSetQuoteHandler {

    public STGroupQuoteHandler() {
        super(createTokenSet(STGroupLanguage.INSTANCE, STGLexer.STRING, STGLexer.EOF_STRING));
    }
}
