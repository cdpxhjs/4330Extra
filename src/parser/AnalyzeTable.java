/*
 * AnalyzeTable.java
 * Create an analysis table that contains ACTION table and GOTO table
 * i.e.
 * LR(1) analysis table = ACTION table + GOTO table(Status transfer table)
 * Store all the path infomation in the container when creating DFA
 * then access ACTION and GOTO
 */

package parser;

import java.util.ArrayList;
import java.util.Iterator;

public class AnalyzeTable {
	
	public static String error = "ERR";//Fail if Table=ERR
	public static String acc = "ACC";//Success if Table=ACC
	
	public DFA dfa;
	
	private String[] actionCol;//action column
	private String[] gotoCol;//goto column
	public int actionLength;
	public int gotoLength;
	public int stateNum;
	
	private int[][] gotoTable;
	private String[][] actionTable;
	
	//Constructor of AnalyzeTable
	public AnalyzeTable(){
		createTableHeader();//Create a table
		this.actionLength = actionCol.length;
		this.gotoLength = gotoCol.length;
		createDFA();//create a DFA for generating syntax analysis tables
		this.stateNum = dfa.size();
		/*
		 * ACTION Table is a Table with marks with S(insert) or R(regulate), which points to the state of the next final symbol
		 * GOTO Table is a table with the non-terminals as the column and the state as the row, pointing to the state of the next non-terminals
		 */
		this.gotoTable = new int[stateNum][gotoLength+actionLength-1];
		this.actionTable = new String[stateNum+1][actionLength+1];//
		createAnalyzeTable();//Fill in to the analysis table
	}
	
	/*
	 * CFG
	 * Use CFG to create a header for the LR(1) syntax analysis table
	 */
	private void createTableHeader(){
		//Create a column of the table
		this.actionCol = new String[CFG.VT.size()+1];
		this.gotoCol = new String[CFG.VN.size()+CFG.VT.size()];
		Iterator<String> iter1 = CFG.VT.iterator();
		Iterator<String> iter2 = CFG.VN.iterator();
		int i = 0;
		//int j = 0;
		while(iter1.hasNext()){
			String vt = iter1.next();
			if(!vt.equals(CFG.emp)){
				actionCol[i] = vt;
				gotoCol[i] = vt;
				i++;
			}
		}
		actionCol[i] = "$";
		while(iter2.hasNext()){
			String vn = iter2.next();
			gotoCol[i] = vn;
			i++;
		}
	}
	
	//private ArrayList<DFAState> stateList = new ArrayList<DFAState>();
	//container used for recursive
	private ArrayList<Integer> gotoStart = new ArrayList<Integer>();
	private ArrayList<Integer> gotoEnd = new ArrayList<Integer>();
	private ArrayList<String> gotoPath = new ArrayList<String>();
	/**
	 * Create a syntax DFA using recursive
	 */
	private void createDFA(){
		this.dfa = new DFA();
		DFAState state0 = new DFAState(0);
		state0.addNewDerivation(new LRDerivation(getDerivation("S'").get(0),"$",0));//First add S'->¡¤S,$
		for(int i = 0;i < state0.set.size();i++){
			LRDerivation lrd = state0.set.get(i);
			if(lrd.index < lrd.d.list.size()){
				String A = lrd.d.list.get(lrd.index);//Get syntax symbols after '¡¤'
				String b = null;//followed by A
				if(lrd.index==lrd.d.list.size()-1){
					b = lrd.lr;
				} else {
					b = lrd.d.list.get(lrd.index+1);
				}
				if(CFG.VN.contains(A)){
					ArrayList<String> firstB = first(b);
					ArrayList<Derivation> dA = getDerivation(A);
					for(int j=0,length1=dA.size();j<length1;j++){
						for(int k=0,length2=firstB.size();k<length2;k++){
							LRDerivation lrd1 = new LRDerivation(dA.get(j),firstB.get(k),0);
							state0.addNewDerivation(lrd1);
						}
					}
				}
			}
		}
		dfa.states.add(state0);
		//state0 built successfully, build other states recursively
		ArrayList<String> gotoPath = state0.getGotoPath();
		for(String path:gotoPath){
			ArrayList<LRDerivation> list = state0.getLRDs(path);//case: pass directly to the next state via path
			addState(0,path,list);//start recursive
		}
	}
	
	/**
	 * get the next state by the LR generative formula list passed by the previous state
	 * if the state already exists, jump out the recursion
	 * if the state does not exist, join the state and continue the recursion
	 * @param list
	 * @param lastState the number of last state
	 */
	private void addState(int lastState,String path,ArrayList<LRDerivation> list){
		DFAState temp = new DFAState(0);
		for(int i = 0;i < list.size();i++){
			list.get(i).index++;
			temp.addNewDerivation(list.get(i));
		}
		for(int i = 0;i < temp.set.size();i++){
			if(temp.set.get(i).d.list.size() != temp.set.get(i).index){
				String A = temp.set.get(i).d.list.get(temp.set.get(i).index);
				String B = null;//followed by A
				if(temp.set.get(i).index+1 == temp.set.get(i).d.list.size()){
					B = temp.set.get(i).lr;
				} else {
					B = temp.set.get(i).d.list.get(temp.set.get(i).index+1);
				}
				ArrayList<Derivation> dA = getDerivation(A);
				ArrayList<String> firstB = first(B);
				for(int j = 0;j < dA.size();j++){
					for(int k = 0;k < firstB.size();k++){
						LRDerivation lrd = new LRDerivation(dA.get(j),firstB.get(k),0);
						if(!temp.contains(lrd)){
							temp.addNewDerivation(lrd);
						}
					}
				}
			}
		}
		for(int i = 0;i < dfa.states.size();i++){
			if(dfa.states.get(i).equalTo(temp)){
				gotoStart.add(lastState);
				gotoEnd.add(i);
				gotoPath.add(path);
				return;
			}
		}
		temp.id = dfa.states.size();
		dfa.states.add(temp);
		gotoStart.add(lastState);
		gotoEnd.add(temp.id);
		gotoPath.add(path);
		ArrayList<String> gotoPath = temp.getGotoPath();
		for(String p:gotoPath){
			ArrayList<LRDerivation> l = temp.getLRDs(p);//jump to the next state
			addState(temp.id,p,l);
		}
	}
	
	/*
	 * obtain a generative formula according to a syntax symbol
	 * @param v
	 * @return
	 */
	public ArrayList<Derivation> getDerivation(String v){
		ArrayList<Derivation> result = new ArrayList<Derivation>();
		Iterator<Derivation> iter = CFG.F.iterator();
		while(iter.hasNext()){
			Derivation d = iter.next();
			if(d.left.equals(v)){
				result.add(d);
			}
		}
		return result;
	}
	
	/**
	 * get a first of a grammar symbol
	 * @param v
	 * @return
	 */
	private ArrayList<String> first(String v){
		ArrayList<String> result = new ArrayList<String>();
		if(v.equals("$")){
			result.add("$");
		} else {
			Iterator<String> iter = CFG.firstMap.get(v).iterator();
			while(iter.hasNext()){
				result.add(iter.next());
			}
		}
		return result;
	}
	
	/**
	 * fill in to the final analyze table
	 */
	private void createAnalyzeTable(){
		for(int i = 0;i < gotoTable.length;i++){
			for(int j = 0;j < gotoTable[0].length;j++){
				gotoTable[i][j] = -1;
			}
		}
		for(int i = 0;i < actionTable.length;i++){
			for(int j = 0;j < actionTable[0].length;j++){
				actionTable[i][j] = AnalyzeTable.error;
			}
		}
		//Finalize GOTO
		int gotoCount = this.gotoStart.size();
		for(int i = 0;i < gotoCount;i++){
			int start = gotoStart.get(i);
			int end = gotoEnd.get(i);
			String path = gotoPath.get(i);
			int pathIndex = gotoIndex(path);
			this.gotoTable[start][pathIndex] = end;
		}
		//Finalize ACTION
		int stateCount = dfa.states.size();
		for(int i = 0;i < stateCount;i++){
			DFAState state = dfa.get(i);//get single status of DFA
			for(LRDerivation lrd:state.set){//Analyze every one
				if(lrd.index == lrd.d.list.size()){
					if(!lrd.d.left.equals("S'")){
						int derivationIndex = derivationIndex(lrd.d);
						String value = "r"+derivationIndex;
						actionTable[i][actionIndex(lrd.lr)] = value;//set "regulate"
					} else {
						actionTable[i][actionIndex("$")] = AnalyzeTable.acc;//set "accept"
					}
				} else {
					String next = lrd.d.list.get(lrd.index);//Get syntax symbols after '¡¤'
					if(CFG.VT.contains(next)){//must be a terminator
						if(gotoTable[i][gotoIndex(next)] != -1){
							actionTable[i][actionIndex(next)] = "s"+gotoTable[i][gotoIndex(next)];
						}
					}
				}
			}
		}
	}
	
	//return the column index of GOTO
	// @param symbol s
	private int gotoIndex(String s){
		for(int i = 0;i < gotoLength;i++){
			if(gotoCol[i].equals(s)){
				return i;
			}
		}
		return -1;//not found
	}
	
	//return the column 
	// @param symbol s
	private int actionIndex(String s){
		for(int i = 0;i < actionLength;i++){
			if(actionCol[i].equals(s)){
				return i;
			}
		}
		return -1;//not found
	}
	
	//return is which derivation formula
	private int derivationIndex(Derivation d){
		int size = CFG.F.size();
		for(int i = 0;i < size;i++){
			if(CFG.F.get(i).equals(d)){
				return i;
			}
		}
		return -1;//not found
	}
	
	//return the status of [stateIndex][vt.index] from ACTION table
	public String ACTION(int stateIndex,String vt){
		int index = actionIndex(vt);
		return actionTable[stateIndex][index];
		//return actionTable[stateIndex][index];
		
	}
	
	//return the status of [stateIndex][vn.index] from GOTO table
	public int GOTO(int stateIndex,String vn){
		int index = gotoIndex(vn);
		return gotoTable[stateIndex][index];
	}
	
	//Print syntax analysis table
	public void print(){
		String colLine = "";
		for(int i = 0;i < actionCol.length;i++){
			colLine += "\t";
			colLine += actionCol[i];
		}
		for(int j = 0;j < gotoCol.length;j++){
			colLine += "\t";
			colLine += gotoCol[j];
		}
		System.out.println(colLine);
		int index = 0;
		for(int i = 0;i < dfa.states.size();i++){
			String line = String.valueOf(i);
			while(index < actionCol.length){
				line += "\t";
				line += actionTable[i][index];
				index++;
			}
			index = 0;
			while(index < gotoCol.length){
				line += "\t";
				if(gotoTable[i][index] == -1){
					line += "X";
				} else {
					line += gotoTable[i][index];
				}
				index++;
			}
			index = 0;
			line += "\t";
			System.out.println(line);
		}
	}
	
	public int getStateNum(){
		return dfa.states.size();
	}

}
