/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
lexer grammar ConditionLexer;


@header {
package org.vclipse.condition.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.vclipse.dependency.lexer.Lexer;
}


KEYWORD_61 : '$'('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_60 : '$'('S'|'s')('U'|'u')('M'|'m')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_58 : ('P'|'p')('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_59 : ('S'|'s')('P'|'p')('E'|'e')('C'|'c')('I'|'i')('F'|'f')('I'|'i')('E'|'e')('D'|'d');

KEYWORD_56 : ('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_57 : ('M'|'m')('A'|'a')('T'|'t')('E'|'e')('R'|'r')('I'|'i')('A'|'a')('L'|'l');

KEYWORD_54 : '$'('P'|'p')('A'|'a')('R'|'r')('E'|'e')('N'|'n')('T'|'t');

KEYWORD_55 : ('T'|'t')('Y'|'y')('P'|'p')('E'|'e')'_'('O'|'o')('F'|'f');

KEYWORD_51 : ('A'|'a')('R'|'r')('C'|'c')('C'|'c')('O'|'o')('S'|'s');

KEYWORD_52 : ('A'|'a')('R'|'r')('C'|'c')('S'|'s')('I'|'i')('N'|'n');

KEYWORD_53 : ('A'|'a')('R'|'r')('C'|'c')('T'|'t')('A'|'a')('N'|'n');

KEYWORD_44 : '$'('R'|'r')('O'|'o')('O'|'o')('T'|'t');

KEYWORD_45 : '$'('S'|'s')('E'|'e')('L'|'l')('F'|'f');

KEYWORD_46 : ('F'|'f')('L'|'l')('O'|'o')('O'|'o')('R'|'r');

KEYWORD_47 : ('L'|'l')('O'|'o')('G'|'g')'1''0';

KEYWORD_48 : ('M'|'m')('D'|'d')('A'|'a')('T'|'t')('A'|'a');

KEYWORD_49 : ('T'|'t')('A'|'a')('B'|'b')('L'|'l')('E'|'e');

KEYWORD_50 : ('T'|'t')('R'|'r')('U'|'u')('N'|'n')('K'|'k');

KEYWORD_40 : ('C'|'c')('E'|'e')('I'|'i')('L'|'l');

KEYWORD_41 : ('F'|'f')('R'|'r')('A'|'a')('C'|'c');

KEYWORD_42 : ('S'|'s')('I'|'i')('G'|'g')('N'|'n');

KEYWORD_43 : ('S'|'s')('Q'|'q')('R'|'r')('T'|'t');

KEYWORD_33 : ('A'|'a')('B'|'b')('S'|'s');

KEYWORD_34 : ('A'|'a')('N'|'n')('D'|'d');

KEYWORD_35 : ('C'|'c')('O'|'o')('S'|'s');

KEYWORD_36 : ('E'|'e')('X'|'x')('P'|'p');

KEYWORD_37 : ('N'|'n')('O'|'o')('T'|'t');

KEYWORD_38 : ('S'|'s')('I'|'i')('N'|'n');

KEYWORD_39 : ('T'|'t')('A'|'a')('N'|'n');

KEYWORD_15 : '<''=';

KEYWORD_16 : '<''>';

KEYWORD_17 : '=''<';

KEYWORD_18 : '=''>';

KEYWORD_19 : '>''=';

KEYWORD_20 : ('E'|'e')('Q'|'q');

KEYWORD_21 : ('G'|'g')('E'|'e');

KEYWORD_22 : ('G'|'g')('T'|'t');

KEYWORD_23 : ('I'|'i')('N'|'n');

KEYWORD_24 : ('L'|'l')('C'|'c');

KEYWORD_25 : ('L'|'l')('E'|'e');

KEYWORD_26 : ('L'|'l')('N'|'n');

KEYWORD_27 : ('L'|'l')('T'|'t');

KEYWORD_28 : ('N'|'n')('E'|'e');

KEYWORD_29 : ('N'|'n')('R'|'r');

KEYWORD_30 : ('O'|'o')('R'|'r');

KEYWORD_31 : ('U'|'u')('C'|'c');

KEYWORD_32 : '|''|';

KEYWORD_1 : '#';

KEYWORD_2 : '(';

KEYWORD_3 : ')';

KEYWORD_4 : '*';

KEYWORD_5 : '+';

KEYWORD_6 : ',';

KEYWORD_7 : '-';

KEYWORD_8 : '.';

KEYWORD_9 : '/';

KEYWORD_10 : '<';

KEYWORD_11 : '=';

KEYWORD_12 : '>';

KEYWORD_13 : '?';

KEYWORD_14 : ('E'|'e');



RULE_ID : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

RULE_SL_COMMENT : {atStartOfLine()}? => '*' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_SYMBOL : '\'' ~(('\''|'\t'|'\n'|'\r'))* '\'';

RULE_INT : ('0'..'9')+;

RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

RULE_WS : (' '|'\t'|'\r'|'\n')+;

RULE_ANY_OTHER : .;



