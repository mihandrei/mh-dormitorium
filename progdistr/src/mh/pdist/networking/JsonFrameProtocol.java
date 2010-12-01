package mh.pdist.networking;

import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Implements a sort of labeled json packages format.
 * 
 * Extends the line protocol by parsing a line as a JSON object of the form:
 * { frame:"frame_name", msg: json }
 * 
 * @author miha
 */
public abstract class JsonFrameProtocol extends LineProtocol {
	public static final String INFO_FRAME = "info";
	public static final String ERROR_FRAME = "error";
	
	private JSONParser parser = new JSONParser();
	
	public JsonFrameProtocol(Socket socket) {
		super(socket);
	}
	
	@Override
	protected final void on_line_recieved(String inputLine) {
		try {
			JSONObject frame = (JSONObject) parser.parse(inputLine);
			String frame_name = (String) frame.get("frame");
			Object msg =  frame.get("msg");
			on_frame(frame_name, msg);			
		} catch (ParseException e) {			
			sendErrorFrame("could not parse frame");
			log.error("could not parse frame",e);
			loseConnection();
		}catch(ClassCastException e){
			sendErrorFrame("could not parse frame");
			log.error("could not parse frame",e);
			loseConnection();
		}
	}

	protected abstract void on_frame(String frameName, Object msg);	

	public void sendFrame(String name,Object msg){
		Map<String, Object> frame = new LinkedHashMap<String, Object>(2);
		frame.put("frame", name);
		frame.put("msg", msg);
		String line = JSONValue.toJSONString(frame);
		sendLine(line);
	}
	
	public void sendErrorFrame(String err){
		Map<String, Object> frame = new LinkedHashMap<String, Object>(2);
		frame.put("frame", ERROR_FRAME);
		frame.put("msg", err);
		String line = JSONValue.toJSONString(frame);
		sendLine(line);
	}
	
	public void sendInfoFrame(String inf){
		Map<String, Object> frame = new LinkedHashMap<String, Object>(2);
		frame.put("frame", INFO_FRAME);
		frame.put("msg", inf);
		String line = JSONValue.toJSONString(frame);
		sendLine(line);
	}
}
