package mh.pdist.auction.model;

public class Bet {

	public Integer auctionID;
	public Integer round;
	public Integer ammount;
	public String usrid;

	public Bet(String usrid, Integer auctionID, Integer auctionRound, Integer ammount) {
		this.auctionID = auctionID;
		this.round = auctionRound;
		this.ammount = ammount;
		this.usrid = usrid;
	}

}
