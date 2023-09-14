package org.antlr.jetbrains.st4plugin.psi;

import com.intellij.util.LazyInitializer;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.st4plugin.STLanguage;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

public final class STTokenTypes {

    private static final LazyInitializer.LazyValue<List<TokenIElementType>> TOKEN_ELEMENT_TYPES = LazyInitializer.create(() -> {
        initIElementTypes();
        return PSIElementTypeFactory.getTokenIElementTypes(STLanguage.INSTANCE);
    });
    private static final LazyInitializer.LazyValue<List<RuleIElementType>> RULE_ELEMENT_TYPES = LazyInitializer.create(() -> {
        initIElementTypes();
        return PSIElementTypeFactory.getRuleIElementTypes(STLanguage.INSTANCE);
    });

    public static void initIElementTypes() {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                STLanguage.INSTANCE,
                STLexer.tokenNames,
                STParser.ruleNames
        );
    }

    public static RuleIElementType getRuleElementType(@MagicConstant(valuesFromClass = STParser.class) int ruleIndex) {
        return RULE_ELEMENT_TYPES.get().get(ruleIndex);
    }

    public static TokenIElementType getTokenElementType(@MagicConstant(valuesFromClass = STLexer.class) int ruleIndex) {
        return TOKEN_ELEMENT_TYPES.get().get(ruleIndex);
    }
}
