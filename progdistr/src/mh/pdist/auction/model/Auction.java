package mh.pdist.auction.model;

public class Auction {
	public final int increment;
	public final int auctionID;
	
	public int round = 0;
	public int ammount = 100;
	public String current_winner;
	public Car car;
	
	public Auction(int auctionID,int startingAmmount,int increment,Car car){
		this.auctionID = auctionID;
		this.ammount =startingAmmount;
		this.increment = increment;
		this.car = car;
	}
	
	public void acceptBet(Bet bet) throws InvalidBetException{
		if (bet.auctionID!= auctionID){
			throw new InvalidBetException("bet submitted to a different auction");
		}
		if(bet.round!=round){
			throw new InvalidBetException("bet is out of order");
		}
		if(bet.ammount < ammount + increment){
			throw new InvalidBetException("ammount betted must be greater than the current amount + increment");
		}
		
		ammount = bet.ammount;
		current_winner = bet.usrid;
	}
}
