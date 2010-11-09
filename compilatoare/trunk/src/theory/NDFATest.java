package theory;

import theory.ui.Viz;

public class NDFATest {
	static void test1(){
		NDFA ndfa = new NDFA();
		ndfa.add("0", "a", "1","2");
		ndfa.add("1", "a", "1","2");
		ndfa.add("2", "b", "1","3");
		ndfa.add("3", "a", "1","2");
		ndfa.build("0", "1");
		System.out.println(ndfa.isDeterministic());
		Viz.show(ndfa);
		NDFA dfa = ndfa.convert2DFA();
		Viz.show(dfa);			
		System.out.println(dfa.isDeterministic());
	}
	
	static void test2(){
		NDFA ndfa = new NDFA();
		ndfa.add("0", "a", "1","2","3");
		ndfa.add("0", "b", "2","3");
		ndfa.add("1", "a", "1","2");
		ndfa.add("1", "b", "2","3");
		ndfa.add("2", "b", "2","3");
		ndfa.add("2", "b", "3","4");
		ndfa.add("3", "a", "4");
		ndfa.add("3", "b", "2","3","4");		
		ndfa.build("0", "1");
		System.out.println(ndfa.isDeterministic());
		Viz.show(ndfa);
		NDFA dfa = ndfa.convert2DFA();
		System.out.println(dfa.isDeterministic());
		Viz.show(dfa);		
	}
	

	static void test3(){
		NDFA ndfa = new NDFA();
		ndfa.add("0", "epsilon", "1");
		ndfa.add("1", "a", "2");
		ndfa.add("2", "b", "3");
		ndfa.add("2", "a", "1");
		ndfa.add("3", "epsilon", "2");
		ndfa.build("0", "1");
		System.out.println(ndfa.isDeterministic());
		Viz.show(ndfa);
		NDFA dfa = ndfa.convert2DFA();
		System.out.println(dfa.isDeterministic());
		Viz.show(dfa);			
	}
	
	static void test_grammar2NDFA() throws Exception{		
		Grammar grammar = SimpleGrammarParser.parseSimple("S->aS; S->aX; X->bS; X->aY; Y->bS; S->a ");
		NDFA ndfa = grammar.toNDFA();
		Viz.show(ndfa);
		NDFA dfa = ndfa.convert2DFA();
		System.out.println(dfa.isDeterministic());
		Viz.show(dfa);	
	}
	
	static void test_grammar2NDFA2grammar() throws Exception {
		Grammar grammar = SimpleGrammarParser.parseSimple("S->aS; S->aX; X->bS; X->aY; Y->bS; S->a ");
		NDFA ndfa = grammar.toNDFA();
		Viz.show(ndfa);
		NDFA dfa = ndfa.convert2DFA();
		Viz.show(dfa);
		
		System.out.println(dfa.toGrammar());
	}
	public static void main(String[] args) throws Exception {
			test1();
	}

}
