package org.antlr.jetbrains.st4plugin;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.antlr.jetbrains.st4plugin.parsing.Issue;
import org.antlr.jetbrains.st4plugin.parsing.LexerErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParserErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParsingResult;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class XSyntaxHighlighter {
	public static final TextAttributesKey[] NO_ATTR =
		new TextAttributesKey[] {TextAttributesKey.createTextAttributesKey("NO_ATTR")};
	final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();

	protected Editor editor;
	protected int startIndex;

	public XSyntaxHighlighter(Editor editor, int startIndex) {
		this.editor = editor;
		this.startIndex = startIndex;
	}

	public abstract Lexer getLexer(String text);
	public abstract ParsingResult parse(CommonTokenStream tokens);
	public boolean isEmbeddedLanguageToken(Token t) { return false; }
	public void highlightEmbedded(Token t) { }
	public void highlightTree(ParserRuleContext tree, Parser parser) { }

	/** Implement this to map token types to attributes.
	 *  @return Key for attribute or NO_ATTR if no highlighting desired.
	 */
	@NotNull
	public abstract TextAttributesKey[] getAttributesKey(Token t);



	public void highlight() {
		MarkupModel markupModel = getEditor().getMarkupModel();
		markupModel.removeAllHighlighters();

		String text = getEditor().getDocument().getText();
		highlight(text);
	}

	/** Highlight tokens in editor and assume all token char indexes
	 *  are relative to start. This is useful for embedded
	 *  languages.
	 */
	public void highlight(String text) {
		Lexer lexer = getLexer(text);
		lexer.removeErrorListeners();
		LexerErrorListener lexerListener = new LexerErrorListener();
		lexer.addErrorListener(lexerListener);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		for (Issue I : lexerListener.issues) {
			lexicalError(I.annotation, I.offendingToken);
		}

//		System.out.println(tokens.getTokens());
		for (int i=0; i<tokens.size(); i++) {
			Token t = tokens.get(i);
			if ( t.getType()!=Token.EOF ) {
				if ( isEmbeddedLanguageToken(t) ) {
					highlightEmbedded(t);
				}
				else {
					highlightToken(t);
				}
			}
		}

		final ParsingResult results = parse(tokens);
		if ( results!=null ) {
			Parser parser = results.parser;
			ParserRuleContext tree = results.tree;
			ParserErrorListener syntaxListener = results.syntaxErrorListener;

			for (Issue I : syntaxListener.issues) {
				syntaxError(I.annotation, I.offendingToken);
			}

			highlightTree(tree, parser);
		}
	}

	protected void lexicalError(String annotation, Token offendingToken) {
		syntaxError(annotation, offendingToken);
	}

	protected void syntaxError(String annotation, Token offendingToken) {
//		Annotation annot = new Annotation(20, 30, HighlightSeverity.ERROR, "Test!", "Test Message!");
//		HighlightInfo info = HighlightInfo.fromAnnotation(annot);
//		List<HighlightInfo> al = new ArrayList<HighlightInfo>();
//		al.add(info);
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

	protected void highlightToken(Token t) {
		TextAttributesKey[] keys = getAttributesKey(t);
		if ( keys!=NO_ATTR ) {
			highlightToken(t, keys);
		}
	}

	protected void highlightToken(Token t, TextAttributesKey[] keys) {
		MarkupModel markupModel = getEditor().getMarkupModel();
		final EditorColorsScheme scheme =
			editorColorsManager.getScheme(EditorColorsScheme.DEFAULT_SCHEME_NAME);
		TextAttributes attr = merge(scheme, keys);
		RangeHighlighter h =
			markupModel.addRangeHighlighter(
				startIndex+t.getStartIndex(),
				startIndex+t.getStopIndex()+1,
				HighlighterLayer.SYNTAX, // layer
				attr,
				HighlighterTargetArea.EXACT_RANGE);
//			h.putUserData(getHighlightCategoryKey(), t);
	}

	public Editor getEditor() {
		return editor;
	}


	public static TextAttributes merge(EditorColorsScheme scheme, TextAttributesKey[] keys) {
		TextAttributes attrs = new TextAttributes();
		for (TextAttributesKey key : keys) {
			TextAttributes a = scheme.getAttributes(key);
			if (a != null) {
				attrs = TextAttributes.merge(attrs, a);
			}
		}
		return attrs;
	}
}
