package chess.repres.Ox88;


public abstract class Piece implements Cloneable {

	public final boolean iswhite;
	protected Board board;
	protected short location;

	public Piece(boolean iswhite) {
		this.iswhite = iswhite;
	}

	public short getLocation() {
		return location;
	}

	protected void setLocation(int loc) {
		location = (short) loc;
	}

	protected void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

	protected abstract MoveGenerator getMoveGenerator();
	
	@Override
	public Object clone() {
		Object ret = null;
		try {
			ret = super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return ret;
	}

	public abstract boolean isSliding();

	public abstract int getValue() ;

}
