package theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import theory.Grammar.Production;
import tools.GraphViz;

/**
 * varianta neidexata O(n) list<transition> cu transition =
 * (state,symbol,set<String>)
 * 
 * vaianta sparse are Map<TableKey, Set<String>> transitions; trans.get(new
 * Tablekey('s1','a')) trans din s1 cu symb a
 * 
 * vaianta tabela v1 ar avea Map<state,Map<symb,Set<String>>> transitions;
 * 
 * deprecated : tabela list<list<stateset>> cu indecsi int in 2 hashmapuri
 * string->int trans.get(1).get(3) ca la v1 numai cu chei implicite indecsi;
 * nu-i mare diferenta! search/insert ii o(1) = 2cautari in hash si 2 indexari
 * 
 * compact ndfa: starile devin intregi , alfabetu e din caractere tabela e
 * short[][][] - nu e redimensionabil
 */

public class NDFA {

	private Set<String> Q = new HashSet<String>();
	private Set<String> F = new HashSet<String>();;
	private Set<String> Sigma = new HashSet<String>();;
	private String q0 = "q0";
	private String epsilon = "epsilon";

	private Map<String, Map<String, Set<String>>> transitions = new HashMap<String, Map<String, Set<String>>>();

	// starile curente ale automatului epsilon closed
	private Set<String> currentStateSet = new HashSet<String>();

	/**
	 * trebuie sa fie apelat inainte de a lucra cu automatul 
	 * trebuie apelat dupa toate add-urile 
	 */
	public void build(String startstate, String... finalstates) {
		reset();
		addState(startstate);
		q0 = startstate;

		currentStateSet.add(q0);
		// daca automatu incepe cu cateva epsilon tranzitii
		fillEpsilonClojure(currentStateSet, q0);

		for (String fstate : finalstates) {
			addState(fstate);
			F.add(fstate);
		}
	}

	public void addState(String s1) {
		Q.add(s1);
		if (!transitions.containsKey(s1)) {
			transitions.put(s1, new HashMap<String, Set<String>>());
		}
	}

	public void add(String s1, String symbol, String... transitionStates) {
		if (!symbol.equals(epsilon)) {
			Sigma.add(symbol);
		}

		addState(s1);

		if (!transitions.get(s1).containsKey(symbol)) {
			transitions.get(s1).put(symbol, new HashSet<String>());
		}

		for (String trans : transitionStates) {
			addState(trans);
			transitions.get(s1).get(symbol).add(trans);
		}
	}

	public boolean isAccepting() {
		HashSet<String> intersect = new HashSet<String>(currentStateSet);
		intersect.retainAll(F);
		return !intersect.isEmpty();
	}

	public void reset() {
		currentStateSet.clear();
	}

	public void put(String symbol) {
		currentStateSet = step(currentStateSet, symbol);
	}

	/**
	 * intoarce starea in care s-ar afla automatu daca ar fi in starea stateSet
	 * si ar citi symbol stateset tre sa fie deja epsilon-closed iintoarce o
	 * mult epsilon-closed
	 */
	private Set<String> step(Set<String> stateSet, String symbol) {
		Set<String> newstate = new HashSet<String>();
		// advance all current states
		for (String acurrentstate : stateSet) {
			fillClosureStateSet(newstate, acurrentstate, symbol);
		}
		return newstate;
	}

	/**
	 * pune in newstate toate starile in care se ajunge din acurrentstate prin
	 * symbol inclusiv tranzitiile epsilon
	 */
	private void fillClosureStateSet(Set<String> newstate,
			String acurrentstate, String symbol) {
		Set<String> directtrans = transitions.get(acurrentstate).get(symbol);
		if (directtrans != null) { // exista transitii pt simbolul dat
			newstate.addAll(directtrans);

			for (String s2 : directtrans) {
				fillEpsilonClojure(newstate, s2);
			}
		}
	}

	/**
	 * epsilon clojure, pune in setu de stari curente toate starile legate de
	 * starile curente de una sau mai multe epsilon tranzitii
	 */
	private void fillEpsilonClojure(Set<String> newstate, String s2) {
		Set<String> epsilontransStates = transitions.get(s2).get(epsilon);
		if (epsilontransStates != null) {
			for (String eps : epsilontransStates) {
				if (!newstate.contains(eps)) { // in cazu in care sunt bucle de
												// epsilonae
					newstate.add(eps);
					fillEpsilonClojure(newstate, eps);
				}
			}
		}
	}

	public NDFA convert2DFA() {
		NDFA dfa = new NDFA();
		HashSet<String> statesSet = new HashSet<String>();
		statesSet.add(q0);
		fillEpsilonClojure(statesSet, q0);
		
		dfa.q0 = statesSet.toString();
		recursiveBuildDFA(statesSet, dfa);

		if (dfa.Q.contains("sink")) {// add sink 2sink rels
			for (String symbol : Sigma) {
				dfa.add("sink", symbol, "sink");
			}
		}

		return dfa;
	}

	private void _addFinal(Set<String> statesSet, NDFA dfa) {
		HashSet<String> intersection = new HashSet<String>(F);
		intersection.retainAll(statesSet);
		if (!intersection.isEmpty()) {
			dfa.F.add(statesSet.toString());
		}
	}

	private void recursiveBuildDFA(Set<String> statesSet, NDFA dfa) {
		for (String symbol : Sigma) {
			Set<String> newset = step(statesSet, symbol);
			if (newset.size() > 0) {// new set 2 set transition
				boolean newset_processed = dfa.Q.contains(newset.toString());
				_addFinal(statesSet, dfa);
				_addFinal(newset, dfa);
				// add the transition and states!,after this newset_processed is
				// true
				dfa.add(statesSet.toString(), symbol, newset.toString());

				// daca starea-i deja in dfa ne oprim
				if (!newset_processed) {
					recursiveBuildDFA(newset, dfa);
				}
			} else {// new set2nil transition
				dfa.add(statesSet.toString(), symbol, "sink");
			}
		}
	}
	
	public boolean isDeterministic(){
		for(String state:Q){
			Map<String, Set<String>> symtran = transitions.get(state);
			if (symtran.size()!=Sigma.size()){
				return false;
			}
			for (String sym : symtran.keySet()) {
				if(symtran.get(sym).size()!= 1){
					return false;
				}
			}
		}
		return true;
	}
	
	public Grammar toGrammar() throws Exception{
		if(!isDeterministic()){
			throw new Exception("not a deterministic machine");
		}
		
		Collection<Production> prods = new ArrayList<Production>();

		for(String state:Q){
			Map<String, Set<String>> symtran = transitions.get(state);
			for (String sym : symtran.keySet()) {
				String to = symtran.get(sym).iterator().next();
				if(to.equals("sink")){
					continue;
				}
				
				prods.add(new Production(Arrays.asList(new String[]{state}),
							Arrays.asList(new String[]{sym,to})));
				
				if(F.contains(to)){ //terminal rule										
					prods.add(new Production(Arrays.asList(new String[]{state}),
							Arrays.asList(new String[]{sym})));
				}
			}
		 }
		 		
//		Set<String> nonterminals = new HashSet<String>(Q);
//		nonterminals.removeAll(F);
		return new Grammar(Q,Sigma,prods,"S","epsilon");
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append( String.format(" ----> \"%s\"\n", q0));
		builder.append("end states : ");
		for (String fstate : F) {			
			builder.append( String.format(fstate));
		}
		builder.append("\n");
		for (String state : Q) {
			Map<String, Set<String>> symtran = transitions.get(state);
			for (String sym : symtran.keySet()) {
				for (String dest : symtran.get(sym)) {
					builder.append( String.format("\"%s\" --\"%s\"--> \"%s\";\n ",
							state, sym, dest));
				}
			}
		}
		
		return builder.toString();		
	}
	
	public void show() {
		StringBuilder dot = new StringBuilder();
		dot.append( "digraph dfa {\n" + "rankdir=LR;\n" + "size=\"12\";\n"
				+ "ranksep = 1.5\n;" + "nodesep = .25;\n" + "\"\" [shape=none]");

		for (String state : Q) {
			String shape = F.contains(state) ? "doublecircle" : "circle";
			dot.append( String.format("\"%s\" [shape=%s];\n", state, shape));
		}

		dot.append( String.format("\"\" -> \"%s\"\n", q0));

		for (String state : Q) {
			Map<String, Set<String>> symtran = transitions.get(state);
			for (String sym : symtran.keySet()) {
				for (String dest : symtran.get(sym)) {
					dot.append( String.format("\"%s\" -> \"%s\" [label=\"%s\"];\n ",
							state, dest, sym));
				}
			}
		}
		dot.append("}");

		final byte[] imgdata = new GraphViz().getGraph(dot.toString());

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				ImageIcon ico = new ImageIcon(imgdata);
				JLabel lbl = new JLabel(ico);
				JFrame frm = new JFrame();
				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frm.add(lbl);
				frm.pack();
				frm.setVisible(true);
			}
		});
	}
}
