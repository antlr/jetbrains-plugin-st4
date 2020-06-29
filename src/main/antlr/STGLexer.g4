/*
 * [The "BSD license"]
 * Copyright (c) 2011-2014 Terence Parr
 * Copyright (c) 2015 Gerald Rosenberg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

 /**
 *	A grammar for StringTemplate v4 implemented using Antlr v4 syntax
 *
 *	Modified 2015.06.16 gbr
 *	-- update for compatibility with Antlr v4.5
 *	-- use imported standard fragments
 */

lexer grammar STGLexer;

import LexBasic;	// Standard set of fragments

// ------------------------------------------------------------------------------
// mode default

COMMENT		        : BlockComment		-> channel(HIDDEN)	;
LINE_COMMENT		: LineComment		-> channel(HIDDEN)	;

WS					: [ \r\n\t]+		-> channel(HIDDEN)	;

STRING_START		: DQuote			-> more, pushMode(STRING_MODE) ;
ANON_TEMPLATE		: LBrace ('\\}'|~'}')* RBrace ;
BIGSTRING_START		: LDAngle 			-> more, pushMode(BIGSTRING_MODE) ;
BIGSTRING_NO_NL_START : LPct			-> more, pushMode(BIGSTRING_NO_NL_MODE) ;


// -----------------------------------
// Symbols

TMPL_ASSIGN	: TmplAssign	;
ASSIGN		: Equal			;

DOT			: Dot			;
COMMA		: Comma			;
COLON		: Colon			;
SEMI        : Semi          ;
LPAREN		: LParen		;
RPAREN		: RParen		;
LBRACK		: LBrack		;
RBRACK		: RBrack		;
AT			: At			;
TRUE		: True			;
FALSE		: False			;
ELLIPSIS	: Ellipsis		;

// -----------------------------------
// Key words

DELIMITERS	: 'delimiters'	;
IMPORT		: 'import'		;
DEFAULT		: 'default'		;
KEY			: 'key'			;

GROUP		: 'group'		;	// for compatibility with V3 syntax
IMPLEMENTS  : 'implements'  ;	// for compatibility with V3 syntax

ID        	: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')* ;

// -----------------------------------
// Grammar specific fragments

fragment TmplAssign	: '::='		;
fragment LPct		: '<%'		;
fragment RPct		: '%>'		;
fragment LDAngle	: LShift	;
fragment RDAngle	: RShift	;

ERRCHAR             : . -> channel (HIDDEN)   ;

mode STRING_MODE;

STRING_ESC		: '\\"' {setText(getText()+"\"");} 	-> more ;
STRING_NL		: '\n' 		-> more ; // match but it should be an error
STRING			: '"' 		-> popMode ;
EOF_STRING      : EOF       -> popMode ;
STRING_TEXT		: .			-> more;

mode BIGSTRING_MODE;

BIGSTRING_ESC		: '\\>' 	-> more ;
BIGSTRING			: '>>' 		-> popMode ;
EOF_BIGSTRING       : EOF       -> popMode ;
BIGSTRING_TEXT		: .			-> more;

mode BIGSTRING_NO_NL_MODE;

BIGSTRING_NO_NL_ESC	: '\\%' 	-> more;
BIGSTRING_NO_NL		: '%>'		-> popMode ;
EOF_BIGSTRING_NO_NL : EOF       -> popMode ;
BIGSTRING_NO_NL_TEXT: . 		-> more;


