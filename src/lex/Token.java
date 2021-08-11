package lex;

public class Token {
	public String type;
	public String value;
	//Constructor of Token
	public Token(String type,String value){
		this.type = type;
		this.value = value;
	}
	public String toString(){
		return "<"+this.type+","+this.value+">";//Get the Token list and return
	}
}
