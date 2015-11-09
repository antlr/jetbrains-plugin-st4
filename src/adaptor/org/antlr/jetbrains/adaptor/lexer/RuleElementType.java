package org.antlr.jetbrains.adaptor.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a specific ANTLR rule in the language of the plug-in and is the
 *  intellij "token type" of an interior PSI tree node. The IntelliJ equivalent
 *  of ANTLR RuleNode.getRuleIndex() method.
 *
 *  Intellij Lexer token types are instances of IElementType.
 *  We differentiate between parse tree subtree roots and tokens with
 *  {@link RuleElementType} and {@link TokenElementType}.
 */
public class RuleElementType extends IElementType {
	private final int ruleIndex;

	public RuleElementType(int ruleIndex,
	                       @NotNull @NonNls String debugName,
	                       @Nullable Language language)
	{
		super(debugName, language);
		this.ruleIndex = ruleIndex;
	}

	public int getRuleIndex() {
		return ruleIndex;
	}
}
