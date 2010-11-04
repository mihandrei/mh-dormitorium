package chess.repres.Ox88;

import java.util.List;

import chess.repres.Ox88.pieces.Rook;

public class RookMoveGenerator extends RayMoveGenerator {

	public RookMoveGenerator(byte[] raydeltas) {
		super(raydeltas);
	}

	@Override
	public Move move(short src, short dest) {
		Move ret = super.move(src, dest);
		setcastleflags(ret);
		return ret;
	}

	@Override
	public List<Move> generateMoves(short location) {
		List<Move> generateMoves = super.generateMoves(location);
	
		for(Move m:generateMoves)
			setcastleflags(m);
		
		return generateMoves;
	}

	private void setcastleflags(Move m) {
		if (m != null) {
			if (((Rook) (m.oldpiece)).kingsideRook)
				if (m.oldpiece.iswhite)
					m.newstate.canwhitecastle = false;
				else
					m.newstate.canblackcastle = false;
			else if (m.oldpiece.iswhite)
				m.newstate.canwhitecastlequeenside = false;
			else
				m.newstate.canblackcastlequeenside = false;
		}		
	}
}
