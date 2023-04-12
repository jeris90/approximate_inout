package appx.af;

import java.util.LinkedList;

public class ArgumentationFramework {
	
	private LinkedList<Integer> [] af_attacker;
	
	private LinkedList<Integer> [] af_attackee;
	
	public ArgumentationFramework(int nb_arg) {
		af_attacker = new LinkedList[nb_arg];
		af_attackee = new LinkedList[nb_arg];
		
		for (int i=0 ; i < nb_arg ; i++) {
			af_attacker[i] = new LinkedList<Integer>(); 
			af_attackee[i] = new LinkedList<Integer>(); 
		}
	}
	
	public void add_attack(int attacker, int target) {
		af_attacker[target].add(attacker);
		af_attackee[attacker].add(target);
	}
	
	public LinkedList<Integer>[] get_af_attacker(){
		return af_attacker;
	}
	
	public LinkedList<Integer>[] get_af_attackee(){
		return af_attackee;
	}
	
	public int nb_arguments() {
		return af_attacker.length;
	}
	
	/***
	 * Returns the in-degree of a given argument (i.e. the number of arguments that directly attack the argument)
	 * @param argument
	 * @return the in-degree value
	 */
	public int inDegree(int argument) {
		return af_attacker[argument].size();
	}
	
	/***
	 * Returns the out-degree of a given argument (i.e. the number of arguments that are directly attacked by the argument)
	 * @param argument
	 * @return the out-degree value
	 */
	public int outDegree(int argument) {
		return af_attackee[argument].size();
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int i=0 ; i < af_attacker.length ; i++) {
			res.append(i + " <- " + af_attacker[i].toString() + "\n");
		}
		return res.toString();
	}

}
