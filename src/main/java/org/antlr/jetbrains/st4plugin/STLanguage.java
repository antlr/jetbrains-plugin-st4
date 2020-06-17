package org.antlr.jetbrains.st4plugin;

import com.intellij.lang.Language;

public class STLanguage extends Language {

    public static final STLanguage INSTANCE = new STLanguage();

    private STLanguage() {
        super("ST");
    }
}
