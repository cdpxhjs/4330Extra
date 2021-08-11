/*
 * File Scan
 * The propose of this program is to read the input file
 * At the same time
 * ignore all white spaces and comments
 */

package lex;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Scan {
	
//	public static void main(String[] args){
//		Scan scan = new Scan("source1.c");
//		System.out.println(scan.input);
//	}
	
	private static String inputPath = "Input/";//define the input file path(src/Input/)
	
	public String input;//ignore all whitespaces and comments
	public int pointer;//pointer point to the current read location 
	
	public Scan(String filename){
		File sourceFile = new File(Scan.inputPath+filename);//file path:"input/source.c"
		ArrayList<Character> trans = new ArrayList<Character>();
		try {
			//DataInputStream in = new DataInputStream(new FileInputStream(sourceFile));
			FileInputStream in = new FileInputStream(sourceFile);
			char ch1 = ' ';
			char ch2 = ' ';//Used to verify if it ends within quotation marks or at the end of a comment
			while(in.available()>0){//still something in the file
				if(ch2 != ' '){//if ch2 store a char that is not white space
					ch1 = ch2;
				} else {//or just let ch1 read the next char
					ch1 = (char) in.read();
				}
				
				if(ch1 == '\''){//Avoid deleting whitespace characters contained in '' when deleting whitespace
					trans.add(ch1);
					trans.add((char)in.read());
					trans.add((char)in.read());
				} else if (ch1 == '\"'){//Avoid deleting whitespace from strings
					trans.add(ch1);
					while(in.available()>0){
						ch1 = (char)in.read();
						trans.add(ch1);
						if(ch1 == '\"'){//if end of string
							break;
						}
					}
				} else if (ch1 == '/'){//Delete, first char of comment
					ch2 = (char)in.read();
					if(ch2 == '/'){//sure single-line comment
						while(in.available() > 0){
							ch2 = (char)in.read();
							if(ch2 == '\n'){//next line detected
								break;
							}
						}
						ch2 = ' ';//reset ch2
					} else if (ch2 == '*') {//partial comment detected
						while(in.available() > 0){
							ch1 = (char)in.read();
							if(ch1 == '*'){
								ch2 = (char)in.read();
								if(ch2 == '/'){//end of partial comment part
									break;
								}
							}
						}
					} else {
						if(ch2 == ' '){//"/ " then delete whitespaces
							while(ch2 == ' '){//until next not whitespace detected
								ch2 = (char)in.read();
							}
						}
						trans.add(ch1);
						trans.add(ch2);
						ch2 = ' ';
					}
				} else if(ch1 == ' '){
					if(trans.get(trans.size()-1) == ' '){
						continue;
					} else {
						//trans.add(' ');
					}
				} else {
					if((int)ch1 == 13 ||(int)ch1 == 10 ||(int)ch1 == 32){//Blank lines
						
					} else {
						trans.add(ch1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		char[] chStr = new char[trans.size()];
		for(int i = 0;i < trans.size();i++){
			chStr[i] = trans.get(i);
		}//give charset to string
		String result = new String(chStr);
		this.input = result;
		this.pointer = 0;
	}
	
	public char getNextChar(){
		if(pointer==input.length()){
			return (char)0;
		} else {
			return input.charAt(pointer++);
		}
	}
	
	//Fall back n position for pointer
	public void retract(int n){
		for(int i = 0;i < n;i++){
			pointer--;
		}
	}
	
	//Get the index of the pointer
	public int getIndex(){
		return pointer;
	}
	
	//Get the length of the input file
	public int getLength(){
		return this.input.length();
	}
	
	public String getSubStr(int index,int length){
		if((index+length-1)>=this.input.length()){
			return null;
		} else {
			String result = this.input.substring(index,index+length);
			return result;
		}
	}
	
	public String getTestString(int index){
		int temp = index;
		int len = 1;
		while(isLetterOrDigit(input.charAt(temp))&&(temp<=(input.length()-1))){
			temp++;
			len++;
		}
		String result = input.substring(index-1,index-1+len);
		return result;
	}
	
	private boolean isLetterOrDigit(char c){
		if(c=='_'||(c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')){
			return true;
		} else {
			return false;
		}
	}
	
	//get the string left
	public String getLeftStr(int index){
		if(index == input.length()-1){//end of string
			return null;
		} else {
			return input.substring(index);
		}
	}
	
	//move pointer
	public void move(int n){
		for(int i = 0;i < n;i++){
			pointer++;
		}
	}
	
	public String getStringInQuotation(int index){
		int temp = index;
		while(input.charAt(temp-1)!='\"'){
			temp--;
		}
		StringBuilder sb = new StringBuilder();
		while(input.charAt(temp) != '\"'){
			sb.append(input.charAt(temp));
			temp++;
		}
		return sb.toString();
	}
	
}
