/**
 * @author miha 
 * @date 25 NOV
 */

package mh.pdist.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

/**
 * A subclass should act as a event handler and hold relevant protocol state.
 * Similar to Swing events, but separate instances do *not* run in the same thread as in AWT.
 *   
 * A subclass is expected to override only the abstract methods and use the provided logger.
 * The logger is specific to the instance.
 * 
 * Implements a generic line protocol. tries to be similar to python twisted but
 * it is stream and thread based not nio async.
 * 
 * WARN: twisted is cooperative these threads are pre-empted; 
 * 
 * subclasses might need syncronisation maybe buffer in/out messages in concurrent queues?
 * 
 * sendline is syncron, it is taking a lock on the out stream of the socket
 * maybe have a sendasync that posts to a queue as well?
 */
public abstract class LineProtocol implements Runnable {
	private Socket socket;
	private boolean connected = false;

	private PrintWriter out;
	private BufferedReader in;

	protected Logger log ;

	public LineProtocol(Socket socket) {
		this.socket = socket;
		this.log = Logger.getLogger("mh.pdist."+getClass().getSimpleName() + "." + socket.getRemoteSocketAddress());
	}

	private void open_connection() throws IOException {
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
				Charset.forName("UTF-8")), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
				Charset.forName("UTF-8")));
		connected = true;

		on_connection_made();

	}

	@Override
	public void run() {
		try {
			try {
				// init streams
				open_connection();

				String inputLine;

				// begin processing input
				while ((inputLine = in.readLine()) != null) {
					log.debug("recv "+ inputLine);
					on_line_recieved(inputLine);
				}
			} finally { // processing is done; try to close what we can
				socket.close(); // implicitly closes the streams
				if (connected) {
					on_connection_lost();
				}
			}
		} catch (Exception e) {
			log.fatal("fatal error aborting",e);
		}

	}

	public void sendLine(String line) {
		synchronized (out) {
			out.println(line);
			log.debug("sent "+ line);
		}

	}

	/**
	 *called on the protocol thread when a line is recieved
	 */
	protected abstract void on_line_recieved(String inputLine);

	protected abstract void on_connection_made();

	protected abstract void on_connection_lost();

	public void loseConnection() {
		try {
			socket.close();
		} catch (IOException e) { // nothing meaningful to do when closing
									// resources fails
			log.warn(e);
		}
	}

}
