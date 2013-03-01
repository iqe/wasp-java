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
		factory = new WaspMessageFactory();
	}

	@Test
	public void shouldBuildDigitalValueMessage() throws Exception {
		buildMessageFrom(0x01, usHigh(7), usLow(7), 'T');
		expectDigitalValueMessage(7, DigitalValueMessage.Value.Toggle);
	}

	@Test
	public void shouldBuildAnalogValueMessage() throws Exception {
		buildMessageFrom(0x02, usHigh(7), usLow(7), usHigh(1337), usLow(1337));
		expectAnalogValueMessage(7, 1337);
	}

	@Test
	public void shouldBuildHeartbeatMessage() throws Exception {
		buildMessageFrom(0xFF);
		expectHeartbeatMessage();
	}

	@Test
	public void shouldThrowUnknownMessageException() throws Exception {
		setMessage(0x42);
		expectException(new UnknownMessageException(0x42));
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

	private void expectDigitalValueMessage(int pin, DigitalValueMessage.Value value) throws Exception {
		assertTrue(message instanceof DigitalValueMessage);
		assertTrue(message.getTimestamp() > 0);
		assertEquals(pin, ((DigitalValueMessage)message).getPin());
		assertEquals(value, ((DigitalValueMessage)message).getValue());
	}

	private void expectAnalogValueMessage(int pin, int value) throws Exception {
		assertTrue(message instanceof AnalogValueMessage);
		assertTrue(message.getTimestamp() > 0);
		assertEquals(pin, ((AnalogValueMessage)message).getPin());
		assertEquals(value, ((AnalogValueMessage)message).getValue());
	}

	private void expectHeartbeatMessage() throws Exception {
		assertTrue(message instanceof HeartbeatMessage);
		assertTrue(message.getTimestamp() > 0);
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