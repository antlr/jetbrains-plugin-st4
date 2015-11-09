package org.antlr.jetbrains.st4plugin;

import com.intellij.lang.Language;

public class STGroupLanguage extends Language {
    public static final STGroupLanguage INSTANCE = new STGroupLanguage();

    private STGroupLanguage() {
        super("STGroup");
    }
}
