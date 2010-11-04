package chess.repres.Ox88;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveGenerator implements MoveGenerator {
	private Board board;

	private List<Move> generatedMoves;

	private int enpassantloc = -1;

	public PawnMoveGenerator() {
	}

	@Override
	public List<Move> generateMoves(short location) {
		generatedMoves = new ArrayList<Move>(3);

		Piece piece = board.b[location];
		int sgn = piece.iswhite ? 1 : -1;
		int dest;

		dest = location + 16 * sgn;
		if ((dest & 0x88) == 0 && board.b[dest] == null)
			addmove(location, dest, -1, -1);

		dest = location + 17 * sgn;
		if ((dest & 0x88) == 0 && board.b[dest] != null && board.b[dest].iswhite != piece.iswhite)
			addmove(location, dest, dest, -1);
		if ((dest & 0x88) == 0 && dest == board.enpassantLocation && board.isWhiteToMove() == piece.iswhite)
			addmove(location, dest, dest - 16 * sgn, -1);

		dest = location + 15 * sgn;
		if ((dest & 0x88) == 0 && board.b[dest] != null && board.b[dest].iswhite != piece.iswhite)
			addmove(location, dest, dest, -1);
		if ((dest & 0x88) == 0 && dest == board.enpassantLocation && board.isWhiteToMove() == piece.iswhite)
			addmove(location, dest, dest - 16 * sgn, -1);

		// doubles
		if (location / 16 == 1 && piece.iswhite && piece.board.b[location + 16] == null && piece.board.b[location + 32] == null) {
			addmove(location, location + 32, -1, location + 16);
		}

		if (location / 96 == 1 && !piece.iswhite && piece.board.b[location - 16] == null && piece.board.b[location - 32] == null) {
			addmove(location, location - 32, -1, location - 16);
		}

		// en-passant

		List<Move> ret = generatedMoves;
		generatedMoves = null;
		return ret;
	}

	@Override
	/*
	 * validates move and sets the enpassantloc column
	 */
	public boolean isValidMove(short src, short dest) {
		enpassantloc = -1;
		// or even throw ;this should never happen
		if ((src & 0x88) != 0 || (dest & 0x88) != 0)
			return false;

		Piece piece = board.b[src];
		int sgn = piece.iswhite ? 1 : -1;
		int dir = (dest - src) * sgn;

		switch (dir) {
		case 16:
			if (board.b[dest] == null)
				return true;
			break;
		case 17:
		case 15:
			if (board.b[dest] != null && board.b[dest].iswhite != board.b[src].iswhite)
				return true;
			break;
		}

		// doubles
		if (src + 32 == dest && src / 16 == 1 && piece.iswhite && piece.board.b[src + 16] == null && piece.board.b[src + 32] == null) {
			enpassantloc = src + 16;
			return true;
		}

		if (src - 32 == dest && src / 96 == 1 && !piece.iswhite && piece.board.b[src - 16] == null && piece.board.b[src - 32] == null) {
			enpassantloc = src - 16;
			return true;
		}
		// en-passant
		if (board.enpassantLocation == dest)
			return true;

		return false;
	}

	@Override
	public Move move(short src, short dest) {
		if (!isValidMove(src, dest))
			return null;
		int sgn = board.b[src].iswhite ? 1 : -1;
		int tormv = -1;

		if (dest == board.enpassantLocation)
			tormv = dest - 16 * sgn;
		else if (board.b[dest] != null)
			tormv = dest;

		generatedMoves = new ArrayList<Move>();
		addmove(src, dest, tormv, enpassantloc);
		List<Move> ret = generatedMoves;
		generatedMoves = null;
		return ret.get(0);
	}

	private void addmove(int src, int dest, int removePieceIndex, int enPassantColumn) {
		Board ret = board.duplicate();
		ret.enpassantLocation = enPassantColumn;

		if (removePieceIndex != -1)
			ret.remove(ret.b[removePieceIndex]);

		ret.b[src].setLocation(dest);
		ret.b[dest] = ret.b[src];
		ret.b[src] = null;
		Move m = new Move();
		m.newpiece = ret.b[dest];
		m.newstate = ret;
		m.oldpiece = board.b[src];
		m.oldstate = board;
		m.capturedPiece = (removePieceIndex != -1) ? board.b[removePieceIndex] : null;
		generatedMoves.add(m);

	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void setBoard(Board b) {
		board = b;
	}
}
