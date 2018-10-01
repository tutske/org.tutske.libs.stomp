package org.tutske.util.stomp;

import org.tutske.utils.PrimitivesParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class StompFrame {

	private static final Charset UTF8 = Charset.forName ("utf-8");

	private final byte [] data;
	private final int start;
	private final int end;
	private final int len;

	private String command;
	private Map<String, String> headers = new HashMap<> ();
	private byte [] body;

	public static StompFrame ping () {
		StompFrame frame = new StompFrame (new byte [] { '\n' }, 0, 1);
		frame.command = "PING";
		return frame;
	}

	public static StompFrame fromRaw (byte [] data) {
		return fromRaw (data, 0, data.length);
	}

	public static StompFrame fromRaw (byte [] data, int start, int len) {
		if ( data.length < 2 && data[0] == '\n' ) { return ping (); }
		else { return new StompFrame (data, start, len); }
	}

	public static StompFrame fromData (String command) {
		return fromData (command, new byte [] {});
	}

	public static StompFrame fromData (String command, Map<String, Object> headers) {
		return fromData (command, headers, new byte [] {});
	}

	public static StompFrame fromData (String command,  byte [] content) {
		return fromData (command, Collections.EMPTY_MAP, content);
	}

	public static StompFrame fromData (String command, Map<String, Object> headers, byte [] content) {
		try ( ByteArrayOutputStream stream = new ByteArrayOutputStream (content.length) ) {
			stream.write (command.getBytes (UTF8));
			stream.write ('\n');

			for ( Map.Entry<String, Object> entry : headers.entrySet () ) {
				stream.write (encode (entry.getKey ()).getBytes (UTF8));
				stream.write (':');
				stream.write (encode (entry.getValue ().toString ()).getBytes (UTF8));
				stream.write ('\n');
			}

			stream.write ('\n');
			stream.write (content);
			stream.write ('\0');

			byte [] raw = stream.toByteArray ();

			return new StompFrame (raw, 0, raw.length);
		} catch (IOException e) {
			throw new RuntimeException ("Creating stomp frame failed", e);
		}
	}

	private StompFrame (byte [] data, int start, int len) {
		this.data = data;
		this.start = start;
		this.len = len;
		this.end = start + len;

		parse ();
	}

	public String command () {
		return command;
	}

	public boolean hasHeader (String key) {
		return headers.containsKey (key);
	}

	public String header (String key) {
		return headers.get (key);
	}

	public <T> T header (String key, Class<T> clazz) {
		return PrimitivesParser.parse (header (key).trim (), clazz);
	}

	public Map<String, String> headers () {
		return headers;
	}

	public String getBody () {
		return new String (body, Charset.forName ("utf-8"));
	}

	public byte [] getByteBody () {
		return body;
	}

	private void parse () {
		int current = start;

		int end = findChar ('\n', current);
		if ( end < 0 ) { failMessage ("Frame does not start with a command."); }

		command = extract (current, end);
		current = end + 1;

		if ( current == data.length ) { return; }

		while ( true ) {
			if ( nextCharsAre (current, '\n') || nextCharsAre (current, '\r', '\n') ) {
				break;
			}

			int sep = findChar (':', current);
			if ( sep < 0 ) { failMessage ("Expected a header but no colon found."); }

			int line = findChar ('\n', sep);
			if ( line < 0 || (sep == current && line > current) ) {
				failMessage ("header not terminated with a newline");
			}

			String header = extract (current, sep);
			String value = extract (sep + 1, line);

			if ( ! headers.containsKey (header) ) {
				headers.put (decode (header), decode (value));
			}
			current = line + 1;
		}

		end = headers.containsKey ("content-length") ?
			current + 1 + Integer.parseInt (headers.get ("content-length").trim ()) :
			findChar ('\0', current);

		if ( end > this.end ) {
			throw new RuntimeException ("content length too large");
		}

		body = Arrays.copyOfRange (data, current + 1, end);
	}

	public byte [] raw () {
		return Arrays.copyOfRange (data, start, end);
	}

	private boolean nextCharsAre (int index, char ... chars) {
		for ( int i = 0; i < chars.length; i++ ) {
			if ( data[i + index] != chars[i] ) { return false; }
		}
		return true;
	}

	private int findChar (char c, int start) {
		for ( int i = start; i < end; i++ ) {
			if ( data[i] == c ) { return i; }
		}

		return -1;
	}

	private String extract (int start, int end) {
		if ( end > 0 && data[end - 1 ] == '\r' ) { end = end - 1; }
		return new String (data, start, end - start);
	}

	private void failMessage (String reason) {
		throw new RuntimeException ("Illegal Stomp Message" + (reason.isEmpty () ? "" : ": ") + reason);
	}

	private static String encode (String value) {
		return value
			.replaceAll ("\\\\", "\\\\\\\\")
			.replaceAll ("\\r", "\\\\r")
			.replaceAll ("\\n", "\\\\n")
			.replaceAll (":", "\\\\c")
			;
	}

	private static String decode (String value) {
		return value
			.replaceAll ("\\\\r", "\r")
			.replaceAll ("\\\\n", "\n")
			.replaceAll ("\\\\c", ":")
			.replaceAll ("\\\\", "\\\\")
			;
	}

}
