package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.antlr.jetbrains.st4plugin.parsing.ParserErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParsingResult;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.jetbrains.st4plugin.parsing.STGParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public static final TextAttributesKey TEMPLATE_NAME =
		createTextAttributesKey("TEMPLATE_NAME", DefaultLanguageHighlighterColors.INSTANCE_METHOD);

	public STGroupSyntaxHighlighter(Editor editor, int startIndex) {
		super(editor, startIndex);
	}

	@Override
	public Lexer getLexer(String text) {
		final ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(new StringReader(text));
			final STGLexer lexer = new STGLexer(input);
			return lexer;
		}
		catch (IOException ioe) {
			System.err.println("huh? can't happen");
		}
		return null;
	}

	@Override
	public ParsingResult parse(CommonTokenStream tokens) {
		STGParser parser = new STGParser(tokens);
		parser.removeErrorListeners();
		ParserErrorListener errorListener = new ParserErrorListener();
		parser.addErrorListener(errorListener);
		ParserRuleContext tree = parser.group();
		return new ParsingResult(parser, tree, errorListener);
	}

	@Override
	public void highlightTree(ParserRuleContext tree, Parser parser) {
		final Collection<ParseTree> options = XPath.findAll(tree, "//formalArg/ID", parser);
		for (ParseTree o : options) {
			TerminalNode tnode = (TerminalNode)o;
			if ( !(tnode instanceof ErrorNode) ) {
				highlightToken(tnode.getSymbol(),
							   new TextAttributesKey[]{DefaultLanguageHighlighterColors.INSTANCE_FIELD});
			}
		}
		final Collection<ParseTree> ids = XPath.findAll(tree, "//template/ID", parser);
		for (ParseTree id : ids) {
			TerminalNode tnode = (TerminalNode)id;
			highlightToken(tnode.getSymbol(), new TextAttributesKey[]{TEMPLATE_NAME});
		}
	}

	@NotNull
	@Override
	public TextAttributesKey[] getAttributesKey(Token t) {
		switch (t.getType()) {
			case STGLexer.DOC_COMMENT:
				return new TextAttributesKey[]{DefaultLanguageHighlighterColors.DOC_COMMENT};
			case STGLexer.LINE_COMMENT:
				return new TextAttributesKey[]{DefaultLanguageHighlighterColors.LINE_COMMENT};
			case STGLexer.BLOCK_COMMENT:
				return new TextAttributesKey[]{DefaultLanguageHighlighterColors.BLOCK_COMMENT};
			case STGLexer.ID:
				return new TextAttributesKey[]{DefaultLanguageHighlighterColors.IDENTIFIER};

			case STGLexer.DELIMITERS:
			case STGLexer.IMPORT:
			case STGLexer.DEFAULT:
			case STGLexer.KEY:
			case STGLexer.VALUE:
			case STGLexer.FIRST:
			case STGLexer.LAST:
			case STGLexer.REST:
			case STGLexer.TRUNC:
			case STGLexer.STRIP:
			case STGLexer.TRIM:
			case STGLexer.LENGTH:
			case STGLexer.STRLEN:
			case STGLexer.REVERSE:
			case STGLexer.GROUP:
			case STGLexer.WRAP:
			case STGLexer.ANCHOR:
			case STGLexer.SEPARATOR:
				return new TextAttributesKey[]{DefaultLanguageHighlighterColors.KEYWORD};
			case Token.INVALID_TYPE:
				return new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};
			default:
				return NO_ATTR;
		}
	}

	@Override
	public boolean isEmbeddedLanguageToken(Token t) {
		final int tokenType = t.getType();
		return
			tokenType == STGLexer.STRING ||
			tokenType == STGLexer.ANON_TEMPLATE ||
			tokenType == STGLexer.BIGSTRING ||
			tokenType == STGLexer.BIGSTRING_NO_NL;
	}

	@Override
	public void highlightEmbedded(Token t) {
		STSyntaxHighlighter templateHighlighter = null;
		int startOfEmbeddedToken = t.getStartIndex();
		if (t.getType() == STGLexer.STRING ||
			t.getType() == STGLexer.ANON_TEMPLATE) {
			String text = t.getText();
			text = text.substring(1, text.length() - 1);
			startOfEmbeddedToken++;
			templateHighlighter = new STSyntaxHighlighter(getEditor(), startOfEmbeddedToken);
			templateHighlighter.highlight(text);
		}
		else if (t.getType() == STGLexer.BIGSTRING ||
				 t.getType() == STGLexer.BIGSTRING_NO_NL) {
			String text = t.getText();
			text = text.substring(2, text.length() - 2);
			startOfEmbeddedToken += 2;
			templateHighlighter = new STSyntaxHighlighter(getEditor(), startOfEmbeddedToken);
			templateHighlighter.highlight(text);
		}
	}
}
