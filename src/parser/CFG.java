/*
 * CFG.java
 * The propose of this file is to read rules from "Input/cfg.txt"
 * and add corresponding generative formula,
 * which express the method for combining terminators and non-terminals into a string
 * 
 */
package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class CFG {//Context Free Grammar
	
	public static String emp = "¦Å";
	
	public static String end = "$";
	
	public static TreeSet<String> keywords = new TreeSet<String>();//reserved keywords set
	
	public static TreeSet<String> VN = new TreeSet<String>();//Non-terminals used to represent syntactic components/variables
	public static TreeSet<String> VT = new TreeSet<String>();//Terminators are basic symbols/tokens of the language
	public static ArrayList<Derivation> F = new ArrayList<Derivation>();//Set of generative formula
	
	public static HashMap<String,TreeSet<String> > firstMap = new HashMap<String,TreeSet<String> >();//first
	public static HashMap<String,TreeSet<String> > followMap = new HashMap<String,TreeSet<String> >();//follow
	
	static{
		/*
		 * read the grammar from the file
		 * add corresponding generative formula
		 * formula describes the method of combining terminators and non-terminals into a string
		 */
		try {
			read("cfg.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//reserved keywords for C
		String[] keyword ={
				"auto","double","int","struct","break","else","long","switch",
				"case","enum","register","typedef","char","return","union","const",
				"extern","float","short","unsigned","continue","for","signed","void",
				"default","goto","sizeof","volatile","do","if","static","while","string"
		};
		for(String k:keyword){
			keywords.add(k);
		}
		
		//S->if B S;|if B S; else S;|<id>=E|S;S
		//B->B >= B|<num>|<id>
		//E->E+E|E*E|<num>|<id>
		//add non-terminals
		VN.add("S'");VN.add("S");VN.add("B");VN.add("E");VN.add("N");
		VT.add("if");
		VT.add("else");
		VT.add(";");
		VT.add("=");
		VT.add(">=");
		VT.add("<num>");
		VT.add("<id>");
		VT.add("<int>");
		VT.add("<char>");
		VT.add("<double>");
		VT.add("<float>");
		VT.add("<long>");
		VT.add("<short>");
		VT.add("<string>");
		
		VT.add("<var>");
		
		VT.add("*");
		VT.add("+");
		VT.add("(");
		VT.add(")");
		
		VT.add("while");
		/*VT.add("#");
		VT.add("include");
		VT.add("main");
		VT.add("printf");
		VT.add("return");
		*/
		
		addFirst();
		//addFollow();
	}
	
	/**
	 * read grammar from file and store it into a static container
	 * The number is the index of the container
	 * @param filename
	 * @throws FileNotFoundException
	 */
	private static void read(String filename) throws FileNotFoundException{
		File file = new File("Input/"+filename);
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			String line = scanner.nextLine();
			String[] div = line.split("->");//split as "->"
			String[] right = div[1].split("\\|");//parse multiple expression into individual
			for(String r:right){
				Derivation derivation = new Derivation(div[0]+"->"+r);
				F.add(derivation);//store into a static container
			}
		}
		scanner.close();
	}
	
	/**
	 * calculate the first set of all symbols
	 * use recursive when several steps needed
	 */
	private static void addFirst(){
		//set the first of all terminators to itself
		Iterator<String> iterVT = VT.iterator();
		while(iterVT.hasNext()){
			String vt = iterVT.next();
			firstMap.put(vt,new TreeSet<String>());
			firstMap.get(vt).add(vt);
		}
		//calculate the first set of all non-terminals
		Iterator<String> iterVN = VN.iterator();
		while(iterVN.hasNext()){
			String vn = iterVN.next();
			firstMap.put(vn, new TreeSet<String>());//no firstMap crossed
			int dSize = F.size();
			for(int i = 0;i < dSize;i++){
				Derivation d = F.get(i);
				if(d.left.equals(vn)){
					if(VT.contains(d.list.get(0))){//if the first symbol at the right end of the generated formula is a terminator, then add to the map
						firstMap.get(vn).add(d.list.get(0));
					} else {//if the first symbol at the right end of the generated formula is a non-terminal, then use a recursive lookup
						firstMap.get(vn).addAll(findFirst(d.list.get(0)));
					}
				}
			}
		}
	}
	
	/**
	 * recursive function used for finding the first symbol
	 * @param Non-terminal string vn
	 * @return
	 */
	private static TreeSet<String> findFirst(String vn){
		TreeSet<String> set = new TreeSet<String>();
		for(Derivation d:F){
			if(d.left.equals(vn)){
				if(VT.contains(d.list.get(0))){//if terminator, the add it
					set.add(d.list.get(0));
				} else {
					if(!vn.equals(d.list.get(0))){//Remove recursion like:"E -> E*E", avoid stack overflow
						set.addAll(findFirst(d.list.get(0)));//recursive again
					}
				}
			}
		}
		return set;
	}
	

//	private static void addFollow(){
//		Iterator<String> iterVN = VN.iterator();
//		HashMap<String,ArrayList<String> > hashmap = new HashMap<String,ArrayList<String> >();
//		while(iterVN.hasNext()){
//			String vn = iterVN.next();
//			followMap.put(vn, new TreeSet<String>());
//			hashmap.put(vn, new ArrayList<String>());
//			for(Derivation d:F){
//				if(d.list.contains(vn)){
//					ArrayList<Integer> index = new ArrayList<Integer>();
//					for(int i = 0;i < d.list.size();i++){
//						if(d.list.get(i).equals(vn)){
//							index.add(i);
//						}
//					}
//					for(int i:index){
//						if(i == (d.list.size()-1)){
//							followMap.get(vn).add(CFG.end);
//							hashmap.get(vn).add(d.left);
//						} else {
//							TreeSet<String> add = new TreeSet<String>();
//							Iterator<String> iter = firstMap.get(d.list.get(i+1)).iterator();
//							while(iter.hasNext()){
//								String value = iter.next();
//								if(!value.equals(CFG.emp)){
//									add.add(value);
//								}
//							}
//							followMap.get(vn).addAll(add);
//						}
//					}
//				}
//			}
//			Iterator<String> iter = hashmap.keySet().iterator();
//			while(iter.hasNext()){
//				String key = iter.next();
//				ArrayList<String> value = hashmap.get(key);
//				if(value.size() != 0){
//					for(String v:value){
//						followMap.get(key).addAll(followMap.get(v));
//						followMap.get(v).addAll(followMap.get(key));
//					}
//				}
//			}
//		}
//	}

}
