package org.antlr.jetbrains.st4plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Key;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

public abstract class SyntaxHighlighter {
	public static final Key<Token> HIGHLIGHT_CATEGORY_KEY = Key.create("HIGHLIGHT_LAYER_KEY");
	public static final TextAttributesKey[] NO_ATTR =
		new TextAttributesKey[] {TextAttributesKey.createTextAttributesKey("NO_ATTR")};

	final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();

	@NotNull
	public abstract CommonTokenStream tokenize(String text);

	/** Override this if you want to do some simple context-sensitive
	 *  highlighting. For general case, a real parser should be used.
	 *  Most of the time, just override {@link #getAttributesKey(int)}.
	 */
	@NotNull
	public TextAttributesKey[] getAttributesKey(CommonTokenStream tokens, int tokenIndex) {
		Token t = tokens.get(tokenIndex);
		return getAttributesKey(t.getType());
	}

	/** Implement this to map token types to attributes.
	 *  @return Key for attribute or NO_ATTR if no highlighting desired.
	 */
	@NotNull
	public abstract TextAttributesKey[] getAttributesKey(int tokenType);

	/** We tag highlighters with a key so we can remove all of them
	 *  before colorizing again.  Ignore this if you only have one
	 *  layer of highlighting.  E.g., StringTemplate has group files
	 *  and then syntax within each template that must be highlighted
	 *  differently.
	 */
	@NotNull
	public Key<Token> getHighlightCategoryKey() {
		return HIGHLIGHT_CATEGORY_KEY;
	}

	public void highlight(Editor editor) {
		String text = editor.getDocument().getText();
		highlight(editor, text, 0, text.length()-1);
	}

	/** Highlight tokens in editor and assume all token char indexes
	 *  are relative to start and <= stop (inclusive). This is useful for embedded
	 *  languages.
	 */
	public void highlight(Editor editor, String text, int start, int stop) {
		MarkupModel markupModel = editor.getMarkupModel();
		removeHighlighters(editor, getHighlightCategoryKey(), start, stop);

		CommonTokenStream tokens = tokenize(text);

		final EditorColorsScheme scheme =
			editorColorsManager.getScheme(EditorColorsScheme.DEFAULT_SCHEME_NAME);
		for (int i=0; i<tokens.size(); i++) {
			if ( tokens.get(i).getType()==Token.EOF ) break;
			TextAttributesKey[] keys = getAttributesKey(tokens, i);
			if ( keys!=NO_ATTR ) {
				TextAttributes attr = merge(scheme, keys);
				Token t = tokens.get(i);
				RangeHighlighter h =
					markupModel.addRangeHighlighter(
						start+t.getStartIndex(),
						start+t.getStopIndex()+1,
						HighlighterLayer.SYNTAX, // layer
						attr,
						HighlighterTargetArea.EXACT_RANGE);
				h.putUserData(getHighlightCategoryKey(), t);
			}
		}
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

	public static void removeHighlighters(Editor editor, Key<?> key, int start, int stop) {
		// Remove anything with user data accessible via key
		System.out.println("removeHighlighters: "+start+","+stop);
		MarkupModel markupModel = editor.getMarkupModel();
		for (RangeHighlighter r : markupModel.getAllHighlighters()) {
			if ( r.getUserData(key)!=null && r.getStartOffset()>=start && r.getEndOffset()<=stop+1 ) {
				markupModel.removeHighlighter(r);
			}
		}
	}
}
