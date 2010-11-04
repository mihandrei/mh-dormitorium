package chess.search;

import java.util.Iterator;

import chess.repres.Ox88.Board;
import chess.repres.Ox88.Piece;

public class Evaluator {
	private Evaluator(){		
	}
	private static Evaluator instance = new Evaluator();

	Board b;
	
	public static int eval(Board b) {
		instance.b=b;
		return instance.internalEval();
	}
	
	private int internalEval(){
		int piecesvalue=0;
		Iterator<Piece> pieces = b.getPieces(true);
		while(pieces.hasNext()){
			Piece next = pieces.next();
			piecesvalue+=next.getValue();
		}
		pieces = b.getPieces(false);
		while(pieces.hasNext()){
			Piece next = pieces.next();
			piecesvalue-=next.getValue();
		}
		return piecesvalue;
	}
}
