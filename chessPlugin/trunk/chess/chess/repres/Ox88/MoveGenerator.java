package chess.repres.Ox88;

import java.util.List;

public interface MoveGenerator {

	void setBoard(Board b);
	boolean isValidMove(short src, short dest);

	Move move(short src, short dest);

	/**
	 * generates the moves
	 * 
	 * @param location
	 *            of the piece to move. A valid 0x88 index and at that location
	 *            a piece on the board
	 * @return
	 */
	List<Move> generateMoves(short location);
	Board getBoard();

}