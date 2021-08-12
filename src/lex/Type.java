/*
 * Basic keywords and operation symbols list for C
 */

package lex;

public class Type {
	//Keywords
	public static final String AUTO = "auto";
	public static final String DOUBLE = "double";
	public static final String INT = "int";
	public static final String STRUCT = "struct";
	public static final String BREAK = "break";
	public static final String ELSE = "else";
	public static final String LONG = "long";
	public static final String SWITCH = "switch";
	public static final String CASE = "case";
	public static final String ENUM = "enum";
	public static final String REGISTER = "register";
	public static final String TYPEDEF = "typedef";
	public static final String CHAR = "char";
	public static final String EXTERN = "extern";
	public static final String RETURN = "return";
	public static final String UNION = "union";
	public static final String CONST = "const";
	public static final String FLOAT = "float";
	public static final String SHORT = "short";
	public static final String UNSIGNED = "unsigned";
	public static final String CONTINUE = "continue";
	public static final String FOR = "for";
	public static final String SIGNED = "signed";
	public static final String VOID = "void";
	public static final String DEFAULT = "default";
	public static final String GOTO = "goto";
	public static final String SIZEOF = "sizeof";
	public static final String VOLATILE = "volatile";
	public static final String DO = "do";
	public static final String IF = "if";
	public static final String STATIC = "static";
	public static final String WHILE = "while";
	public static final String STRING = "string";
	//Mathematics operations
	public static final String ASSIGN = "ASSIGN";
	public static final String ADD = "ADD";//+
	public static final String SUB = "SUB";//-
	public static final String DIV = "DIV";//\
	public static final String LT = "LT";//<
	public static final String LE = "LE";//<=
	public static final String GT = "GT";//>
	public static final String GE = "GE";//>=
	public static final String NE = "NE";//!=
	public static final String EQUAL = "EQUAL";//==
	public static final String OR_1 = "OR_1";//|
	public static final String OR_2 = "OR_2";//||
	public static final String AND_1 = "AND_1";//=
	public static final String AND_2 = "AND_2";//==
	public static final String NOT = "NOT";//!
	public static final String XOR = "XOR";
	public static final String INCREASE = "INCREASE";//++
	public static final String DECREASE = "DECREASE";//--
	public static final String COMMA = "COMMA";//,
	public static final String SEMICOLON = "SEMICOLON";//;
	public static final String BRACE_L = "BRACE_L";//{
	public static final String BRACE_R = "BRACE_R";//}
	public static final String BRACKET_L = "BRACKET_L";//[
	public static final String BRACKET_R = "BRACKET_R";//]
	public static final String PARENTHESIS_L = "PARENTHESIS_L";//(
	public static final String PARENTHESIS_R = "PARENTHESIS_R";//)
	public static final String POUND = "POUND";//#
	//ID and NUM
	public static final String ID = "ID";
	public static final String NUM = "NUM";
	
	public static final String VAR = "VAR";
	//public static final String MAIN = 70;
	//public static final String PRINTF = 71;
	
	//Supplement
	public static final String INCREASEBY = "INCREASEBY";//+=
	public static final String DECREASEBY = "DECREASEBY";//-=
	public static final String MULBY = "MULBY";//*=
	public static final String DIVBY = "DIVBY";///=
	public static final String MUL = "MUL";//*
	public static final String INCLUDE = "INCLUDE";
	public static final String SINGLE_QUOTAOTION = "SINGLE_QUOTAOTION";
	public static final String DOUBLE_QUOTATION = "DOUBLE_QUOTATION";
	public static final String TRANSFER = "TRANSFER";
	
	/**
	 * Judge if the mark is about calculation
	 * @param type Input String
	 * @return
	 */
	public static boolean isCalc(String type){
		if(type == Type.ASSIGN || type == Type.ADD || type == Type.SUB || type == Type.DIV ||
				type == Type.MUL ||type == Type.LT || type == Type.GT || type == Type.OR_1 ||
				type == Type.AND_1 || type == Type.NOT){
			return true;
		} else {
			return false;
		}
	}
}
