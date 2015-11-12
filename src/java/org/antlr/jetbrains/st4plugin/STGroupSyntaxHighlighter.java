package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.antlr.jetbrains.st4plugin.parsing.STGLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class STGroupSyntaxHighlighter extends SyntaxHighlighter {
	public STGroupSyntaxHighlighter(Editor editor) {
		super(editor);
	}

	@NotNull
	@Override
	public CommonTokenStream tokenize(String text) {
		ANTLRInputStream input = new ANTLRInputStream(text);
		STGLexer lexer = new STGLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens;
	}

	@Override
	@NotNull
	public TextAttributesKey[] getAttributesKey(CommonTokenStream tokens, int tokenIndex) {
		TextAttributesKey[] key = getAttributesKey(tokens.get(tokenIndex).getType());
		if ( key == NO_ATTR ) return NO_ATTR;
		if ( tokenIndex==tokens.size()-1 ) return key; // at end
		List<Token> afterTokens = tokens.getHiddenTokensToRight(tokenIndex, Token.DEFAULT_CHANNEL);
		if ( afterTokens==null || afterTokens.size()==0 ) {
			return key;
		}
		Token after = afterTokens.get(0);
		Token t = tokens.get(tokenIndex);
		if ( t.getType()==STGLexer.ID &&
			(after.getType()==STGLexer.TMPL_ASSIGN||after.getType()==STGLexer.LPAREN) )
		{
			return new TextAttributesKey[] {DefaultLanguageHighlighterColors.INSTANCE_FIELD};
		}
		return key;
	}

	@Override
	public boolean isEmbeddedLanguageToken(int tokenType) {
		return
			tokenType==STGLexer.STRING ||
			tokenType==STGLexer.ANON_TEMPLATE ||
			tokenType==STGLexer.BIGSTRING ||
			tokenType==STGLexer.BIGSTRING_NO_NL;
	}

	@Override
	public void highlightEmbedded(Token t) {
		STSyntaxHighlighter templateHighlighter = new STSyntaxHighlighter(getEditor());
		int startOfEmbeddedToken = t.getStartIndex();
		if ( t.getType()==STGLexer.STRING ||
			 t.getType()==STGLexer.ANON_TEMPLATE )
		{
//				System.out.println("template: "+t);
			String text = t.getText();
			text = text.substring(1, text.length()-1);
			startOfEmbeddedToken++;
			templateHighlighter.highlight(text, startOfEmbeddedToken, t.getStopIndex()-1);
		}
		else if ( t.getType()==STGLexer.BIGSTRING ||
			 t.getType()==STGLexer.BIGSTRING_NO_NL )
		{
//				System.out.println("template: "+t);
			String text = t.getText();
			text = text.substring(2, text.length()-2);
			startOfEmbeddedToken += 2;
			templateHighlighter.highlight(text, startOfEmbeddedToken, t.getStopIndex()-2);
		}
		// do error tokens
		for (Token err : templateHighlighter.lexer.getErrorTokens()) {
			System.out.println(err);
			templateHighlighter.highlightToken(err, startOfEmbeddedToken);
		}
	}

	@NotNull
	public TextAttributesKey[] getAttributesKey(int tokenType) {
		switch ( tokenType ) {
			case STGLexer.DOC_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.DOC_COMMENT};
			case STGLexer.LINE_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.LINE_COMMENT};
			case STGLexer.BLOCK_COMMENT :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.BLOCK_COMMENT};
			case STGLexer.ID :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.IDENTIFIER};

			case STGLexer.DELIMITERS :
			case STGLexer.IMPORT :
			case STGLexer.DEFAULT :
			case STGLexer.KEY :
			case STGLexer.VALUE :
			case STGLexer.FIRST :
			case STGLexer.LAST :
			case STGLexer.REST :
			case STGLexer.TRUNC :
			case STGLexer.STRIP :
			case STGLexer.TRIM :
			case STGLexer.LENGTH :
			case STGLexer.STRLEN :
			case STGLexer.REVERSE :
			case STGLexer.GROUP :
			case STGLexer.WRAP :
			case STGLexer.ANCHOR :
			case STGLexer.SEPARATOR :
				return new TextAttributesKey[] {DefaultLanguageHighlighterColors.KEYWORD};
			default :
				return NO_ATTR;
		}
	}
}
