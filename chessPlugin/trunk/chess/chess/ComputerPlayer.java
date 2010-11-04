package chess;

import search.Adversarial;
import search.Node;
import chess.repres.Ox88.Board;
import chess.repres.Ox88.Move;
import chess.search.ChessState;

public class ComputerPlayer {
	Adversarial<ChessState> engine = new Adversarial<ChessState>();
	private boolean color;
	private int depth =  7;

	public ComputerPlayer(boolean white) {
		engine.setUtilityInv(!white);
		color = white;
	}

	public Move play(Board board) {
		if (color != board.isWhiteToMove()) return null;
		Move m = new Move();
		m.newstate = board;
		ChessState state = new ChessState(m);
		Node<ChessState> moveNode = engine.alphaBetaSearch(state, depth);
		System.out.println("value :"+moveNode.getV());

		return moveNode.getState().getMove();
	}
	
	/**
	 * set the number of plies to search . Default is 7
	 */
	public void setDepth(int i) {
		depth = i;
	}
	/**
	 * Returns the underlying chess engine
	 * @return
	 */
	public Adversarial<ChessState> getEngine() {
		return engine;
	}
}
