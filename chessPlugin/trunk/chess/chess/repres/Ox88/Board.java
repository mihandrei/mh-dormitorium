package chess.repres.Ox88;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import chess.repres.Ox88.pieces.Bishop;
import chess.repres.Ox88.pieces.King;
import chess.repres.Ox88.pieces.Knight;
import chess.repres.Ox88.pieces.Pawn;
import chess.repres.Ox88.pieces.Queen;
import chess.repres.Ox88.pieces.Rook;
/**
 * Board is semantically immutable. A new one should be created for a move 
 * TODO: create a boardFactory class that will generate boards. 
 * Move all mutable operations there.
 * @author mihand
 *
 */
public class Board {
	// magic numbers follow
	static final short bitmask = 0x88;
	private static final short blength = (short) 128;
	// end magic
	private static final HashMap<Class<? extends Piece>, Integer> hashv;
	static {
		hashv = new HashMap<Class<? extends Piece>, Integer>(6);
		hashv.put(Rook.class, 0);
		hashv.put(Knight.class, 1);
		hashv.put(Bishop.class, 2);
		hashv.put(Queen.class, 3);
		hashv.put(King.class, 4);
		hashv.put(Pawn.class, 5);
	}

	private static int hashType(Piece piece) {
		return hashType(piece.getClass(), piece.iswhite);
	}

	private static int hashType(Class<? extends Piece> clazz, boolean iswhite) {
		int hash = hashv.get(clazz);
		if (iswhite) hash += 6;
		return hash;
	}

	public static Board createDefaultBoard() {
		Board board = new Board();
		board.add(new Rook(true), 0);
		board.add(new Knight(true), 1);
		board.add(new Bishop(true), 2);
		board.add(new Queen(true), 3);
		board.add(new King(true), 4);
		board.add(new Bishop(true), 5);
		board.add(new Knight(true), 6);
		board.add(new Rook(true), 7);
		for (int i = 16; i < 24; i++)
			board.add(new Pawn(true), i);
		board.add(new Rook(false), 112);
		board.add(new Knight(false), 113);
		board.add(new Bishop(false), 114);
		board.add(new Queen(false), 115);
		board.add(new King(false), 116);
		board.add(new Bishop(false), 117);
		board.add(new Knight(false), 118);
		board.add(new Rook(false), 119);
		for (int i = 96; i < 104; i++)
			board.add(new Pawn(false), i);
		board.canblackcastle = board.canblackcastlequeenside = board.canwhitecastle = board.canwhitecastlequeenside = true;
		return board;
	}

	final Piece[] b = new Piece[blength];;
	final List<Piece>[] plist = new ArrayList[12];
	int enpassantLocation = -1;

	boolean canwhitecastle, canwhitecastlequeenside;
	boolean canblackcastle, canblackcastlequeenside;

	private boolean whiteToMove = true;

	private final Attacks attacks = new Attacks(this);

	public Board() {
		for (int i = 0; i < 12; i++) {
			plist[i] = new ArrayList<Piece>(2);
		}
	}

	/**
	 * copies the state of the board except the en-passant file
	 */
	public Board duplicate() {
		Board ret = new Board();
		for (int i = 0; i < 12; i++) {
			List<Piece> l = plist[i];
			for (int j = 0; j < l.size(); j++) {
				Piece p = (Piece) l.get(j).clone();
				ret.add(p, p.getLocation());
			}
		}
		ret.canblackcastle = canblackcastle;
		ret.canblackcastlequeenside = canblackcastlequeenside;
		ret.canwhitecastle = canwhitecastle;
		ret.canwhitecastlequeenside = canwhitecastlequeenside;
		ret.whiteToMove=!whiteToMove;
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int r = 7; r >= 0; r--) {
			ret.append(System.getProperty("line.separator"));
			for (int f = 0; f < 8; f++)
				ret.append(b[f + r * 16] == null ? "." : b[f + r * 16]);
		}
		return ret.toString();
	}

	void add(Piece p, int loc) {
		p.setLocation(loc);
		p.setBoard(this);
		b[loc] = p;
		plist[hashType(p)].add(p);
	}

	public void add(Piece p, Coordinate loc) {
		add(p, loc.to0x88Location());
	}

	public void remove(Piece p) {
		p.setBoard(null);
		b[p.getLocation()] = null;
		plist[hashType(p)].remove(p);
	}

	public Iterator<Piece> getPieces(Class<? extends Piece> clazz, boolean white) {
		return plist[hashType(clazz, white)].iterator();
	}

	public Iterator<Piece> getPieces(boolean white) {
		return white ? new WhitesIterator() : new BlacksIterator();
	}
	

	public Iterator<Piece> getPieces() {
		return new PieceIterator();
	}

	public Piece getPiece(Coordinate c) {
		return b[c.to0x88Location()];
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
	}

	public Move move(Piece p, Coordinate dest) {
		Move ret = null;
		MoveGenerator moveGenerator = p.getMoveGenerator();
		moveGenerator.setBoard(this);

		if (p.iswhite == whiteToMove) {
			ret = moveGenerator.move(p.location, dest.to0x88Location());
		}
		if (ret != null) {
			ret.newstate.whiteToMove = !whiteToMove;
			if (ret.newstate.attacks.inCheck()) ret = null;
		}
		return ret;
	}

	/**
	 * This method uses the {@link MoveGenerator} provided by getMoveGenerator to create moves.
	 * 
	 * @param b
	 * @return list of moves or null if no move generator is available
	 */
	public List<Move> genMoves(Piece p) {
		MoveGenerator mgen = p.getMoveGenerator();
		mgen.setBoard(this);
		return mgen.generateMoves(p.location);
	}

	public Attacks getAttacks() {
		return attacks;
	}
	
	public void setIsWhiteToMove(boolean c) {
		whiteToMove=c;
	}

	private class PieceIterator implements Iterator<Piece> {
		int ctype = 0, cidx, len;

		public PieceIterator() {
			setrange();
			while (ctype < len && plist[ctype].size() == 0)
				ctype++;
		}
		
		protected void setrange(){
			ctype = 0;
			len = 12;
		}

		@Override
		public boolean hasNext() {
			return ctype < len;
		}

		@Override
		public Piece next() {
			if (!hasNext()) throw new NoSuchElementException();

			Piece ret = plist[ctype].get(cidx);

			if (cidx + 1 < plist[ctype].size())
				cidx++;
			else {
				cidx = 0;
				do {
					ctype++;
				} while (ctype < len && plist[ctype].size() == 0);
			}

			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
	
	private class WhitesIterator extends PieceIterator{
		protected void setrange(){
			ctype = 6;
			len = 12;
		}
	}
	private class BlacksIterator extends PieceIterator{
		protected void setrange(){
			ctype = 0;
			len = 6;
		}
	}
	
}
