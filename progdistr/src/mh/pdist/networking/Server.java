package mh.pdist.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class Server {

	// allocate a small pool 4 testing purposes
	private static final int NTHREADS = 3;

	/**
	 * starts listening on the port, for each connection request it uses the
	 * provided factory to instantiate protocols
	 * 
	 * logs to log4j. logger name is TCP_srv_.nameoftheprotocolclass
	 */
	public static void listenTCP(int port, ProtocolFactory protofact) {
		Logger log = Logger.getLogger("mh.pdist."+"TCP_srv."+protofact.getClass().getSimpleName());
		ExecutorService pool = Executors.newFixedThreadPool(NTHREADS);
		try {
			log.info("threadpool of " + NTHREADS
					+ " allocated to serve requests");

			ServerSocket serverSocket = null;
			boolean listening = true;

			try {
				serverSocket = new ServerSocket(port);
				log.info("protocol " + protofact.getClass().getName()
						+ " started listening on " + port);
			} catch (IOException e) {
				log.fatal("Could not listen on port: ." + port);
				System.exit(-1);
			}

			try {
				while (listening) {
					Socket sock = serverSocket.accept();
					log.info("accepted client connection from "
							+ sock.getRemoteSocketAddress());
					pool.execute(protofact.newProtocol(sock));
				}

				serverSocket.close();
			} catch (IOException ex) {
				log.fatal("IO error in server",ex);
			}
		} finally {
			pool.shutdown();
		}
	}
}
