package org.tutske.util.stomp;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


public class StompFrameTest {

	@Test
	public void sanity_should_construct_a_valid_message () throws Exception {
		StompFrame message = message ("CONNECT", "", "the body");

		Field field = message.getClass ().getDeclaredField ("data");
		field.setAccessible (true);
		byte [] data = (byte []) field.get (message);

		assertThat (data, is ("CONNECT\n\nthe body\0".getBytes ()));
	}

	@Test
	public void it_should_create_frames_with_just_a_command () {
		StompFrame frame = StompFrame.fromData ("COMMAND");
		assertThat (frame.command (), is ("COMMAND"));
	}

	@Test
	public void it_should_create_frames_with_just_headers_and_a_command () {
		Map<String, Object> headers = new LinkedHashMap<> ();
		headers.put ("header", "value");
		StompFrame frame = StompFrame.fromData ("COMMAND", headers);

		assertThat (frame.command (), is ("COMMAND"));
		assertThat (frame.header ("header"), is ("value"));
	}

	@Test
	public void it_should_find_the_command () {
		StompFrame message = message ("CONNECT", "", "the body");
		assertThat (message.command (), is ("CONNECT"));
	}

	@Test
	public void it_should_find_the_command_when_CRLF_is_used () {
		StompFrame message = messageWithSep ("\r\n", "CONNECT", "", "the body");
		assertThat (message.command (), is ("CONNECT"));
	}

	@Test
	public void it_should_find_headers () {
		StompFrame message = message ("CONNECT", "x-custom: test", "", "the body");
		assertThat (message.hasHeader ("x-custom"), is (true));
		assertThat (message.header ("x-custom").trim (), is ("test"));
	}

	@Test
	public void it_should_find_headers_when_CRLF_is_used () {
		StompFrame message = messageWithSep ("\r\n", "CONNECT", "x-custom: test", "", "the body");
		assertThat (message.hasHeader ("x-custom"), is (true));
		assertThat (message.header ("x-custom").trim (), is ("test"));
	}

	@Test
	public void it_should_find_multiple_headers () {
		StompFrame message = message (
			"CONNECT",
			"x-first-custom: first",
			"x-second-custom: second",
			"",
			"the body"
		);

		assertThat (message.hasHeader ("x-first-custom"), is (true));
		assertThat (message.hasHeader ("x-second-custom"), is (true));
	}

	@Test
	public void it_should_find_headers_with_colons_in_them () {
		StompFrame message = message ("CONNECT", "with\\ccolon: first", "", "the body");
		assertThat (message.hasHeader ("with:colon"), is (true));
	}

	@Test
	public void it_should_find_and_convert_primitive_header_values () {
		StompFrame message = message ("CONNECT", "number: 1234", "", "the body");
		assertThat (message.header ("number", Integer.class), is (1234));
	}

	@Test
	public void it_should_reed_the_body () {
		StompFrame message = message ("CONNECT", "", "the body");
		assertThat (message.getBody (), is ("the body"));
	}

	@Test
	public void it_should_read_the_body_as_bytes () {
		StompFrame frame = message ("CONNECT", "", "the body");
		assertThat (frame.getByteBody (), is ("the body".getBytes ()));
	}

	@Test
	public void it_should_stop_the_message_at_a_null_byte () {
		StompFrame message = message ("CONNECT", "", "the body\0tail");
		assertThat (message.getBody (), is ("the body"));
	}

	@Test
	public void it_should_read_over_nulls_when_a_content_length_is_specified () {
		StompFrame message = message ("CONNECT", "content-length: 13", "", "the body\0tail");
		assertThat (message.getBody (), is ("the body\0tail"));
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_about_large_contents () {
		message ("CONNECT", "content-length: 1234", "", "the body");
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_when_there_is_an_malformed_header () {
		message ("CONNECT", "content-length = 13", "", "the body");
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_about_missing_headers_section () {
		message ("CONNECT", "the body");
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_about_incorrectly_terminated_header_line () {
		message ("CONNECT", "header: value\0");
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_about_frames_without_a_properly_terminated_command () {
		message ("CONNECT\0");
	}

	@Test
	public void it_should_have_the_provided_command_string () {
		StompFrame message = StompFrame.fromData ("COMMAND", "the body".getBytes ());
		assertThat (message.command (), is ("COMMAND"));
	}

	private StompFrame message (String ... parts) {
		return messageWithSep ("\n", parts);
	}

	private StompFrame messageWithSep (String sep, String ... parts) {
		StringBuilder builder = new StringBuilder ();
		for ( String part : parts ) { builder.append (sep).append (part); }
		builder.delete (0, sep.length ());
		builder.append ("\0");

		return StompFrame.fromRaw (builder.toString ().getBytes ());
	}

}
