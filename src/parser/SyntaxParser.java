/*
 * SyntaxParser.java
 * MAIN PROGRAM OF THE SYNTAX ANALYZER
 */

package parser;

import java.util.ArrayList;
import java.util.Stack;

import lex.LexicalAnalyzer;
import lex.Token;
import lex.Type;

public class SyntaxParser {
	
	private LexicalAnalyzer lex;//import the lex analyzer
	private ArrayList<Token> tokenList;//import all tokens from lex analyzer
	private int length;//length of token list
	private int index;//Pointer point to the current token
	
	private AnalyzeTable table;//Construct a syntax analysis table, using AnalyzeTable in same folder
	private Stack<Integer> stateStack;//store the corresponding status
	
	private Error error = null;//Errors
	
	public static void main(String[] args){
		SyntaxParser parser = new SyntaxParser("source.c");
		parser.analyze();
	}
	
	//Constructor of Syntax Parser
	public SyntaxParser(String filename){
		this.lex = new LexicalAnalyzer(filename);
		this.tokenList = lex.getTokenList();
		this.tokenList.add(new Token("-1","$"));//after the sentence obtained from the previous syntax analysis, add (-1,"$") as a terminator
		this.length = this.tokenList.size();
		this.index = 0;//init pointer
		this.table = new AnalyzeTable();
		this.stateStack = new Stack<Integer>();
		this.stateStack.push(0);
		this.table.dfa.printAllStates();
		this.table.print();
		for(int i = 0;i < tokenList.size();i++){
			System.out.println(tokenList.get(i).toString());
		}//add (-1,"$") as a terminator before output
		
	}
	
	//return the current index token
	public Token readToken(){
		if(index < length){
			return tokenList.get(index++);
		} else {
			return null;
		}
	}
	
	//Analyze process
	//For more explanation/logic for this part and Analyze Table, please see the report doc
	public void analyze(){
		while(true){
			Token token = readToken();
			String valueType = token.type;
			String value = getValue(valueType);
			int state = stateStack.lastElement();
			String action = table.ACTION(state, value);
			System.out.println(action);
			if(action.startsWith("s")){//S
				int newState = Integer.parseInt(action.substring(1));
				stateStack.push(newState);
				System.out.print("Insert"+"\t");
				System.out.print("StatusTable:"+stateStack.toString()+"\t");
				System.out.print("Input:");
				printInput();
				System.out.println();
				System.out.println();
			} else if(action.startsWith("r")){//Regulate
				Derivation derivation = CFG.F.get(Integer.parseInt(action.substring(1)));
				int r = derivation.list.size();
				index--;
				for(int i = 0;i < r;i++){
					stateStack.pop();
				}
				int s = table.GOTO(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				System.out.print("Regulate"+"\t");
				System.out.print("StatusTable:"+stateStack.toString()+"\t");
				System.out.print("Input:");
				printInput();
				System.out.println();
			} else if(action.equals(AnalyzeTable.acc)){//syntax correct
				System.out.print("Syntax Analysis Complete"+"\t");
				System.out.print("StatusTable:"+stateStack.toString()+"\t");
				System.out.print("Input:");
				printInput();
				System.out.println();
				return;
			} else {
				error();//Syntax error, report error instead
				return;
			}
			
			
		}
	}
	
	//Return the actual value of the certain type
	private String getValue(String valueType){
		switch(valueType){
		case Type.ADD:
			return "+";
		case Type.SUB:
			return "-";
		case Type.MUL:
			return "*";
		case Type.DIV:
			return "/";
		case Type.ID:
			return "<id>";
		case Type.NUM:
			return "<num>";
		case Type.IF:
			return "if";
		case Type.ELSE:
			return "else";
		case Type.SEMICOLON:
			return ";";
		case Type.PARENTHESIS_L:
			return "(";
		case Type.PARENTHESIS_R:
			return ")";
		case Type.GE:
			return ">=";
		case Type.ASSIGN:
			return "=";
		case Type.INT:
			return "<int>";	
		case Type.WHILE:
			return "<while>";	
		case Type.VAR:
			return "<var>";
		case Type.CHAR:
			return "<char>";
		case Type.DOUBLE:
			return "<double>";
		case Type.FLOAT:
			return "<float>";
		case Type.LONG:
			return "<long>";
		case Type.SHORT:
			return "<short>";
		case Type.STRING:
			return "<string>";
		/*	
		case Type.POUND:
			return "#";	
		case Type.INCLUDE:
			return "<include>";
	
		case Type.RETURN:
			return "<return>";
		*/	
		case "-1":
			return "$";
		default:
			return null;
		}
	}
	
	//Syntax Error
	public void error(){
		System.out.println("No."+(index-1)+" lex position found a Syntax error:"+tokenList.get(index-1).toString());
	}
	
	private void printInput(){
		String output = "";
		for(int i = index;i < tokenList.size();i++){
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.print(output);
	}
	
}
