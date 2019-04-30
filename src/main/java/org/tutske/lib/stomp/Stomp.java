package org.tutske.lib.stomp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class Stomp {

	public static class Commands {
		public static final String CONNECT = "CONNECT";
		public static final String DISCONNECT = "DISCONNECT";
		public static final String SEND = "SEND";
		public static final String RECEIPT = "RECEIPT";
		public static final String MESSAGE = "MESSAGE";
		public static final String ERROR = "ERROR";
		public static final String ACK = "ACK";
		public static final String NACK = "NACK";
	}

	public static Map<String, Object> headers (Object ... headers) {
		if ( headers.length % 2 != 0 ) {
			throw new IllegalArgumentException ("Expected an even number of arguments");
		}

		Map<String, Object> map = new LinkedHashMap<> ();
		for ( int i = 0; i < headers.length; i += 2 ) {
			map.put ((String) headers[i], headers[i + 1]);
		}

		return map;
	}

	public static StompFrame connect () { return frame (Commands.CONNECT); }
	public static StompFrame connect (Map<String, Object> headers) { return frame (Commands.CONNECT, headers); }
	public static StompFrame connect (Object ... headers) { return frame (Commands.CONNECT, headers (headers)); }

	public static StompFrame disconnect () { return frame (Commands.DISCONNECT); }
	public static StompFrame disconnect (Map<String, Object> headers) { return frame (Commands.DISCONNECT, headers); }
	public static StompFrame disconnect (Object ... headers) { return frame (Commands.DISCONNECT, headers (headers)); }

	public static StompFrame send () { return frame (Commands.SEND); }
	public static StompFrame send (Map<String, Object> headers) { return frame (Commands.SEND, headers); }
	public static StompFrame send (byte [] data) { return frame (Commands.SEND, data); }
	public static StompFrame send (Map<String, Object> headers, byte [] data) { return frame (Commands.SEND, headers, data); }

	public static StompFrame receipt () { return frame (Commands.RECEIPT); }
	public static StompFrame receipt (Map<String, Object> headers) { return frame (Commands.RECEIPT, headers); }
	public static StompFrame receipt (byte [] data) { return frame (Commands.RECEIPT, data); }
	public static StompFrame receipt (Map<String, Object> headers, byte [] data) { return frame (Commands.RECEIPT, headers, data); }

	public static StompFrame message () { return frame (Commands.MESSAGE); }
	public static StompFrame message (Map<String, Object> headers) { return frame (Commands.MESSAGE, headers); }
	public static StompFrame message (byte [] data) { return frame (Commands.MESSAGE, data); }
	public static StompFrame message (Map<String, Object> headers, byte [] data) { return frame (Commands.MESSAGE, headers, data); }

	public static StompFrame error () { return frame (Commands.ERROR); }
	public static StompFrame error (Map<String, Object> headers) { return frame (Commands.ERROR, headers); }
	public static StompFrame error (byte [] data) { return frame (Commands.ERROR, data); }
	public static StompFrame error (String msg) { return frame (Commands.ERROR, msg.getBytes ()); }
	public static StompFrame error (Map<String, Object> headers, byte [] data) { return frame (Commands.ERROR, headers, data); }

	public static StompFrame ack () { return frame (Commands.ACK); }
	public static StompFrame ack (Map<String, Object> headers) { return frame (Commands.ACK, headers); }
	public static StompFrame ack (Object ... headers) { return frame (Commands.ACK, headers (headers)); }

	public static StompFrame nack () { return frame (Commands.NACK); }
	public static StompFrame nack (Map<String, Object> headers) { return frame (Commands.NACK, headers); }
	public static StompFrame nack (Object ... headers) { return frame (Commands.NACK, headers (headers)); }


	public static StompFrame frame (String command) {
		return StompFrame.fromData (command, Collections.EMPTY_MAP, new byte [] {});
	}

	public static StompFrame frame (String command, Map<String, Object> headers) {
		return StompFrame.fromData (command, headers, new byte [] {});
	}

	public static StompFrame frame (String command, byte [] data) {
		return StompFrame.fromData (command, Collections.EMPTY_MAP, data);
	}

	public static StompFrame frame (String command, Map<String, Object> headers, byte [] data) {
		return StompFrame.fromData (command, headers, data);
	}

}
