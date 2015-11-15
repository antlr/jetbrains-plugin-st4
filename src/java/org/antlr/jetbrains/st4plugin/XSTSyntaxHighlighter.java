package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.antlr.jetbrains.st4plugin.parsing.ParserErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParsingResult;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class XSTSyntaxHighlighter extends XSyntaxHighlighter {
	public static final TextAttributesKey TEMPLATE_TEXT =
		createTextAttributesKey("TEMPLATE_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);

	public XSTSyntaxHighlighter(Editor editor, int startIndex) {
		super(editor, startIndex);
	}

	@Override
	public Lexer getLexer(String text) {
		STLexer lexer = new STLexer(text);
		return lexer;
	}

	@Override
	public ParsingResult parse(CommonTokenStream tokens) {
		STParser parser = new STParser(tokens);
		parser.removeErrorListeners();
		ParserErrorListener errorListener = new ParserErrorListener();
		parser.addErrorListener(errorListener);
		ParserRuleContext tree = parser.template();
		return new ParsingResult(parser, tree, errorListener);
	}

	@Override
	public void highlightTree(ParserRuleContext tree, Parser parser) {
		final Collection<ParseTree> options = XPath.findAll(tree, "//option/ID", parser);
		for (ParseTree o : options) {
			TerminalNode tnode = (TerminalNode)o;
			highlightToken(tnode.getSymbol(),
						   new TextAttributesKey[]{DefaultLanguageHighlighterColors.KEYWORD});
		}
		final Collection<ParseTree> ids = XPath.findAll(tree, "//primary/ID", parser);
		for (ParseTree id : ids) {
			TerminalNode tnode = (TerminalNode)id;
			highlightToken(tnode.getSymbol(),
						   new TextAttributesKey[]{DefaultLanguageHighlighterColors.INSTANCE_FIELD});
		}
	}

	@NotNull
	@Override
	public TextAttributesKey[] getAttributesKey(Token t) {
		int tokenType = t.getType();
		TextAttributesKey key;
		switch ( tokenType ) {
			case STLexer.IF:
			case STLexer.ELSE:
			case STLexer.REGION_END:
			case STLexer.TRUE:
			case STLexer.FALSE:
			case STLexer.ELSEIF:
			case STLexer.ENDIF:
			case STLexer.SUPER:
				key = DefaultLanguageHighlighterColors.KEYWORD;
				break;
			case STLexer.STRING:
			case STLexer.TEXT:
				key = TEMPLATE_TEXT;
				return new TextAttributesKey[]{key, DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR};
//			case STLexer.ID:
//				key = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
//				break;
			case STLexer.COMMENT:
				key = DefaultLanguageHighlighterColors.LINE_COMMENT;
				break;
			case STLexer.ERROR_TYPE :
				key = HighlighterColors.BAD_CHARACTER;
				break;
			default:
//				key = DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR;
//				break;
				return NO_ATTR;
		}
		return new TextAttributesKey[]{key};//, DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR};
	}
}
