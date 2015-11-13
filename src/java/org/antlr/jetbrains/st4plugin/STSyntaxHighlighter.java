package org.antlr.jetbrains.st4plugin;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.antlr.jetbrains.st4plugin.parsing.MyParserErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class STSyntaxHighlighter extends SyntaxHighlighter {
	public static final TextAttributesKey TEMPLATE_TEXT =
		createTextAttributesKey("TEMPLATE_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
	static {
		TEMPLATE_TEXT.getDefaultAttributes().setForegroundColor(JBColor.gray);
	}

	protected STLexer lexer = null;

	public STSyntaxHighlighter(Editor editor, int startIndex) {
		super(editor, startIndex);
	}

	@NotNull
	@Override
	public CommonTokenStream tokenize(String text) {
		lexer = new STLexer(text);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens;
	}

	@Override
	protected void highlightToken(Token t, int start) {
		if ( t.getType()==STLexer.STRING ) { // don't highlight start/end quotes as emitted text
			CommonToken t2 = new CommonToken(t);
			t2.setStartIndex(t.getStartIndex()+1);
			t2.setStopIndex(t.getStopIndex()-1);
			super.highlightToken(t2, start);
		}
		else {
			super.highlightToken(t, start);
		}
	}

	@Override
	public ParserRuleContext parse(CommonTokenStream tokens) {
		MyParserErrorListener errorListener = new MyParserErrorListener();
		STParser parser = new STParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		ParserRuleContext tree = parser.template();
		for (MyParserErrorListener.Issue I : errorListener.issues) {
			syntaxError(I.annotation, I.offendingToken);
		}
		return tree;
	}

	protected void syntaxError(String annotation, Token offendingToken) {
		Annotation annot = new Annotation(20, 30, HighlightSeverity.ERROR, "Test!", "Test Message!");
		HighlightInfo info = HighlightInfo.fromAnnotation(annot);
		List<HighlightInfo> al = new ArrayList<HighlightInfo>();
		al.add(info);
//		UpdateHighlightersUtil.setHighlightersToEditor(project, doc, 20, 30, al, null, 0)

		final TextAttributes attr = new TextAttributes();
		attr.setForegroundColor(JBColor.RED);
		attr.setEffectColor(JBColor.RED);
		attr.setEffectType(EffectType.WAVE_UNDERSCORE);
		MarkupModel markupModel = editor.getMarkupModel();
		RangeHighlighter highlighter =
			markupModel.addRangeHighlighter(startIndex+offendingToken.getStartIndex(),
			                                startIndex+offendingToken.getStopIndex()+1,
			                                HighlighterLayer.ERROR, // layer
			                                attr,
			                                HighlighterTargetArea.EXACT_RANGE);
	}

	@Override
	protected void highlightTree(ParserRuleContext tree, CommonTokenStream tokens) {
//		XPath.findAll(tree, "", new STParser(tokens));
	}

	@NotNull
	@Override
	public TextAttributesKey[] getAttributesKey(int tokenType) {
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
			case STLexer.ID:
				key = DefaultLanguageHighlighterColors.LOCAL_VARIABLE;
				break;
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
