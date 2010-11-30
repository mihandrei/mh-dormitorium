package mh.pdistr.networking;

import java.net.Socket;

/**
 * The logic and state common across individual connections in a protocol are to be stored in this class.
 * WARNING:  implementations have to coordinate access to this common state
 * these infrastructure classes are *not* providing syncronisation
 * 
 * @author miha 
 */
public interface ProtocolFactory {
	public LineProtocol newProtocol(Socket sock);
}
