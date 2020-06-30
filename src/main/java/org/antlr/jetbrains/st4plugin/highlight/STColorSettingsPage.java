package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.antlr.jetbrains.st4plugin.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class STColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Template Name", STGroupSyntaxHighlighter.TEMPLATE_NAME),
            new AttributesDescriptor("Template Parameter", STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM),
            new AttributesDescriptor("String", STGroupSyntaxHighlighter.STRING),
            new AttributesDescriptor("Keyword", STGroupSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Line Comment", STGroupSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block Comment", STGroupSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Option", STSemanticHighlightAnnotator.OPTION)
    };

    @Override
    public @Nullable Icon getIcon() {
        return Icons.STG_FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new STGroupSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return "<comment>/**\n" +
                " * Multi line comment\n" +
                " */</comment>\n" +
                "\n" +
                "<keyword>delimiters</keyword> \"$\", \"$\"\n" +
                "\n" +
                "// single line comment\n" +
                "myMap ::= [\n" +
                "    \"key\": \"value\",\n" +
                "    <keyword>default</keyword>: <keyword>key</keyword>\n" +
                "]\n" +
                "\n" +
                "myTemplate(<param>param1</param>, <param>param2</param>) ::= <<\n" +
                "    hello, world\n" +
                "    <comment><! some comment !></comment>\n" +
                "    <<keyword>if</keyword> (<param>param1</param>)>\n" +
                "        a\n" +
                "    <<keyword>elseif</keyword> (<keyword>true</keyword>)>\n" +
                "        b\n" +
                "    <<keyword>else</keyword>>\n" +
                "        <<param>param2</param> <option>separator</option>=\", \">\n" +
                "    <<keyword>endif</keyword>>\n" +
                ">>\n" +
                "\n" +
                "oneLiner(<param>x</param>) ::= \"hello, <<param>x</param>>\"\n";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> tagToDescriptor = new HashMap<>();
        tagToDescriptor.put("param", STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM);
        tagToDescriptor.put("keyword", STGroupSyntaxHighlighter.KEYWORD);
        tagToDescriptor.put("option", STSemanticHighlightAnnotator.OPTION);
        tagToDescriptor.put("comment", STGroupSyntaxHighlighter.BLOCK_COMMENT);
        return tagToDescriptor;
    }

    @Override
    public @NotNull AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public @NotNull ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull @Nls(
            capitalization = Nls.Capitalization.Title
    ) String getDisplayName() {
        return "StringTemplate";
    }
}
