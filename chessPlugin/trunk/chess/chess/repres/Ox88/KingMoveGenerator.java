package chess.repres.Ox88;

import java.util.List;

import chess.repres.Ox88.pieces.King;
import chess.repres.Ox88.pieces.Rook;

public class KingMoveGenerator implements MoveGenerator {
	private Board board;
	private RayMoveGenerator rmgen = new RayMoveGenerator(King.usturoi);

	public KingMoveGenerator() {
	}

	@Override
	public List<Move> generateMoves(short location) {
		List<Move> ret = rmgen.generateMoves(location);
		if (isValidKingSideCastle(location, (short) (location + 2))) {
			Move kingsidecastle = genKingSideCastle(location);
			ret.add(kingsidecastle);
		}
		if (isValidQueenSideCastle(location, (short) (location - 2))) {
			Move queensidecastle = genQueenSideCastle(location);
			ret.add(queensidecastle);
		}
		for (Move m : ret) {
			boolean side = m.oldpiece.iswhite;
			if (side) {
				m.newstate.canwhitecastle = false;
				m.newstate.canwhitecastlequeenside = false;
			} else {
				m.newstate.canblackcastle = false;
				m.newstate.canblackcastlequeenside = false;
			}
		}
		return ret;
	}

	@Override
	public boolean isValidMove(short src, short dest) {
		return rmgen.isValidMove(src, dest) || isValidKingSideCastle(src, dest) || isValidQueenSideCastle(src, dest);
	}

	public boolean isValidKingSideCastle(short src, short dest) {
		boolean side = board.b[src].iswhite;
		if((side&&src != 4)||(!side&&src!=116)) return false;
		
		if (dest != src + 2) return false;
		if (board.b[src] == null || board.b[src + 3] == null) return false;
		if (board.b[src + 3].iswhite != board.b[src].iswhite) return false;
		if (board.b[src + 3].getClass() != Rook.class) return false;

		if (!((side && board.canwhitecastle) || (!side && board.canblackcastle))) return false;

		for (int i = src + 1; i <= src + 2; i++)
			if (board.b[i] != null) return false;
		for (short i = src; i <= src + 1; i++)
			if (board.getAttacks().attacked(i, !side)) return false;
		return true;
	}

	public boolean isValidQueenSideCastle(short src, short dest) {
		boolean side = board.b[src].iswhite;
		if((side&&src != 4)||(!side&&src!=116)) return false;
		
		if (dest != src - 2) return false;		
		if (board.b[src] == null || board.b[src - 4] == null) return false;
		if (board.b[src - 4].iswhite != board.b[src].iswhite) return false;
		if (board.b[src - 4].getClass() != Rook.class) return false;

		if (!((side && board.canwhitecastlequeenside) || (!side && board.canblackcastlequeenside))) return false;

		for (int i = src - 1; i >= src - 3; i--)
			if (board.b[i] != null) return false;
		for (short i = src; i >= src - 2; i--)
			if (board.getAttacks().attacked(i, !side)) return false;
		return true;
	}

	@Override
	public Move move(short src, short dest) {
		Move ret = null;

		if (rmgen.isValidMove(src, dest)) {
			ret = rmgen.move(src, dest);
		} else if (isValidKingSideCastle(src, dest)) {
			ret = genKingSideCastle(src);
		} else if (isValidQueenSideCastle(src, dest)) {
			ret = genQueenSideCastle(src);
		}

		if (ret != null) {
			boolean side = board.b[src].iswhite;
			if (side) {
				ret.newstate.canwhitecastle = false;
				ret.newstate.canwhitecastlequeenside = false;
			} else {
				ret.newstate.canblackcastle = false;
				ret.newstate.canblackcastlequeenside = false;
			}
		}
		return ret;
	}

	private Move genKingSideCastle(short location) {
		Board ret = board.duplicate();
		ret.b[location].setLocation(location + 2);
		ret.b[location + 2] = ret.b[location];
		ret.b[location] = null;
		ret.b[location + 3].setLocation(location + 1);
		ret.b[location + 1] = ret.b[location + 3];
		ret.b[location + 3] = null;
		Move m = new Move();
		m.newpiece = ret.b[location + 2];
		m.newstate = ret;
		m.oldpiece = board.b[location];
		m.oldstate = board;

		return m;
	}

	private Move genQueenSideCastle(short location) {
		Board ret = board.duplicate();
		ret.b[location].setLocation(location - 2);
		ret.b[location - 2] = ret.b[location];
		ret.b[location] = null;
		ret.b[location - 4].setLocation(location - 1);
		ret.b[location - 1] = ret.b[location - 4];
		ret.b[location - 4] = null;
		Move m = new Move();
		m.newpiece = ret.b[location - 2];
		m.newstate = ret;
		m.oldpiece = board.b[location];
		m.oldstate = board;

		return m;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void setBoard(Board b) {
		board = b;
		rmgen.setBoard(b);
	}
}
