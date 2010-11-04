package chess.repres.Ox88.pieces;

import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.Piece;
import chess.repres.Ox88.RayMoveGenerator;

public class Bishop extends Piece {

	public Bishop(boolean iswhite) {
		super(iswhite);
	}

	public static final byte rayDeltaBishop[] = { 17, 15, -17, -15 };
	private static final MoveGenerator rmgen = new RayMoveGenerator(rayDeltaBishop);
	
	public String toString() {
		return iswhite ? "B" : "b";
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
		return 3;
	}
}
