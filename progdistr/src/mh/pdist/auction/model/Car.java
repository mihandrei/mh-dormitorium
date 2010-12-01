package mh.pdist.auction.model;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Car {
	public long id;
	public String producer;
	public String model;
	public String color;
	public long price;

	private static JSONParser parser = new JSONParser();

	public Car() {

	}

	public Car( long id, String producer, String model, String color, long price) {
		this.id = id;
		this.producer = producer;
		this.model = model;
		this.color = color;
		this.price = price;
	}

	public static Car fromCsvStr(String line) {
		// zero width negative lookbehind
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

	public static Car fromJsonStr(String line) throws ParseException {
		JSONObject map = (JSONObject) parser.parse(line);
		return fromMap(map);
	}

	public String asCsvString() {
		return String.format("%d,%s,%s,%s,%d", id, esc(producer), esc(model),
				esc(color), price);
	}

	public String asJsonString() {
		return JSONValue.toJSONString(asMap());
	}

	public String toString() {
		return asJsonString();
	}

	public Map<String, Object> asMap() {
		Map<String, Object> ret = new HashMap<String, Object>(5);
		ret.put("id", id);
		ret.put("producer", producer);
		ret.put("model", model);
		ret.put("color", color);
		ret.put("price", price);
		return ret;
	}

	public static Car fromMap(Map<String, Object> msg) {
		long id = (Long) msg.get("id");
		long price = (Long) msg.get("price");
		String producer = (String) msg.get("producer");
		String model = (String) msg.get("model");
		String color = (String) msg.get("color");
		
		return new Car(id,producer,model,color,  price);
	}
}
