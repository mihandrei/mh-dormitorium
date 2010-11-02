package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import theory.Grammar.Production;

public class SimpleGrammarParser {

	public static Grammar parseSimple(String grstr)
			throws Exception {
		grstr = grstr.replaceAll(" ", "").replaceAll("\n", "");

		TreeSet<String> nonterminals = new TreeSet<String>();
		TreeSet<String> terminals = new TreeSet<String>();

		List<Production> productions = new ArrayList<Production>(
				16);

		String[] prods = grstr.split(";");

		for (int line = 0; line < prods.length; line++) {
			String[] prod = prods[line].split("->");
			if (prod.length != 2)
				throw new Exception("err line" + line);

			List<String> head = new ArrayList<String>(prod[0].length());
			List<String> tail = new ArrayList<String>(prod[1].length());

			for (Character chr : prod[0].toCharArray()) {
				head.add(chr.toString());
				addSymbol(chr, nonterminals, terminals);
			}
			for (Character chr : prod[1].toCharArray()) {
				tail.add(chr.toString());
				addSymbol(chr, nonterminals, terminals);
			}

			productions.add(new Production(head, tail));
		}

		return new Grammar(nonterminals, terminals,
				productions, "S", "e");
	}

	private static void addSymbol(Character chr,
			Collection<String> nonterminals, Collection<String> terminals) throws Exception {
		if ("Se->;".contains(Character.toString(chr))) {
			return;
		}

		if (Character.isUpperCase(chr)) {
			nonterminals.add(Character.toString(chr));
		} else if (Character.isLowerCase(chr)) {
			terminals.add(Character.toString(chr));
		} else {
			throw new Exception("invalid symbol" + chr);
		}
	}

}
