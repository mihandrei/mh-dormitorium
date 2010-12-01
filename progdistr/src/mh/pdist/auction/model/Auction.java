package mh.pdist.auction.model;

import java.util.HashMap;
import java.util.Map;

public class Auction {
	public final long increment;
	public final long auctionID;

	public long round = 0;
	public long ammount = 100;
	public String current_winner;
	public Car car;

	public Auction(long auctionID, long startingAmmount, long increment, Car car) {
		this.auctionID = auctionID;
		this.ammount = startingAmmount;
		this.increment = increment;
		this.car = car;
	}

	public void acceptBet(Bet bet) throws InvalidBetException {
		if (bet.auctionID != auctionID) {
			throw new InvalidBetException(
					"bet submitted to a different auction");
		}
		if (bet.round != round) {
			throw new InvalidBetException("bet is out of order");
		}
		if (bet.ammount < ammount + increment) {
			throw new InvalidBetException(
					"ammount betted must be greater than the current amount + increment");
		}

		ammount = bet.ammount;
		current_winner = bet.usrid;
	}

	public Map<String, Object> asMap() {
		Map<String, Object> ret = new HashMap<String, Object>(6);
		ret.put("auctionID", auctionID);
		ret.put("increment", increment);
		ret.put("current_winner", current_winner);
		ret.put("round", round);
		ret.put("car", car.asMap());
		ret.put("ammount", ammount);
		return ret;
	}

	public static Auction fromMap(Map<String, Object> msg) {
		long id = (Long) msg.get("auctionID");
		long ammount = (Long) msg.get("ammount");
		long increment = (Long) msg.get("increment");
		Auction auct = new Auction(id,	ammount, increment,	Car.fromMap((Map<String, Object>) msg.get("car")));
		auct.current_winner = (String) msg.get("current_winner");
		auct.round = (Long) msg.get("round");
		return auct;
	}
}
