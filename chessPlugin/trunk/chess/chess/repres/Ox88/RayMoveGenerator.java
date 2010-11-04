package chess.repres.Ox88;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mihand Generates simple ray tracing moves
 */
public class RayMoveGenerator implements MoveGenerator {
	private Board board;
	private final byte[] raydeltas;

	public RayMoveGenerator(byte[] raydeltas) {
		this.raydeltas = raydeltas;
	}

	private List<Move> generatedMoves;

	public static final boolean isSameRay(short location, short destination,
			byte ray) {
		int n = (destination - location) / ray;// dest =src + n*ray
		// (dest-src)%ray==0 |n|<8
		int r = (destination - location) % ray;
		return r == 0 && Math.abs(n) < 8;
	}

	public static final boolean isValidLocation(short location) {
		return (location & Board.bitmask) == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chess.repres.Ox88.MoveGenerator#isValidMove(short, short)
	 */
	public boolean isValidMove(short src, short dest) {
		rayloop: for (byte ray : raydeltas) {
			if (!isSameRay(src, dest, ray)) continue;
			for (int i = src + ray; i != dest; i += ray) {
				// hit a piece or the margin
				if ((i & Board.bitmask) != 0 || board.b[i] != null)
					continue rayloop;
				if (!board.b[src].isSliding()) continue rayloop;
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chess.repres.Ox88.MoveGenerator#move(short, short)
	 */
	public Move move(short src, short dest) {
		if (!isValidMove(src, dest)) return null;
		generatedMoves = new ArrayList<Move>();
		genmove(src, dest);
		List<Move> ret = generatedMoves;
		generatedMoves = null;
		return ret.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chess.repres.Ox88.MoveGenerator#generateMoves(short)
	 */
	public List<Move> generateMoves(short location) {
		generatedMoves = new ArrayList<Move>();
		for (byte delta : raydeltas) {
			for (int i = location + delta; (i & Board.bitmask) == 0; i += delta) {
				if (!genmove(location, (short) i)) break;
			}
		}

		List<Move> ret = generatedMoves;
		generatedMoves = null;
		return ret;
	}

	private boolean genmove(short src, short dest) {
		Piece piece = board.b[src];
		Piece destpiece = board.b[dest];
		boolean continueRay = (destpiece == null) && piece.isSliding();

		if( destpiece!=null && (destpiece.iswhite == piece.iswhite))
			return continueRay;
		
		addmove(src, dest, destpiece);
		
		return continueRay;
	}

	private void addmove(short src, short dest, Piece capturedPiece) {
		Board ret = board.duplicate();
		if (capturedPiece!=null) ret.remove(ret.b[dest]);
		ret.b[src].setLocation(dest);
		ret.b[dest] = ret.b[src];
		ret.b[src] = null;
		Move m = new Move();
		m.capturedPiece = capturedPiece;
		m.newpiece = ret.b[dest];
		m.newstate = ret;
		m.oldpiece = board.b[src];
		m.oldstate = board;
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
