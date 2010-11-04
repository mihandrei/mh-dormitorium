package chess.repres.Ox88;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chess.repres.Ox88.pieces.Bishop;
import chess.repres.Ox88.pieces.King;
import chess.repres.Ox88.pieces.Knight;
import chess.repres.Ox88.pieces.Pawn;
import chess.repres.Ox88.pieces.Queen;
import chess.repres.Ox88.pieces.Rook;

public class Attacks {

	public static int[] typeDeltas = new int[256];
	private static Map<Class<? extends Piece>, Integer> typemasks = new HashMap<Class<? extends Piece>, Integer>();

	static {
		// 0000 0000
		// r n b q k p
		final int pm = 0x1;
		final int km = 0x2;
		final int qm = 0x4;
		final int bm = 0x8;
		final int nm = 0x10;
		final int rm = 0x20;

		for (int m : Knight.magicKnight) {
			typeDeltas[128 + m] = nm;
			typeDeltas[128 - m] = nm;
		}
		for (int m : King.usturoi) {
			typeDeltas[128 + m] = km;
			typeDeltas[128 - m] = km;
		}
		for (int i = 1; i < 8; i++) {
			for (int m : Rook.rayDeltaRook) {
				typeDeltas[128 + m * i] = typeDeltas[128 + m * i] | rm | qm;
				typeDeltas[128 - m * i] = typeDeltas[128 - m * i] | rm | qm;
			}

			for (int m : Bishop.rayDeltaBishop) {
				typeDeltas[128 + m * i] = typeDeltas[128 + m * i] | bm | qm;
				typeDeltas[128 - m * i] = typeDeltas[128 - m * i] | bm | qm;
			}
		}

		// TODO: pawn
		typeDeltas[128 + 15] = typeDeltas[128 + 15] | pm;
		typeDeltas[128 + 17] = typeDeltas[128 + 17] | pm;
		typeDeltas[128 - 15] = typeDeltas[128 - 15] | pm;
		typeDeltas[128 - 17] = typeDeltas[128 - 17] | pm;

		typemasks.put(Pawn.class, pm);
		typemasks.put(King.class, km);
		typemasks.put(Queen.class, qm);
		typemasks.put(Bishop.class, bm);
		typemasks.put(Knight.class, nm);
		typemasks.put(Rook.class, rm);
	}
	private Board board;


	public Attacks(Board board) {
		this.board = board;
	}

	public List<Piece> attackers(short location, boolean advside,boolean findfirst) {
		List<Piece> attackers = new ArrayList<Piece>();
		
		for (Iterator<Piece> adversarPieceIterator = board.getPieces(advside); adversarPieceIterator
				.hasNext();) {
			Piece p = adversarPieceIterator.next();
			int delta = p.location - location;
			int mask = typeDeltas[128 + delta];
			int typemask = typemasks.get(p.getClass());
	
			if ((mask & typemask) != 0) {
				if (p.isSliding()) {
					MoveGenerator moveGenerator = p.getMoveGenerator();
					moveGenerator.setBoard(board);
					if (moveGenerator.isValidMove(p.location, location)){
						attackers.add(p);
						if(findfirst) return attackers;
					}
				} else if (p instanceof Pawn) {
					// pawn
					if ((!p.iswhite && delta > 0)
							|| (p.iswhite && delta < 0)) {
						attackers.add(p);
						if(findfirst) return attackers;
					}
				} else {
					// knight and king
					attackers.add(p);
					if(findfirst) return attackers;
				}
			}
		}
	
		return attackers;
	}
	
	public boolean attacked(short location,boolean advside){
		return attackers(location, advside, true).size()!=0;
	}
	

	public boolean inCheck() {
		final boolean advside = board.isWhiteToMove();
		Piece ourKing = board.getPieces(King.class, !advside).next();
		return attacked(ourKing.location, advside);
	}

}
