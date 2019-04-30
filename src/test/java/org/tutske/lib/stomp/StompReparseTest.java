package org.tutske.lib.stomp;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;


public class StompReparseTest {

	private StompFrame message = StompFrame.fromData (
		"COMMAND",
		Stomp.headers (
			"content-length", "the body".length (),
			"with:colon", "colon:colon:colon",
			"with\\backslach", 1234,
			"with\nenter", "with\nenter",
			"with\rcarriage return", "c\rr\r"
		),
		"the body".getBytes ()
	);
	private StompFrame parsed = StompFrame.fromRaw (message.raw ());

	@Test
	public void test () {
		System.out.println (new String (parsed.raw ()));
	}

	@Test
	public void it_should_have_the_rigth_command () {
		assertThat (parsed.command (), is ("COMMAND"));
	}

	@Test
	public void it_should_have_the_content_length () {
		assertThat (parsed.header ("content-length", Integer.class), is (8));
	}

	@Test
	public void it_should_have_headers_with_colons () {
		assertThat (parsed.hasHeader ("with:colon"), is (true));
		assertThat (parsed.header ("with:colon"), is ("colon:colon:colon"));
	}

	@Test
	public void it_should_have_headers_with_enters () {
		assertThat (parsed.hasHeader ("with\nenter"), is (true));
		assertThat (parsed.header ("with\nenter"), is ("with\nenter"));
	}

	@Test
	public void it_should_have_headers_with_carriage_return () {
		assertThat (parsed.hasHeader ("with\rcarriage return"), is (true));
		assertThat (parsed.header ("with\rcarriage return"), is ("c\rr\r"));
	}

	@Test
	public void it_should_have_a_body () {
		assertThat (parsed.getBody (), is ("the body"));
	}

}
