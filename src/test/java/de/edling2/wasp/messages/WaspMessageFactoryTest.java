package de.edling2.wasp.messages;

import org.junit.Before;
import org.junit.Test;

import static de.edling2.wasp.comm.TestStream.*;
import static org.junit.Assert.*;

public class WaspMessageFactoryTest {
	private WaspMessageFactory factory;
	private WaspMessage message;
	private byte[] messageBytes;

	@Before
	public void setUp() {
		factory = new WaspMessageFactory("X");
	}

	@Test
	public void shouldSetSourcePrefixToHashCode() throws Exception {
		factory = new WaspMessageFactory();
		assertEquals("W" + factory.hashCode(), factory.getSourcePrefix());
	}

	@Test
	public void shouldBuildDigitalValueMessage() throws Exception {
		buildMessageFrom(0x01, usHigh(7), usLow(7), 'T');
		expectDigitalValueMessage("X.7", DigitalValueMessage.Value.Toggle);
	}

	@Test
	public void shouldBuildAnalogValueMessage() throws Exception {
		buildMessageFrom(0x02, usHigh(7), usLow(7), usHigh(1337), usLow(1337));
		expectAnalogValueMessage("X.7", 1337);
	}

	@Test
	public void shouldBuildHeartbeatMessageWithPrefixFromMessage() throws Exception {
		buildMessageFrom(0xFF, 1, 'Z');
		expectHeartbeatMessage("Z");
	}

	@Test
	public void shouldSetSourcePrefixFromHeartbeatMessage() throws Exception {
		buildMessageFrom(0x01, usHigh(7), usLow(7), 'H');
		expectDigitalValueMessage("X.7", DigitalValueMessage.Value.High);

		buildMessageFrom(0xFF, 1, 'Z');
		expectHeartbeatMessage("Z");

		buildMessageFrom(0x01, usHigh(7), usLow(7), 'H');
		expectDigitalValueMessage("Z.7", DigitalValueMessage.Value.High);
	}

	@Test
	public void shouldThrowUnknownMessageException() throws Exception {
		setMessage(0x42);
		expectException(new UnknownMessageException(0x42));
	}

	@Test
	public void shouldThrowInvalidSubjectException() throws Exception {
		try {
			factory.buildMessageBytes(new DigitalValueMessage("Bad Source", DigitalValueMessage.Value.High));
		} catch (Exception e) {
			assertEquals("'Bad Source' is no valid message subject", e.getMessage());
		}

		try {
			factory.buildMessageBytes(new DigitalValueMessage("", DigitalValueMessage.Value.High));
		} catch (Exception e) {
			assertEquals("'' is no valid message subject", e.getMessage());
		}
	}

	private void buildMessageFrom(int... bytes) {
		setMessage(bytes);
		buildMessage();
	}

	private void setMessage(int... bytes) {
		messageBytes = buildByteArray(bytes);
	}

	private void buildMessage() {
		message = factory.buildMessage(messageBytes, messageBytes.length);
	}

	private void expectDigitalValueMessage(String source, DigitalValueMessage.Value value) throws Exception {
		assertTrue(message instanceof DigitalValueMessage);
		assertTrue(message.getTimestamp() > 0);
		assertEquals(source, message.getSource());
		assertEquals(value, ((DigitalValueMessage)message).getValue());
	}

	private void expectAnalogValueMessage(String source, int value) throws Exception {
		assertTrue(message instanceof AnalogValueMessage);
		assertTrue(message.getTimestamp() > 0);
		assertEquals(source, message.getSource());
		assertEquals(value, ((AnalogValueMessage)message).getValue());
	}

	private void expectHeartbeatMessage(String source) throws Exception {
		assertTrue(message instanceof HeartbeatMessage);
		assertTrue(message.getTimestamp() > 0);
		assertEquals(source, message.getSource());
	}

	private void expectException(Exception expected) throws Exception {
		try {
			buildMessage();
			fail("Expected exception '" + expected + "' not thrown");
		} catch (Exception e) {
			assertEquals(expected.getClass(), e.getClass());
			assertEquals(expected.getMessage(), e.getMessage());
		}
	}
}