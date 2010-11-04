package chess.search;

import search.Node;
import chess.ComputerPlayer;
import chess.repres.Ox88.Board;
import chess.repres.Ox88.Coordinate;
import chess.repres.Ox88.pieces.Bishop;
import chess.repres.Ox88.pieces.King;
import chess.repres.Ox88.pieces.Knight;
import chess.repres.Ox88.pieces.Pawn;
import chess.repres.Ox88.pieces.Queen;
import chess.repres.Ox88.pieces.Rook;

public class Test {

	String[] strinbrd = new String[]{ 
		"......k." ,
		"....n..." ,
		"....Q.r." ,
		"........" ,
		"........" ,
		"........" ,
		"........" ,
		"K......."};

	void run() {
		Board b = fromstr(strinbrd);
		b.setIsWhiteToMove(true);

		System.out.println(b);

		ComputerPlayer pl = new ComputerPlayer(true);
		pl.setDepth(2);
		pl.play(b);

		for (Node<ChessState> n : pl.getEngine().getPlannedSuccession()) {
			System.out.println(n.getDepth() + "-----util:" + n.getUtility() + "-----");
			System.out.println(n.getState());
		}
	}
	
	
	
	
	static Board fromstr(String[] ss){
		Board b = new Board();
		for(int ln=0;ln<8;ln++)
			for(int cl=0;cl<8;cl++){
				Character ch = ss[7-ln].charAt(cl);
				Coordinate coord = new Coordinate(ln+1,cl+1);
				switch(ch){
				case 'k':
					b.add(new King(false),coord);
					break;
				case 'K':
					b.add(new King(true),coord);
					break;
				case 'q':
					b.add(new Queen(false),coord);
					break;
				case 'Q':
					b.add(new Queen(true),coord);
					break;
				case 'r':
					b.add(new Rook(false),coord);
					break;
				case 'R':
					b.add(new Rook(true),coord);
					break;
				case 'b':
					b.add(new Bishop(false),coord);
					break;
				case 'B':
					b.add(new Bishop(true),coord);
					break;
				case 'p':
					b.add(new Pawn(false),coord);
					break;
				case 'P':
					b.add(new Pawn(true),coord);
					break;
				case 'n':
					b.add(new Knight(false),coord);
					break;
				case 'N':
					b.add(new Knight(true),coord);
					break;
				}
			}
		return b;
	}
	public static void main(String[] args) {
		new Test().run();
	}
}
