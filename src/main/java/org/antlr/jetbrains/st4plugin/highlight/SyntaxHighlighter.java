package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.antlr.jetbrains.st4plugin.parsing.Issue;
import org.antlr.jetbrains.st4plugin.parsing.LexerErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParserErrorListener;
import org.antlr.jetbrains.st4plugin.parsing.ParsingResult;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SyntaxHighlighter {
	public static final Key<Object> SYNTAX_HIGHLIGHTING_TAG = Key.create("SYNTAX_HIGHLIGHTING_TAG");
	protected static final TextAttributesKey[] EMPTY = new TextAttributesKey[0];

	final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();

	protected Editor editor;
	protected int startIndex;
	protected List<HighlightInfo> highlightInfos;

	public SyntaxHighlighter(Editor editor, int startIndex, List<HighlightInfo> highlightInfos) {
		this.editor = editor;
		this.startIndex = startIndex;
		this.highlightInfos = highlightInfos;
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
		removeHighlighters(getEditor(), SYNTAX_HIGHLIGHTING_TAG);
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

		// parse first so we set delimiters
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

		Project project = editor.getProject();

		if (project != null) {
			UpdateHighlightersUtil.setHighlightersToEditor(
					project, editor.getDocument(), 0, editor.getDocument().getTextLength(),
					highlightInfos, editorColorsManager.getGlobalScheme(), 0);
		}
	}

	protected void lexicalError(String annotation, Token offendingToken) {
		syntaxError(annotation, offendingToken);
	}

	protected void syntaxError(String annotation, Token offendingToken) {
		HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR)
				.range(startIndex + offendingToken.getStartIndex(), startIndex + offendingToken.getStopIndex() + 1)
				.descriptionAndTooltip(annotation)
				.create();
		highlightInfos.add(highlightInfo);
	}

	protected void highlightToken(Token t) {
		TextAttributesKey[] keys = getAttributesKey(t);
		if ( keys!=EMPTY ) {
			highlightToken(t, keys);
		}
	}

	protected void highlightToken(Token t, TextAttributesKey[] keys) {
		MarkupModel markupModel = getEditor().getMarkupModel();
		EditorColorsScheme scheme = editorColorsManager.getGlobalScheme();
		TextAttributes attr = merge(scheme, keys);
		RangeHighlighter h =
			markupModel.addRangeHighlighter(
				startIndex+t.getStartIndex(),
				startIndex+t.getStopIndex()+1,
				HighlighterLayer.SYNTAX, // layer
				attr,
				HighlighterTargetArea.EXACT_RANGE);
		h.putUserData(SYNTAX_HIGHLIGHTING_TAG, t); // store any non-null value to tag it
	}

	protected static void removeHighlighters(Editor editor, Key taggedWithThis) {
		final MarkupModel model = editor.getMarkupModel();
		ArrayList<RangeHighlighter> toRemove = new ArrayList<RangeHighlighter>();
		for (RangeHighlighter highlighter : model.getAllHighlighters()) {
			if ( highlighter.getUserData(taggedWithThis) != null ) {
				toRemove.add(highlighter);
			}
		}
		for (RangeHighlighter highlighter : toRemove) {
			model.removeHighlighter(highlighter);
		}
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
