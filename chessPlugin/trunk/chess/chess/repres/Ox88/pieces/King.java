package chess.repres.Ox88.pieces;

import chess.repres.Ox88.KingMoveGenerator;
import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.Piece;

public class King extends Piece {

	public static final byte usturoi[] = { 17, 15, -17, -15, 1, 16, -1, -16 };;

	public King(boolean iswhite) {
		super(iswhite);
	}

	private static final MoveGenerator mgen = new KingMoveGenerator();

	@Override
	public boolean isSliding() {
		return false;
	}

	public String toString() {
		return iswhite ? "K" : "k";
	}

	@Override
	protected MoveGenerator getMoveGenerator() {
		return mgen;
	}

	@Override
	/**
	 * this is improper for the king as it cannot be captured
	 * thus a big value is used
	 */
	public int getValue() {
		return 1000;  
	}
}