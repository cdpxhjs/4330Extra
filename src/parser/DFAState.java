/*
 * DFAState.java
 * set the DFA number and state/status
 */

package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DFAState {
	
	//id is the number of the state
	//set is all LR(1) analysis formula in the state
	public int id ;
	public ArrayList<LRDerivation> set = new ArrayList<LRDerivation>();
	
	//Constructor
	public DFAState(int id){
		this.id = id;
	}
	
	
	public boolean addNewDerivation(LRDerivation d){
		if(contains(d)){//already has
			return false;
		} else {
			set.add(d);//add to the set
			return true;
		}
	}
	
	public void print(){
		Iterator<LRDerivation> iter = set.iterator();
		while(iter.hasNext()){
			iter.next().print();
		}
	}
	
	public boolean contains(LRDerivation lrd){
		for(LRDerivation l:set){//local set
			if(l.equalTo(lrd)){
				return true;
			}
		}
		return false;
	}
	
//	private boolean contains(ArrayList<LRDerivation> set1,ArrayList<LRDerivation> set2){
//	for(int i = 0;i < set2.size();i++){
//		if(!contain(set1,set2.get(i))){
//			return false;
//		}
//	}
//	return true;
//}
	
	private boolean contains(ArrayList<LRDerivation> set,LRDerivation lrd){
		for(LRDerivation l:set){//input set
			if(l.equalTo(lrd)){
				return true;
			}
		}
		return false;
	}
	
	public boolean equalTo(DFAState state){
		if(this.toString().hashCode()==state.toString().hashCode()){
//		if(contains(set,state.set)&&contains(state.set,set)){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		String result = "";
		for(int i = 0;i < set.size();i++){
			result += set.get(i);
			if(i < set.size()-1){
				result += "|";
			}
		}
		return result;
	}
	
	
	public ArrayList<String> getGotoPath(){
		ArrayList<String> result = new ArrayList<String>();
		for(LRDerivation lrd:set){
			if(lrd.d.list.size()==lrd.index){
				continue;
			}
			String s = lrd.d.list.get(lrd.index);
			if(!result.contains(s)){
				result.add(s);
			}
		}
		return result;
	}
	
	public ArrayList<LRDerivation> getLRDs(String s){
		ArrayList<LRDerivation> result = new ArrayList<LRDerivation>();
		for(LRDerivation lrd:set){
			if(lrd.d.list.size() != lrd.index){
				String s1 = lrd.d.list.get(lrd.index);
				if(s1.equals(s)){
					result.add((LRDerivation)lrd.clone());
				}
			}
		}
		return result;
	}

}
