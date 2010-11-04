package chess.repres.Ox88.pieces;

import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.Piece;
import chess.repres.Ox88.RookMoveGenerator;

public final class Rook extends Piece {

	public static final byte rayDeltaRook[] = { 1, 16, -1, -16 };
	private static final MoveGenerator rmgen= new RookMoveGenerator(rayDeltaRook);

	public Rook(boolean iswhite) {
		super(iswhite);
	}
	public boolean kingsideRook;

	public String toString() {
		return iswhite ? "R" : "r";
	}

	@Override
	public boolean isSliding() {
		return true;
	}
	@Override
	protected MoveGenerator getMoveGenerator() {
		return rmgen;
	}

	@Override
	public int getValue() {
		return 5;
	}
}
