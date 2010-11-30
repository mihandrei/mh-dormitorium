package mh.pdist.auction.net;

import java.net.Socket;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import mh.pdist.auction.model.Auction;
import mh.pdist.auction.model.Bet;
import mh.pdist.auction.model.InvalidBetException;
import mh.pdistr.networking.LineProtocol;
import mh.pdistr.networking.ProtocolFactory;

import org.apache.log4j.Logger;

/**
 * TODO: operations on the connections array MUST be synchronised ! add joined,
 * left synchronised methods here!
 * 
 * TODO:multithreaded sychronisation here must be *CHECKED*! 
 * 
 * Is this becoming too complex for a lab assignment?!
 * @author miha
 * 
 */
public class AuctionFactory implements ProtocolFactory {
	private Map<String, AuctionProtocol> connections = new HashMap<String, AuctionProtocol>();
	private AuctionProcessor auctionProcessor;
	private Deque<Auction> auctions;

	private Logger log = Logger.getLogger(getClass().getName());

	@Override
	public LineProtocol newProtocol(Socket sock) {
		return new AuctionProtocol(sock, this);
	}

	public AuctionFactory(Deque<Auction> auctions) {
		auctionProcessor = new AuctionProcessor();
		new Thread(auctionProcessor).run();
		this.auctions = auctions;
	}

	public void broadcastInfo(String message) {
		synchronized (connections) {
			for (AuctionProtocol proto : connections.values()) {
				proto.sendInfoFrame(message);
			}
		}
	}

	public void postBet(Bet bet) {
		auctionProcessor.post(bet);
	}

	/**
	 * notifies the factory that a user joined on the specified connection
	 * 
	 * It takes a lock on the list of users , so it might block if a user is
	 * joining leaving or the factory is broadcasting.
	 */
	public void usr_joined(String userid, AuctionProtocol proto) {
		synchronized (connections) {
			AuctionProtocol oldConnection = connections.get(userid);
			if (oldConnection != null) {
				oldConnection
						.sendErrorFrame("you logged in from somewhere else.dropping connection.");
				oldConnection.loseConnection();
			}
			connections.put(userid, proto);
		}
	}

	public void usr_left(String userid) {
		synchronized (connections) {
			connections.remove(userid);
		}

	}

	class AuctionProcessor implements Runnable {
		private LinkedBlockingQueue<Bet> bets = new LinkedBlockingQueue<Bet>();
		private boolean running = true;

		@Override
		public void run() {
			while (running && auctions.size()!=0) {
				// get next auction
				Auction auction = auctions.pop();
				// wait for at least 3 participants
				log.info("next auction " + auction.auctionID
						+ "; awaiting connections");

				try {
					synchronized(connections){
						while (connections.size() < 3)
							wait();
					}
				} catch (InterruptedException e) {
					log.error("unexpected interruption " + e); // deal with this
				}
				
				// factory broadcast auction messages
				broadcastInfo(String.format(
						"auction nr %s started for the car %s ",
						auction.auctionID, auction.car.toString()));
				
				boolean auctionRunning = true;
				
				while (auctionRunning ) {
					Bet bet;
					try {
						//wait 3 seconds for a bet
						bet = bets.poll(3, TimeUnit.SECONDS);
						if(bet == null){ //no bet yet send notice that the auction will close
							broadcastInfo("auction will close in 3 seconds if no newe bets are placed");
						}
						bet = bets.poll(3, TimeUnit.SECONDS);
						if(bet == null){ //ok > 6 seconds without a bet , we have a winner
							auctionRunning = false;
							break;
						}
						
						try {
							auction.acceptBet(bet);
						} catch (InvalidBetException e) {
							synchronized(connections){
								connections.get(bet.usrid).sendErrorFrame(e.toString());
							}
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				broadcastInfo(String.format(
						"auction nr %s started for the car %s won by %s", auction.auctionID,auction.car,auction.current_winner));
				
			}

		}

		public void requestShutdown() {
			running = false;
		}

		public void post(Bet bet) {
			try {
				bets.put(bet);
			} catch (InterruptedException e) {

				// TODO: deal with a possible interuuption here
			}

		}
	}
}
