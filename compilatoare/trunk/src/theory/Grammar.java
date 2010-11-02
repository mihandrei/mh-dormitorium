package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * immutable; invariant: head contains at least one Nonterminal
 * nonterminals and terminals are disjunct
 */
public final class Grammar {

	public static final class Production {
		public final List<String> head;
		public final List<String> tail;

		public Production(Collection<String> h, Collection<String> t) {
			head = Collections.unmodifiableList(new ArrayList<String>(h));
			tail = Collections.unmodifiableList(new ArrayList<String>(t));
		}
	}

	public final SortedSet<String> nonterminals;
	public final SortedSet<String> terminals;
	public final String S;
	public final String epsilon;
	public final List<Production> productions;

	public Grammar(Collection<String> nt, Collection<String> t,
			Collection<Production> prods, String s, String epsil)
			throws Exception {
		
		S = s;
		epsilon = epsil;
		nt.add(S);
		
		nonterminals = Collections
				.unmodifiableSortedSet(new TreeSet<String>(nt));
		terminals = Collections.unmodifiableSortedSet(new TreeSet<String>(t));
		productions = Collections.unmodifiableList(new ArrayList<Production>(
				prods));

		Set<String> intersect = new TreeSet<String>(terminals);
		intersect.retainAll(nonterminals);
		
		if(!intersect.isEmpty()) {
			throw new Exception("terminals and nonterminals must be disjunct");
		}
		
		for (Production p : productions) {
			boolean valid = false;

			for (String headsymb : p.head) {
				if (headsymb.equals(s) || nonterminals.contains(headsymb)) {
					valid = true;
					break;
				}
			}

			if (!valid) {
				throw new Exception("expected at least one nonterminal in head");
			}
		}
	}


	public boolean isContextFree() {
		for (Production p : productions) {
			if (p.head.size() != 1) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isLinear() {
		if (!isContextFree()) {
			return false;
		}
		
		for (Production p : productions) {
			boolean nonterminal_prev_found = false;
			for(String tailsym:p.tail){
				if(nonterminals.contains(tailsym)){
					if(nonterminal_prev_found){
						return false;
					}else{
						nonterminal_prev_found = true;
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean isRegular() {
		if (!isContextFree()) {
			return false;
		}
		boolean regular = true;
		for (Production p : productions) {
			if ((p.tail.size() > 2)
					|| (p.tail.size() == 1
							&& !terminals.contains(p.tail.get(0)) && !p.tail
							.get(0).equals(epsilon))
					|| (p.tail.size() == 2
							&& !terminals.contains(p.tail.get(1)) && !nonterminals
							.contains(p.tail.get(1)))) {
				regular = false;
				break;
			}
		}
		return regular;
	}	
	
	public NDFA toNDFA() throws Exception{
		if(!isRegular()){
			throw new Exception("irregular grammar");
		}
		
		NDFA ndfa = new NDFA();
		for (Production p : productions){
			String s1 = p.head.get(0);
			String sym = p.tail.get(0);
			String s2;
			if(p.tail.size() == 1){
				s2 = "Z";
			}else{
				s2 = p.tail.get(1);
			}
			ndfa.add(s1, sym, s2);
		}
		ndfa.build(S, "Z");		
		return ndfa;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for (Production prod:productions){
			for(String hs:prod.head){
				builder.append(hs);
				builder.append(" ");
			}
			builder.append("->");
			for(String ts:prod.tail){
				builder.append(ts);
				builder.append(" ");
			}
			builder.append(";\n");
		}
		return builder.toString();		
	}
	
}
