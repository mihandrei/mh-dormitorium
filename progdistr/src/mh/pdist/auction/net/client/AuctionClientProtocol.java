package mh.pdist.auction.net.client;

import java.net.Socket;
import java.util.Map;

import mh.pdist.auction.model.Auction;
import mh.pdist.auction.model.Bet;
import mh.pdist.networking.JsonFrameProtocol;
/**
 * frames
 * {"frame":"join","msg":{"userid":"mihai"}}
 * {"frame":"bet","msg":{"auctionID":1, "round":0,"ammount":130}}
 * @author miha
 *
 */
public class AuctionClientProtocol extends JsonFrameProtocol {
	private static final String JOIN_FRAME = "join";
	private static final String BET_FRAME = "bet";
	private static final String BET_CONFIRMED_FRAME = "bet_placed";
	private static final String JOINED_FRAME = "joined";
	public static final String AUCTION_STARTED_FRAME = "auction_started";
	public static final String AUCTION_CLOSED_FRAME = "auction_closed";
	
	private enum PState  {DISSCONNECTED, CONNECTED, JOINED, IN_AUCTION }
	private PState state = PState.DISSCONNECTED;
	
	private String userid;
	private Auction c_auction;
	
	public synchronized String getUserid() {
		return userid;
	}	

	public AuctionClientProtocol(Socket socket) {
		super(socket);
	}

	@Override
	protected void on_connection_lost() {
		log.info("conn lost");
		if(state != PState.DISSCONNECTED){
			state = PState.DISSCONNECTED;
		}
	}

	@Override
	protected void on_connection_made() {
		log.info("connected");
		state = PState.CONNECTED;
	}

	@Override
	protected synchronized void on_frame(String frameName, Object msg) {		
		if(frameName.equals(ERROR_FRAME)){
			log.error((String)msg);
			return;
		}
		
		if(frameName.equals(INFO_FRAME)){
			log.info((String)msg);
			return;
		}
		
		switch(state){
		case CONNECTED:			
			if(frameName.equals(JOINED_FRAME)){										
				state = PState.JOINED;				
				log.info("join confirmed");				
			}else{
				sendErrorFrame("expected join confirmation");
			}
			break;
		case JOINED:
			if(frameName.equals(AUCTION_STARTED_FRAME)){
				c_auction = Auction.fromMap((Map<String, Object>) msg);
				log.info("auction started: " + c_auction);
				state = PState.IN_AUCTION;
			}			
			
		case IN_AUCTION:
			if(frameName.equals(AUCTION_CLOSED_FRAME)){
				log.info("auction closed");
				c_auction = null;
				state= PState.JOINED;
			}			
			else if(frameName.equals(BET_CONFIRMED_FRAME)){									
				log.info("bet placed");
			}else{
				sendErrorFrame("expected bet confirmation");
			}
			break;		
		}		
	}
	
	public synchronized void placeBet(long ammount){
		if(state == PState.IN_AUCTION){
			Bet bet = new Bet(userid, c_auction.auctionID, c_auction.round, ammount);
			sendFrame(BET_FRAME, bet.asMap());
		}else{
			log.warn("cannot place bet not in an auction");
		}
	}

	public synchronized boolean needs_login() {
		return state != PState.JOINED && state != PState.IN_AUCTION;
	}

	public synchronized void login(String str) {
		sendFrame(JOIN_FRAME, str);
		
	}
		
}

