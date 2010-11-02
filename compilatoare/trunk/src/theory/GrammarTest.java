package theory;

import static org.junit.Assert.*;

import org.junit.Test;

public class GrammarTest {

	static String contextsenzitive = "S->aBSc;" +
			"S->abc;" +
			"Ba->aB;" +
			"Bb->bb;";
	
	static String cfree = "S->aSb; S->ab";

	private String reg = "S->aA; A->aA; A->bB; B->bB; B->e";
	
	@Test
	public void testParseSimple() throws Exception {
		SimpleGrammarParser.parseSimple(cfree);
		SimpleGrammarParser.parseSimple(contextsenzitive);
	}

	@Test
	public void testIsContextFree() throws Exception {
		assertTrue( SimpleGrammarParser.parseSimple(cfree).isContextFree());
		assertFalse(SimpleGrammarParser.parseSimple(contextsenzitive).isContextFree());
	}

	@Test
	public void testIsRegular() throws Exception {
		assertFalse(SimpleGrammarParser.parseSimple(cfree).isRegular());
		assertTrue(SimpleGrammarParser.parseSimple(reg).isRegular());
	}


	@Test
	public void testIsLinear() throws Exception {
		assertTrue(SimpleGrammarParser.parseSimple(cfree).isLinear());
		
	}
}
