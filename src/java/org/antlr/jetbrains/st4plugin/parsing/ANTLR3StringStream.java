package org.antlr.jetbrains.st4plugin.parsing;

import java.util.ArrayList;
import java.util.List;

public class ANTLR3StringStream {
	public class CharStreamState {
		/** Index into the char stream of next lookahead char */
		int p;

		/** What line number is the scanner at before processing buffer[p]? */
		int line;

		/** What char position 0..n-1 in line is scanner before processing buffer[p]? */
		int charPositionInLine;
	}

	/** The data being scanned */
	protected char[] data;

	/** How many characters are actually in the buffer */
	protected int n;

	/** 0..n-1 index into string of next char */
	protected int p=0;

	/** line number 1..n within the input */
	protected int line = 1;

	/** The index of the character relative to the beginning of the line 0..n-1 */
	protected int charPositionInLine = 0;

	/** tracks how deep mark() calls are nested */
	protected int markDepth = 0;

	/** A list of CharStreamState objects that tracks the stream state
	 *  values line, charPositionInLine, and p that can change as you
	 *  move through the input stream.  Indexed from 1..markDepth.
     *  A null is kept @ index 0.  Create upon first call to mark().
	 */
	protected List<CharStreamState> markers;

	/** Track the last mark() call result value for use in rewind(). */
	protected int lastMarker;

	/** What is name or source of this char stream? */
	public String name;

	public ANTLR3StringStream() {
	}

	/** Copy data in string to a local char array */
	public ANTLR3StringStream(String input) {
		this();
		this.data = input.toCharArray();
		this.n = input.length();
	}

	/** This is the preferred constructor as no data is copied */
	public ANTLR3StringStream(char[] data, int numberOfActualCharsInArray) {
		this();
		this.data = data;
		this.n = numberOfActualCharsInArray;
	}

	/** Reset the stream so that it's in the same state it was
	 *  when the object was created *except* the data array is not
	 *  touched.
	 */
	public void reset() {
		p = 0;
		line = 1;
		charPositionInLine = 0;
		markDepth = 0;
	}

    public void consume() {
		//System.out.println("prev p="+p+", c="+(char)data[p]);
        if ( p < n ) {
			charPositionInLine++;
			if ( data[p]=='\n' ) {
				/*
				System.out.println("newline char found on line: "+line+
								   "@ pos="+charPositionInLine);
				*/
				line++;
				charPositionInLine=0;
			}
            p++;
			//System.out.println("p moves to "+p+" (c='"+(char)data[p]+"')");
        }
    }

    public int LA(int i) {
		if ( i==0 ) {
			return 0; // undefined
		}
		if ( i<0 ) {
			i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
			if ( (p+i-1) < 0 ) {
				return -1; // invalid; no char before first char
			}
		}

		if ( (p+i-1) >= n ) {
            //System.out.println("char LA("+i+")=EOF; p="+p);
            return -1;
        }
        //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
		//System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
		return data[p+i-1];
    }

	public int LT(int i) {
		return LA(i);
	}

	/** Return the current input symbol index 0..n where n indicates the
     *  last symbol has been read.  The index is the index of char to
	 *  be returned from LA(1).
     */
    public int index() {
        return p;
    }

	public int size() {
		return n;
	}

	public int mark() {
        if ( markers==null ) {
            markers = new ArrayList<CharStreamState>();
            markers.add(null); // depth 0 means no backtracking, leave blank
        }
        markDepth++;
		CharStreamState state;
		if ( markDepth>=markers.size() ) {
			state = new CharStreamState();
			markers.add(state);
		}
		else {
			state = markers.get(markDepth);
		}
		state.p = p;
		state.line = line;
		state.charPositionInLine = charPositionInLine;
		lastMarker = markDepth;
		return markDepth;
    }

    public void rewind(int m) {
		CharStreamState state = markers.get(m);
		// restore stream state
		seek(state.p);
		line = state.line;
		charPositionInLine = state.charPositionInLine;
		release(m);
	}

	public void rewind() {
		rewind(lastMarker);
	}

	public void release(int marker) {
		// unwind any other markers made after m and release m
		markDepth = marker;
		// release this marker
		markDepth--;
	}

	/** consume() ahead until p==index; can't just set p=index as we must
	 *  update line and charPositionInLine.
	 */
	public void seek(int index) {
		if ( index<=p ) {
			p = index; // just jump; don't update stream state (line, ...)
			return;
		}
		// seek forward, consume until p hits index
		while ( p<index ) {
			consume();
		}
	}

	public String substring(int start, int stop) {
		return new String(data,start,stop-start+1);
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void setCharPositionInLine(int pos) {
		this.charPositionInLine = pos;
	}

	public String getSourceName() {
		return name;
	}

    public String toString() { return new String(data); }
}
