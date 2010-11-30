package mh.pdist.auction.model;


public class Car {
	public int id;
	public String producer;
	public String model;
	public String color;
	public int price;

	public Car(){
		
	}
	public Car(int id, String producer, String model, String color, int price) {
		this.id =id;
		this.producer =producer;
		this.model = model;
		this.color = color;
		this.price = price;
	}

	public static Car fromStr(String line) {
		//zero width negative lookbehind
		String[] ss = line.split("(?<!\\\\),");
		if (ss.length != 5)
			throw new IllegalArgumentException("bad format string");
		
		Car ret = new Car();
		try {
			ret.id = Integer.parseInt(ss[0]);
			ret.producer = uesc(ss[1]);
			ret.model = uesc(ss[2]);
			ret.color = uesc(ss[3]);
			ret.price = Integer.parseInt(ss[4]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("bad format string");
		}
		return ret;
	}

	private static String esc(String s) {
		return s.replace(",", "\\,");
	}

	private static String uesc(String s) {
		return s.replace("\\,", ",");
	}

	public String asString() {
		return String.format("%d,%s,%s,%s,%d", id, esc(producer), esc(model),
				esc(color), price);
	}
	
	public String toString(){
		return asString();
	}
}
