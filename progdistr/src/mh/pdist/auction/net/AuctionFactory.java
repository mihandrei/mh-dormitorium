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
 * FIXME:multithreaded sychronisation here must be *CHECKED*!
 * 
 * Is this becoming too complex for a lab assignment?!
 * 
 * @author miha
 * 
 */
public class AuctionFactory implements ProtocolFactory {
	private Map<String, AuctionProtocol> connections = new HashMap<String, AuctionProtocol>();
	private AuctionProcessor auctionProcessor;
	private Deque<Auction> auctions;

	private Logger log = Logger.getLogger("mh.pdist."
			+ getClass().getSimpleName());

	@Override
	public LineProtocol newProtocol(Socket sock) {
		return new AuctionProtocol(sock, this);
	}

	public AuctionFactory(Deque<Auction> auctions) {
		auctionProcessor = new AuctionProcessor();
		this.auctions = auctions;
	}

	public void start() {
		new Thread(auctionProcessor, "auction-proc").start();
	}

	public void broadcastInfo(String message) {
		synchronized (connections) {
			for (AuctionProtocol proto : connections.values()) {
				proto.sendInfoFrame(message);
			}
		}
	}

	public void postBet(Bet bet) {
		try {
			auctionProcessor.bets.put(bet);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // handel this
			e.printStackTrace();
		}
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
				// !this is taking locks! Check for deadlock
				oldConnection.loseConnection();
				oldConnection.sendErrorFrame("you logged in from"
						+ " somewhere else.dropping connection.");
			}
			connections.put(userid, proto);

			if (connections.size() >= 3) {
				auctionProcessor.eventqueue.offer("CANSTART");
			}
		}
	}

	public void usr_left(String userid) {
		synchronized (connections) {
			connections.remove(userid);
		}

	}

	class AuctionProcessor implements Runnable {
		/**
		 * protocol threads post bets to this queue This thread processes those
		 * bets
		 */
		private LinkedBlockingQueue<Bet> bets = new LinkedBlockingQueue<Bet>();
		/**
		 * when enough users to start the auction join; the thread calling
		 * usr_joined puts in this queue a signalling message This thread waits
		 * for that message before starting a auction.
		 * 
		 * Did this instead of wait() notifyall() beacuse it has a simpler
		 * semantics
		 */
		private LinkedBlockingQueue<String> eventqueue = new LinkedBlockingQueue<String>(
				1);
		private volatile boolean running = true;

		@Override
		public void run() {
			while (running && auctions.size() != 0) {
				// get next auction
				Auction auction = auctions.pop();

				// we can start if there are at least 3 logged in users
				log.info("next auction " + auction.auctionID);
						
				boolean canstart = false;
				synchronized (connections) {
					canstart = connections.size() >= 3;
				}

				//wait for at least 3 participants
				if (!canstart) {
					log.info(" awaiting connections");
					try {
						String event = "";
						while (!"CANSTART".equals(event)) {
							event = eventqueue.take();
						}
					} catch (InterruptedException e) {
						log.info("auctionprocessor interrupted while "
								+ "waiting for connections, aborting");
						running = false;
						break;
					}
				}

				// factory broadcast auction messages
				broadcastInfo(String.format(
						"auction nr %s started for the car %s ",
						auction.auctionID, auction.car.toString()));

				boolean auctionRunning = true;

				while (auctionRunning) {
					Bet bet;
					try {
						// wait 3 seconds for a bet
						bet = bets.poll(3, TimeUnit.SECONDS);
						if (bet == null) { // no bet yet send notice that the
							// auction will close
							broadcastInfo("auction will close in 3 seconds"
									+ " if no new bets are placed");
						}
						bet = bets.poll(3, TimeUnit.SECONDS);
						if (bet == null) { // ok > 6 seconds without a bet , we
							// have a winner
							auctionRunning = false;
							break;
						}

						try {
							auction.acceptBet(bet);
						} catch (InvalidBetException e) {
							synchronized (connections) {
								connections.get(bet.usrid).sendErrorFrame(
										e.toString());
							}
						}
					} catch (InterruptedException e1) {
						auctionRunning = false;
						// Thread.currentThread().interrupt(); //stil have to do
						// this?
						running = false;
						break;
					}
				}

				broadcastInfo(String.format(
						"auction nr %s closed for the car [%s] won by %s",
						auction.auctionID, auction.car, auction.current_winner));

			}
			
			broadcastInfo("actions finished. Bye");

		}

		public void requestShutdown() {
			running = false;
		}

	}
}
