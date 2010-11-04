package chess.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import search.AdversarialState;
import chess.repres.Ox88.Board;
import chess.repres.Ox88.Move;
import chess.repres.Ox88.Piece;
import chess.repres.Ox88.pieces.King;

public class ChessState implements AdversarialState {
	Move move;
	Board board;
	static final int MAXVAL=10000;
	static final int MINVAL=-10000;

	public ChessState(Move m){
		move=m;
		board = m.newstate;
	}
	
	@Override
	public int utility() {	
		if(issolution())
			return board.isWhiteToMove()?MINVAL:MAXVAL;
		return Evaluator.eval(board);
	}

	@Override
	public boolean issolution() {
		return !board.getPieces(King.class, board.isWhiteToMove()).hasNext();
	}

	@Override
	public List<ChessState> succesors() {
		List<ChessState> ret = new ArrayList<ChessState>();
		Iterator<Piece> piecesItr = board.getPieces(board.isWhiteToMove());
		while(piecesItr.hasNext()){
			Piece piece = piecesItr.next();
			for(Move m : board.genMoves(piece)){
				ret.add(new ChessState(m));
			}
		}
		return ret;
	}
	@Override
	public String toString() {		
		return board.toString();
	}
	public Move getMove(){
		return move;
	}
}
