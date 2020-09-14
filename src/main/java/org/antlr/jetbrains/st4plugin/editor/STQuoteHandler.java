package org.antlr.jetbrains.st4plugin.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;

import static org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory.createTokenSet;

/**
 * Automatically closes {@link STLexer#STRING} quotes.
 */
public class STQuoteHandler extends SimpleTokenSetQuoteHandler {

    public STQuoteHandler() {
        super(createTokenSet(STLanguage.INSTANCE, STLexer.STRING, STLexer.EOF_STRING));
    }
}
