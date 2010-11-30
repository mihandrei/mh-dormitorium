package mh.pdist.auction.net;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import mh.pdist.auction.model.Auction;
import mh.pdist.auction.model.Car;
import mh.pdist.auction.model.CarDataSource;
import mh.pdistr.networking.ProtocolFactory;
import mh.pdistr.networking.Server;

public class Main {

	public static void main(String[] args) throws IOException {	
		CarDataSource ds = new CarDataSource("/home/miha/Desktop/cardb");
		System.out.println(ds.carindex.toString());
		ds.put(new Car(1,"dacia","logan","coapta", 3000));		
		ds.put(new Car(7,"trabant","furia","alba",2030));
		ds.put(new Car(8,"mercedes","s7","black",2030));
		ds.commit();
		
		
		Deque<Auction> auctions = new ArrayDeque<Auction>(ds.carindex.size());
		int auctionID = 0;
		for(Car c:ds.carindex.values()){
			auctions.add(new Auction(auctionID, c.price, 5, c));
			auctionID +=1;
		}
		ProtocolFactory protofact = new AuctionFactory(auctions);
		Server.listenTCP(4444, protofact);

	}

}
