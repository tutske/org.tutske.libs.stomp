package org.tutske.util.stomp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.tutske.util.stomp.Stomp.*;

import org.junit.Test;

import java.util.Map;


public class StompTest {

	@Test
	public void it_should_create_stomp_messages_with_well_known_commands  () {
		assertThat (connect ().command (), is ("CONNECT"));
		assertThat (disconnect ().command (), is ("DISCONNECT"));
		assertThat (send ().command (), is ("SEND"));
		assertThat (receipt ().command (), is ("RECEIPT"));
		assertThat (message ().command (), is ("MESSAGE"));
		assertThat (error ().command (), is ("ERROR"));
		assertThat (ack ().command (), is ("ACK"));
		assertThat (nack ().command (), is ("NACK"));
	}

	@Test
	public void it_should_create_stomp_messages_with_header_maps_for_well_known_commands () {
		Map<String, Object> headers = headers ("header", "value");

		assertThat (connect (headers).headers (), hasKey ("header"));
		assertThat (disconnect (headers).headers (), hasKey ("header"));
		assertThat (send (headers).headers (), hasKey ("header"));
		assertThat (receipt (headers).headers (), hasKey ("header"));
		assertThat (message (headers).headers (), hasKey ("header"));
		assertThat (error (headers).headers (), hasKey ("header"));
		assertThat (ack (headers).headers (), hasKey ("header"));
		assertThat (nack (headers).headers (), hasKey ("header"));
	}

	@Test
	public void it_should_create_stomp_messages_with_well_known_commands_and_content () {
		String body = "This is some content for the body of stomp messages";
		byte [] bytes = body.getBytes ();

		assertThat (send (bytes).getBody (), is (body));
		assertThat (receipt (bytes).getBody (), is (body));
		assertThat (message (bytes).getBody (), is (body));
		assertThat (error (bytes).getBody (), is (body));
	}

	@Test
	public void it_should_create_stomp_messages_with_headers_and_body_of_well_known_commands_verify_header () {
		Map<String, Object> headers = headers ("header", "value");
		String body = "This is some content for the body of stomp messages";
		byte [] bytes = body.getBytes ();

		assertThat (send (headers, bytes).headers (), hasKey ("header"));
		assertThat (receipt (headers, bytes).headers (), hasKey ("header"));
		assertThat (message (headers, bytes).headers (), hasKey ("header"));
		assertThat (error (headers, bytes).headers (), hasKey ("header"));
	}

	@Test
	public void it_should_create_stomp_messages_with_headers_and_body_of_well_known_commands_verify_body () {
		Map<String, Object> headers = headers ("header", "value");
		String body = "This is some content for the body of stomp messages";
		byte [] bytes = body.getBytes ();

		assertThat (send (headers, bytes).getBody (), is (body));
		assertThat (receipt (headers, bytes).getBody (), is (body));
		assertThat (message (headers, bytes).getBody (), is (body));
		assertThat (error (headers, bytes).getBody (), is (body));
	}

	@Test
	public void it_should_have_shortcuts_for_nack_frame_creation_with_headers () {
		assertThat (nack ("header", "value").header ("header"), is ("value"));
		assertThat (ack ("header", "value").header ("header"), is ("value"));
		assertThat (connect ("header", "value").header ("header"), is ("value"));
		assertThat (disconnect ("header", "value").header ("header"), is ("value"));
	}

	@Test
	public void it_should_have_a_short_cut_for_creating_error_frames_from_a_string () {
		StompFrame frame = error ("The description of the error");
		assertThat (frame.getBody (), is ("The description of the error"));
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_about_odd_number_of_header_arguments () {
		headers ("header", "value", "only-header");
	}

}
