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
lexer grammar ProcedureLexer;


@header {
package org.vclipse.procedure.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.vclipse.dependency.lexer.Lexer;
}

KEYWORD_68 : '$'('S'|'s')('E'|'e')('T'|'t')'_'('P'|'p')('R'|'r')('I'|'i')('C'|'c')('I'|'i')('N'|'n')('G'|'g')'_'('F'|'f')('A'|'a')('C'|'c')('T'|'t')('O'|'o')('R'|'r');

KEYWORD_65 : '$'('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_66 : '$'('D'|'d')('E'|'e')('L'|'l')'_'('D'|'d')('E'|'e')('F'|'f')('A'|'a')('U'|'u')('L'|'l')('T'|'t');

KEYWORD_67 : '$'('S'|'s')('E'|'e')('T'|'t')'_'('D'|'d')('E'|'e')('F'|'f')('A'|'a')('U'|'u')('L'|'l')('T'|'t');

KEYWORD_64 : '$'('S'|'s')('U'|'u')('M'|'m')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t')('S'|'s');

KEYWORD_61 : ('I'|'i')('N'|'n')('V'|'v')('I'|'i')('S'|'s')('I'|'i')('B'|'b')('L'|'l')('E'|'e');

KEYWORD_62 : ('P'|'p')('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_63 : ('S'|'s')('P'|'p')('E'|'e')('C'|'c')('I'|'i')('F'|'f')('I'|'i')('E'|'e')('D'|'d');

KEYWORD_59 : ('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');

KEYWORD_60 : ('M'|'m')('A'|'a')('T'|'t')('E'|'e')('R'|'r')('I'|'i')('A'|'a')('L'|'l');

KEYWORD_57 : '$'('P'|'p')('A'|'a')('R'|'r')('E'|'e')('N'|'n')('T'|'t');

KEYWORD_58 : ('T'|'t')('Y'|'y')('P'|'p')('E'|'e')'_'('O'|'o')('F'|'f');

KEYWORD_54 : ('A'|'a')('R'|'r')('C'|'c')('C'|'c')('O'|'o')('S'|'s');

KEYWORD_55 : ('A'|'a')('R'|'r')('C'|'c')('S'|'s')('I'|'i')('N'|'n');

KEYWORD_56 : ('A'|'a')('R'|'r')('C'|'c')('T'|'t')('A'|'a')('N'|'n');

KEYWORD_47 : '$'('R'|'r')('O'|'o')('O'|'o')('T'|'t');

KEYWORD_48 : '$'('S'|'s')('E'|'e')('L'|'l')('F'|'f');

KEYWORD_49 : ('F'|'f')('L'|'l')('O'|'o')('O'|'o')('R'|'r');

KEYWORD_50 : ('L'|'l')('O'|'o')('G'|'g')'1''0';

KEYWORD_51 : ('M'|'m')('D'|'d')('A'|'a')('T'|'t')('A'|'a');

KEYWORD_52 : ('T'|'t')('A'|'a')('B'|'b')('L'|'l')('E'|'e');

KEYWORD_53 : ('T'|'t')('R'|'r')('U'|'u')('N'|'n')('K'|'k');

KEYWORD_43 : ('C'|'c')('E'|'e')('I'|'i')('L'|'l');

KEYWORD_44 : ('F'|'f')('R'|'r')('A'|'a')('C'|'c');

KEYWORD_45 : ('S'|'s')('I'|'i')('G'|'g')('N'|'n');

KEYWORD_46 : ('S'|'s')('Q'|'q')('R'|'r')('T'|'t');

KEYWORD_36 : ('A'|'a')('B'|'b')('S'|'s');

KEYWORD_37 : ('A'|'a')('N'|'n')('D'|'d');

KEYWORD_38 : ('C'|'c')('O'|'o')('S'|'s');

KEYWORD_39 : ('E'|'e')('X'|'x')('P'|'p');

KEYWORD_40 : ('N'|'n')('O'|'o')('T'|'t');

KEYWORD_41 : ('S'|'s')('I'|'i')('N'|'n');

KEYWORD_42 : ('T'|'t')('A'|'a')('N'|'n');

KEYWORD_15 : '<''=';

KEYWORD_16 : '<''>';

KEYWORD_17 : '=''<';

KEYWORD_18 : '=''>';

KEYWORD_19 : '>''=';

KEYWORD_20 : '?''=';

KEYWORD_21 : ('E'|'e')('Q'|'q');

KEYWORD_22 : ('G'|'g')('E'|'e');

KEYWORD_23 : ('G'|'g')('T'|'t');

KEYWORD_24 : ('I'|'i')('F'|'f');

KEYWORD_25 : ('I'|'i')('N'|'n');

KEYWORD_26 : ('I'|'i')('S'|'s');

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



