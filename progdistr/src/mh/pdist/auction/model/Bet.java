package mh.pdist.auction.model;

import java.util.HashMap;
import java.util.Map;

public class Bet {

	public long auctionID;
	public long round;
	public long ammount;
	public String usrid;

	public Bet(String usrid, long auctionID, long auctionRound, long ammount) {
		this.auctionID = auctionID;
		this.round = auctionRound;
		this.ammount = ammount;
		this.usrid = usrid;
	}

	public Map<String, Object>  asMap() {
		Map<String, Object> ret = new HashMap<String, Object>(4);
		ret.put("userid", usrid);
		ret.put("ammount", ammount);
		ret.put("auctionID", auctionID);
		ret.put("round", round);	
		return ret;
	}

	public static Bet fromMap(Map<String,Object> mapbet) {
		String userid = (String) mapbet.get("userid"); 
		long ammount = (Long) mapbet.get("ammount"); 
		long auctionID = (Long) mapbet.get("auctionID");
		long auctionRound = (Long) mapbet.get("round");
		
		return new Bet(userid,auctionID,auctionRound,ammount);
	}

}
