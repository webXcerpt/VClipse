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
lexer grammar ConstraintLexer;


@header {
package org.vclipse.constraint.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.vclipse.dependency.lexer.Lexer;
}


KEYWORD_74 : '$'('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_75 : ('R'|'r')('E'|'e')('S'|'s')('T'|'t')('R'|'r')('I'|'i')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n')('S'|'s');

KEYWORD_73 : ('R'|'r')('E'|'e')('S'|'s')('T'|'t')('R'|'r')('I'|'i')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_70 : '$'('S'|'s')('U'|'u')('M'|'m')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_71 : ('I'|'i')('N'|'n')('F'|'f')('E'|'e')('R'|'r')('E'|'e')('N'|'n')('C'|'c')('E'|'e')('S'|'s');

KEYWORD_72 : ('S'|'s')('U'|'u')('B'|'b')('P'|'p')('A'|'a')('R'|'r')('T'|'t')'_'('O'|'o')('F'|'f');

KEYWORD_66 : ('C'|'c')('O'|'o')('N'|'n')('D'|'d')('I'|'i')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_67 : ('I'|'i')('S'|'s')'_'('O'|'o')('B'|'b')('J'|'j')('E'|'e')('C'|'c')('T'|'t');

KEYWORD_68 : ('P'|'p')('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_69 : ('S'|'s')('P'|'p')('E'|'e')('C'|'c')('I'|'i')('F'|'f')('I'|'i')('E'|'e')('D'|'d');

KEYWORD_64 : ('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_65 : ('M'|'m')('A'|'a')('T'|'t')('E'|'e')('R'|'r')('I'|'i')('A'|'a')('L'|'l');

KEYWORD_60 : '$'('P'|'p')('A'|'a')('R'|'r')('E'|'e')('N'|'n')('T'|'t');

KEYWORD_61 : ('O'|'o')('B'|'b')('J'|'j')('E'|'e')('C'|'c')('T'|'t')('S'|'s');

KEYWORD_62 : ('P'|'p')('A'|'a')('R'|'r')('T'|'t')'_'('O'|'o')('F'|'f');

KEYWORD_63 : ('T'|'t')('Y'|'y')('P'|'p')('E'|'e')'_'('O'|'o')('F'|'f');

KEYWORD_57 : ('A'|'a')('R'|'r')('C'|'c')('C'|'c')('O'|'o')('S'|'s');

KEYWORD_58 : ('A'|'a')('R'|'r')('C'|'c')('S'|'s')('I'|'i')('N'|'n');

KEYWORD_59 : ('A'|'a')('R'|'r')('C'|'c')('T'|'t')('A'|'a')('N'|'n');

KEYWORD_48 : '$'('R'|'r')('O'|'o')('O'|'o')('T'|'t');

KEYWORD_49 : '$'('S'|'s')('E'|'e')('L'|'l')('F'|'f');

KEYWORD_50 : ('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');

KEYWORD_51 : ('F'|'f')('L'|'l')('O'|'o')('O'|'o')('R'|'r');

KEYWORD_52 : ('L'|'l')('O'|'o')('G'|'g')'1''0';

KEYWORD_53 : ('M'|'m')('D'|'d')('A'|'a')('T'|'t')('A'|'a');

KEYWORD_54 : ('T'|'t')('A'|'a')('B'|'b')('L'|'l')('E'|'e');

KEYWORD_55 : ('T'|'t')('R'|'r')('U'|'u')('N'|'n')('K'|'k');

KEYWORD_56 : ('W'|'w')('H'|'h')('E'|'e')('R'|'r')('E'|'e');

KEYWORD_43 : ('C'|'c')('E'|'e')('I'|'i')('L'|'l');

KEYWORD_44 : ('F'|'f')('R'|'r')('A'|'a')('C'|'c');

KEYWORD_45 : ('I'|'i')('S'|'s')'_'('A'|'a');

KEYWORD_46 : ('S'|'s')('I'|'i')('G'|'g')('N'|'n');

KEYWORD_47 : ('S'|'s')('Q'|'q')('R'|'r')('T'|'t');

KEYWORD_36 : ('A'|'a')('B'|'b')('S'|'s');

KEYWORD_37 : ('A'|'a')('N'|'n')('D'|'d');

KEYWORD_38 : ('C'|'c')('O'|'o')('S'|'s');

KEYWORD_39 : ('E'|'e')('X'|'x')('P'|'p');

KEYWORD_40 : ('N'|'n')('O'|'o')('T'|'t');

KEYWORD_41 : ('S'|'s')('I'|'i')('N'|'n');

KEYWORD_42 : ('T'|'t')('A'|'a')('N'|'n');

KEYWORD_17 : '<''=';

KEYWORD_18 : '<''>';

KEYWORD_19 : '=''<';

KEYWORD_20 : '=''>';

KEYWORD_21 : '>''=';

KEYWORD_22 : ('E'|'e')('Q'|'q');

KEYWORD_23 : ('G'|'g')('E'|'e');

KEYWORD_24 : ('G'|'g')('T'|'t');

KEYWORD_25 : ('I'|'i')('F'|'f');

KEYWORD_26 : ('I'|'i')('N'|'n');

KEYWORD_27 : ('L'|'l')('C'|'c');

KEYWORD_28 : ('L'|'l')('E'|'e');

KEYWORD_29 : ('L'|'l')('N'|'n');

KEYWORD_30 : ('L'|'l')('T'|'t');

KEYWORD_31 : ('N'|'n')('E'|'e');

KEYWORD_32 : ('N'|'n')('R'|'r');

KEYWORD_33 : ('O'|'o')('R'|'r');

KEYWORD_34 : ('U'|'u')('C'|'c');

KEYWORD_35 : '|''|';

KEYWORD_1 : '#';

KEYWORD_2 : '(';

KEYWORD_3 : ')';

KEYWORD_4 : '*';

KEYWORD_5 : '+';

KEYWORD_6 : ',';

KEYWORD_7 : '-';

KEYWORD_8 : '.';

KEYWORD_9 : '/';

KEYWORD_10 : ':';

KEYWORD_11 : ';';

KEYWORD_12 : '<';

KEYWORD_13 : '=';

KEYWORD_14 : '>';

KEYWORD_15 : '?';

KEYWORD_16 : ('E'|'e');



RULE_ID : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

RULE_SL_COMMENT : {atStartOfLine()}? => '*' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_SYMBOL : '\'' ~(('\''|'\t'|'\n'|'\r'))* '\'';

RULE_INT : ('0'..'9')+;

RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

RULE_WS : (' '|'\t'|'\r'|'\n')+;

RULE_ANY_OTHER : .;



