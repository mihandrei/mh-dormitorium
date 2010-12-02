package mh.pdist.auction.net.srv;

import java.net.Socket;
import java.util.Map;

import mh.pdist.auction.model.Bet;
import mh.pdist.networking.JsonFrameProtocol;
/**
 * frames
 * {"frame":"join","msg":{"userid":"mihai"}}
 * {"frame":"bet","msg":{"auctionID":1, "round":0,"ammount":130}}
 * @author miha
 *
 * WARN : not realy implementing the protocol; parts of it is in the factory to simplify synchronisation
 * not a maintainable setup
 */
public class AuctionProtocol extends JsonFrameProtocol {
	private static final String JOIN_FRAME = "join";
	private static final String BET_FRAME = "bet";
	private static final String BET_CONFIRMED_FRAME = "bet_placed";
	private static final String JOINED_FRAME = "joined";
	public static final String AUCTION_STARTED_FRAME = "auction_started";
	public static final String AUCTION_CLOSED_FRAME = "auction_closed";
	
	private enum PState  {DISSCONNECTED, CONNECTED, JOINED }
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
			if(frameName.equals(JOIN_FRAME)){				
				userid = (String) msg; //WARN: class casts
				factory.usr_joined(userid,this);
				
				state = PState.JOINED;
				sendFrame(JOINED_FRAME, userid);
				
				factory.broadcastInfo(userid + " joined");
				log.info(userid +" joined");
			}else{
				sendErrorFrame("expected join frame got "+frameName);
			}
			break;
		case JOINED:
			if(frameName.equals(BET_FRAME)){	
				Map mapbet =  (Map)msg;
				Bet bet = Bet.fromMap(mapbet);				
				
				factory.postBet(bet);
				sendFrame(BET_CONFIRMED_FRAME, "");
			}else{
				sendErrorFrame("expected bet frame got "+frameName);
			}
			break;		
		}
		
	}
	
	

}

