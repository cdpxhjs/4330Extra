/*
 * This is the main program of the lexical Analyzer
 * The propose of this program is to read file that user input
 * 
 * 
 * 
 */

package lex;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LexicalAnalyzer {
	
	public static void main(String[] args){
		LexicalAnalyzer lex = new LexicalAnalyzer("source.c");//Read a C file for analysis
		ArrayList<Token> tokens = lex.getTokenList();
		try {
			lex.output(tokens, "result.txt");//The output of the program is a sequence of Tokens after the lexical analysis process, which is returned in an ArrayList<Token> to provide a data source for further analysis.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//throw error
		}
	}
	
	/*
	 * Scan, see "Scan" file
	 */
	private Scan scan;
	
	/**
	 * Constructor of LexicalAnalyzer
	 * @param filename The position of file and filename
	 */
	public LexicalAnalyzer(String filename){
		this.scan = new Scan(filename);
	}
	
	private static String outputPath = "Output/";
	/**
	 * Output the result into "Output/result.txt"
	 * @param list TokenList of the result of lexical analysis
	 * @param filename File Path and Filename
	 * @throws FileNotFoundException 
	 */
	//@SuppressWarnings("resource")
	public void output(ArrayList<Token> list,String filename) throws FileNotFoundException, IOException{
		filename = (LexicalAnalyzer.outputPath+filename);
		File file = new File(filename);
		while(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(file);
		for(int i = 0;i < list.size();i++){
			String str = "<"+list.get(i).type+","+list.get(i).value+">";
			pw.println(str);
		}
		pw.close();
	}
	
	/**
	 * Get the TokenList according to rules
	 * @return TokenList
	 */
	public ArrayList<Token> getTokenList(){
		ArrayList<Token> result = new ArrayList<Token>();
		int index = 0;
		while(index < scan.getLength()){
			Token token = analyze(index);//Analyze the current position
			result.add(token);//Write the result in the outputs
			index = scan.getIndex();
		}
		this.scan.retract(scan.getLength()-1);
		return result;
	}
	
	//Keywords of C
	private String[] keyword ={
		"auto","double","int","struct","break","else","long","switch",
		"case","enum","register","typedef","char","return","union","const",
		"extern","float","short","unsigned","continue","for","signed","void",
		"default","goto","sizeof","volatile","do","if","static","while","string"
	};

	private boolean flag = false;//distinguish strings in double quote marks
	/**
	 * Lexical analysis in one place
	 * @param index of pointer
	 * @return single token
	 */
	@SuppressWarnings("deprecation")
	public Token analyze(int index){
		int length = scan.getLength();
		String type = "-1";//IF no type matched, -1
		String value = "";
		while(index < length){
			char ch = scan.getNextChar();
			//System.out.println(ch);
			index++;
			char ch1 = '\0';
			if(isDigit(ch)){//is a digit?
				if(Type.isCalc(type)){
					scan.retract(1);
					break;
				}
				if(value == ""){
					value = new Character(ch).toString();
					type = Type.NUM;
				} else {
					value += new Character(ch).toString();
				}
				
			} else if (isLetter(ch)){//is a letter?
				if(Type.isCalc(type)){
					scan.retract(1);
					break;
				}
				
				/*
				if(type ==Type.VAR) {//is a var?
					value += new Character(ch).toString();
					continue;
				}
				*/

				
				if(flag){//string in double quote marks
					value = scan.getStringInQuotation(index);
					type = Type.ID;
					scan.move(value.length()-1);
					return new Token(type,value);
				}
				if(type == Type.ID){
					value += new Character(ch).toString();
					continue;
				}
				
				/*if(type == Type.VAR){
					value += new Character(ch).toString();
					continue;
				}*/
				
				String str = scan.getTestString(index);
				String val = null;
				if(str.startsWith("include")){
					val = "include";
					type = Type.INCLUDE;
				} 
				
				if(str.startsWith("@")) {//Variable name start with @
					val = str;
					type = Type.ID;
				}
				
				
				else {
					for(int i = 0;i < keyword.length;i++){
						if(str.startsWith(keyword[i])){
							val = keyword[i];
							type = keyword[i];
							break;
						}
					}
				}
				
				if(val == null){
					type = Type.ID;
					if(value == ""){
						value = new Character(ch).toString();
					} else {
						value += new Character(ch).toString();
					}
				} else {
					value = val;
					scan.move(value.length()-1);
					return new Token(type,value);
				}
				
			} else {
				if(type == Type.NUM || type == Type.ID){
					scan.retract(1);
					return new Token(type,value);
				}
				switch(ch){
				case '='://==,=
					if(type == "-1"){
						type = Type.ASSIGN;
						value = "=";
					} else if(type == Type.LT){//<=
						type = Type.LE;
						value = "<=";
						return new Token(type,value);
					} else if(type == Type.GT){//>=
						type = Type.GE;
						value = ">=";
						return new Token(type,value);
					} else if(type == Type.ASSIGN){//==
						type = Type.EQUAL;
						value = "==";
						return new Token(type,value);
					} else if(type == Type.NOT){//!=
						type = Type.NE;
						value = "!=";
						return new Token(type,value);
					} else if(type == Type.ADD){//+=
						type = Type.INCREASEBY;
						value = "+=";
						return new Token(type,value);
					} else if(type == Type.SUB){//-=
						type = Type.DECREASEBY;
						value = "-=";
						return new Token(type,value);
					} else if(type == Type.DIV){///=
						type = Type.DIVBY;
						value = "/=";
						return new Token(type,value);
					} else if(type == Type.MUL){//*=
						type = Type.MULBY;
						value = "*=";
						return new Token(type,value);
					}
					break;
				case '+':
					if(type == "-1"){
						type = Type.ADD;
						value = "+";
					} else if(type == Type.ADD){//++
						type = Type.INCREASE;
						value = "++";
						return new Token(type,value);
					} 
					break;
				case '-':
					if(type == "-1"){
						type = Type.SUB;
						value = "-";
					} else if(type == Type.SUB){//--
						type = Type.DECREASEBY;
						value = "--";
						return new Token(type,value);
					}
					break;
				case '*':
					if(type == "-1"){
						type = Type.MUL;
						value = "*";
					} 
					break;
				case '/':
					if(type == "-1"){
						type = Type.DIV;
						value = "/";
					}
					break;
				case '<':
					if(type == "-1"){
						type = Type.LT;
						value = "<";
					}
					break;
				case '>':
					if(type == "-1"){
						type = Type.GT;
						value = ">";
					}
					break;
				case '!':
					if(type == "-1"){
						type = Type.NOT;
						value = "!";
					}
					break;
				case '|':
					if(type == "-1"){
						type = Type.OR_1;
						value = "|";
					} else if(type == Type.OR_1){
						type = Type.OR_2;
						value = "||";
						return new Token(type,value);
					}
					break;
				case '&':
					if(type == "-1"){
						type = Type.AND_1;
						value = "&";
					} else if(type == Type.AND_1){
						type = Type.AND_2;
						value = "&&";
						return new Token(type,value);
					}
					break;
				case ';':
					if(type == "-1"){
						type = Type.SEMICOLON;
						value = ";";
					}
					break;
				case '{':
					if(type == "-1"){
						type = Type.BRACE_L;
						value = "{";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '}':
					if(type == "-1"){
						type = Type.BRACE_R;
						value = "}";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '[':
					if(type == "-1"){
						type = Type.BRACKET_L;
						value = "[";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case ']':
					if(type == "-1"){
						type = Type.BRACKET_R;
						value = "]";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '(':
					if(type == "-1"){
						type = Type.PARENTHESIS_L;
						value = "(";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					} 
					break;
				case ')':
					if(type == "-1"){
						type = Type.PARENTHESIS_R;
						value = ")";
					} else if(Type.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '#':
					if(type == "-1"){
						type = Type.POUND;
						value = "#";
					}
					break;
				case ',':
					if(type == "-1"){
						type = Type.COMMA;
						value = ",";
					}
					break;
				case '\'':
					if(type == "-1"){
						type = Type.SINGLE_QUOTAOTION;
						value = "\'";
					}
					break;
				case '"':
					if(flag == false){
						flag = true;//This is the first double-quote mark
					} else {
						flag = false;
					}
					if(type == "-1"){
						type = Type.DOUBLE_QUOTATION;
						value = "\"";
					}
					break;
				default:
					break;
				}
				if(!Type.isCalc(type)){
					break;
				}
			}
		}
		if(value.length()>1){
			scan.move(value.length()-1);
		}
		Token token = new Token(type,value);
		return token;
	}
	
	//is a digit?
	private boolean isDigit(char c){
		if((c<='9'&&c>='0')||c=='.'){
			return true;
		} else {
			return false;
		}
	}
	//is a letter?
	private boolean isLetter(char c){
		if((c>='a'&&c<='z')||c=='_'||(c>='A'&&c<='Z')||c=='@'){
			return true;
		} else {
			return false;
		}
	}
	
	
	/*is a id? id start at @
	 *But in main analyzer, isLetter() Function, '@' is also a letter
	 *So I add another @VAR distinguisher in analyzer isLetter() function
	 */
	@SuppressWarnings("unused")
	private boolean isVriLetter(char c){
		String s=Character.toString(c);
		char fit=s.charAt(0);
		if(fit=='@'){
			return true;
		} else {
			return false;
		}
	}
	
	//is a function name? start with a letter and followed by any number of letters
	@SuppressWarnings("unused")
	private boolean isFucLetter(char c){
		String s=Character.toString(c);
		char fit=s.charAt(0);
		if((fit>='a'&&fit<='z')||(fit>='A'&&fit<='Z')){
			return true;
		} else {
			return false;
		}
	}
	
	
}
