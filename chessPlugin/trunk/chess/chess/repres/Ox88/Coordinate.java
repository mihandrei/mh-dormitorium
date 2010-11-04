package chess.repres.Ox88;

import java.util.HashMap;
import java.util.Map;

public final class Coordinate {
	private static Map<Character, Integer> files = new HashMap<Character, Integer>(
			8);
	static {
		files.put('a', 0);
		files.put('b', 1);
		files.put('c', 2);
		files.put('d', 3);
		files.put('e', 4);
		files.put('f', 5);
		files.put('g', 6);
		files.put('h', 7);
	}

	private byte rank;

	private byte file;

	public Coordinate() {
	}

	public Coordinate(int rank, int file) {
		setRank(rank);
		setFile(file);
	}

	public Coordinate(short ox88Location) {
		if ((ox88Location & Board.bitmask) != 0)
			throw new IllegalArgumentException("invalid 0x88 location");
		rank = (byte) (ox88Location / 16);
		file = (byte) (ox88Location % 16);
	}

	public Coordinate(String aritmeticNotation) {
		if (aritmeticNotation.length() != 2)
			throw new IllegalArgumentException("bad argument");

		aritmeticNotation = aritmeticNotation.toLowerCase();

		char filechar = aritmeticNotation.charAt(0);
		Integer fileinteger = files.get(filechar);

		if (fileinteger == null)
			throw new IllegalArgumentException("file out of range a..h");

		file = fileinteger.byteValue();

		char rankchar = aritmeticNotation.charAt(1);

		if (!Character.isDigit(rankchar))
			throw new IllegalArgumentException("non numeric rank");

		rank = Byte.parseByte(Character.toString(rankchar));
		rank--;
	}

	final public void setRank(int rank) {
		rank--;
		if (rank < 0 || rank > 8)
			throw new IllegalArgumentException("rank must be in 0..8");
		this.rank = (byte) rank;
	}

	final public byte getRank() {
		return (byte) (rank + 1);
	}

	final public void setFile(int file) {
		file--;
		if (file < 0 || file > 8)
			throw new IllegalArgumentException("rank must be in 0..8");
		this.file = (byte) file;
	}

	final public byte getFile() {
		return (byte) (file+1);
	}

	public short to0x88Location() {
		return (short) (file + rank * 16);
	}

	public int toArrayCoord() {
		return rank + file * 8;
	}
}
