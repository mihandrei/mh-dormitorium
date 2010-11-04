package search;

public interface AdversarialState extends State {
   /**
    * Called for terminal nodes (solutions or depth limit nodes) to evaluate them
    * > if MAX won 0 for a draw < if MIN won , otherwise a estimated value of the state.
    * @return
    */
	public int utility();
}
