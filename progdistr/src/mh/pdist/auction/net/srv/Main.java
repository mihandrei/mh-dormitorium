package mh.pdist.auction.net.srv;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import mh.pdist.auction.model.Auction;
import mh.pdist.auction.model.Car;
import mh.pdist.auction.model.CarDataSource;
import mh.pdist.networking.Server;

public class Main {
	static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException {			
		int port = Integer.parseInt(args[0]);
		
		CarDataSource ds = null;
		try {
			ds = new CarDataSource("cardb");
			System.out.println(ds.carindex.toString());
			ds.put(new Car(1,"dacia","logan","coapta", 3000));		
			ds.put(new Car(7,"trabant","furia","alba",2030));
			ds.put(new Car(8,"mercedes","s7","black",2030));
			ds.commit();
		} catch (ParseException e) {
			log.fatal("could not open data source",e);
			System.exit(-1);
		}catch(IOException e){
			log.fatal("could not open data source",e);
			System.exit(-1);
		}		
		
		Deque<Auction> auctions = new ArrayDeque<Auction>(ds.carindex.size());
		int auctionID = 0;
		for(Car c:ds.carindex.values()){
			auctions.add(new Auction(auctionID, c.price, 5, c));
			auctionID +=1;
		}
		AuctionFactory protofact = new AuctionFactory(auctions);
		protofact.start();
		
		Server.listenTCP(port, protofact);

	}

}
