package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.util.LazyInitializer;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.st4plugin.STGroupLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

public final class STGroupTokenTypes {

    private static final LazyInitializer.LazyValue<List<TokenIElementType>> TOKEN_ELEMENT_TYPES = LazyInitializer.create(() -> {
        initIElementTypes();
        return PSIElementTypeFactory.getTokenIElementTypes(STGroupLanguage.INSTANCE);
    });
    private static final LazyInitializer.LazyValue<List<RuleIElementType>> RULE_ELEMENT_TYPES = LazyInitializer.create(() -> {
        initIElementTypes();
        return PSIElementTypeFactory.getRuleIElementTypes(STGroupLanguage.INSTANCE);
    });

    public static void initIElementTypes() {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                STGroupLanguage.INSTANCE,
                STGLexer.tokenNames,
                STGParser.ruleNames
        );
    }

    public static RuleIElementType getRuleElementType(@MagicConstant(valuesFromClass = STGParser.class) int ruleIndex) {
        return RULE_ELEMENT_TYPES.get().get(ruleIndex);
    }

    public static TokenIElementType getTokenElementType(@MagicConstant(valuesFromClass = STGLexer.class) int ruleIndex) {
        return TOKEN_ELEMENT_TYPES.get().get(ruleIndex);
    }
}
