package theory;

public class CompactDFA {	
	private final char[][] transitions;
	private boolean[] finals;
    private int initialState;
	private int currentState;

	public CompactDFA(char[][] transitions,
			boolean[] finals, int initialState) {
		this.finals = finals;
		this.initialState = initialState;
		this.currentState = initialState;
		this.transitions = transitions;
	}

	public void reset(){
		currentState = initialState;
	}
	
	public boolean isAccepting(){
		return finals[currentState];
	}
	
	public void put(char str){
		currentState =  transitions[currentState][str];		
	}
	
	public void put(String str){
		for(char c:str.toCharArray()){
			put(c);
		}
	}

}
