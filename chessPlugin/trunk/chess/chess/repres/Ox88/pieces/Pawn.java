package chess.repres.Ox88.pieces;

import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.PawnMoveGenerator;
import chess.repres.Ox88.Piece;

public class Pawn extends Piece {

	private static final MoveGenerator rmgen = new PawnMoveGenerator();
	public Pawn(boolean iswhite) {
		super(iswhite);
	}

	@Override
	public boolean isSliding() {
		return false;
	}

	public String toString() {
		return iswhite ? "P" : "p";
	}
	
	@Override
	protected MoveGenerator getMoveGenerator() {
		return rmgen;
	}

	@Override
	public int getValue() {
		return 1;
	}
}
