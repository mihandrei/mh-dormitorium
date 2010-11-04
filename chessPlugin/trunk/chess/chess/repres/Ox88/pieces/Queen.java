package chess.repres.Ox88.pieces;

import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.Piece;
import chess.repres.Ox88.RayMoveGenerator;

public class Queen extends Piece {

	public static final byte rayDeltaQueen[] = { 17, 15, -17, -15, 1, 16, -1, -16 };
	private static final MoveGenerator rmgen = new RayMoveGenerator(rayDeltaQueen);

	public Queen(boolean iswhite) {
		super(iswhite);
	}

	public String toString() {
		return iswhite ? "Q" : "q";
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
		return 9;
	}
}
