package mh.pdist.auction.net;

import java.net.Socket;
import java.util.Map;

import mh.pdist.auction.model.Bet;
import mh.pdistr.networking.JsonFrameProtocol;

public class AuctionProtocol extends JsonFrameProtocol {
	private enum PState  {DISSCONNECTED, CONNECTED, IN_AUCTION }
	private PState state = PState.DISSCONNECTED;
	
	private AuctionFactory factory;
		
	private String userid;
	
	public String getUserid() {
		return userid;
	}

	public AuctionProtocol(Socket socket, AuctionFactory auctionFactory) {
		super(socket);
		this.factory = auctionFactory;
	}

	@Override
	protected void on_connection_lost() {
		log.info("conn lost");
		if(state != PState.DISSCONNECTED){
			state = PState.DISSCONNECTED;
			factory.usr_left(userid);
		}
	}

	@Override
	protected void on_connection_made() {
		log.info("connected");
		state = PState.CONNECTED;
	}

	@Override
	protected void on_frame(String frameName, Object msg) {		
		switch(state){
		case CONNECTED:			
			if(frameName.equals("join")){				
				userid = (String) ((Map)msg).get("userid"); //WARN: class casts
				factory.usr_joined(userid,this);
				
				state = PState.IN_AUCTION;
				
				factory.broadcastInfo(userid + "joined");
				log.info(userid + "joined");
			}else{
				sendErrorFrame("expected join frame");
			}
			break;
		case IN_AUCTION:
			if(frameName.equals("bet")){	
				Map bet =  (Map)msg;
				Integer ammount = (Integer) bet.get("ammount"); 
				Integer auctionID = (Integer) bet.get("auctionID");
				Integer auctionRound = (Integer) bet.get("round");
				
				factory.postBet(new Bet(userid,auctionID,auctionRound,ammount));
				sendInfoFrame("bet placed");
			}else{
				sendErrorFrame("expected bet frame");
			}
			break;		
		}
		
	}
	
	

}

