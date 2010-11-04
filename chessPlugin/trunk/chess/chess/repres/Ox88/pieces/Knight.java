package chess.repres.Ox88.pieces;

import chess.repres.Ox88.MoveGenerator;
import chess.repres.Ox88.Piece;
import chess.repres.Ox88.RayMoveGenerator;

public class Knight extends Piece {
	public Knight(boolean iswhite) {
		super(iswhite);
	}
	
	public static final byte magicKnight[] = { 18, 14, -18, -14, 33, 31, -33, -31 };
	private static final MoveGenerator rmgen=new RayMoveGenerator(magicKnight);
	
	@Override
	public boolean isSliding() {
		return false;
	}
	public String toString() {
		return iswhite ? "N" : "n";
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
