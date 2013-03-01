package de.edling2.wasp.messages;

import de.edling2.wasp.comm.TestStream;
import org.junit.Before;
import org.junit.Test;

import static de.edling2.wasp.comm.TestStream.*;
import static de.edling2.wasp.messages.WaspMessageFactory.*;
import static org.junit.Assert.*;

public class WaspMessageInputStreamTest {
	private TestStream s;
	private WaspMessageInputStream stream;

	@Before
	public void setUp() {
		s = new TestStream();
		stream = new WaspMessageInputStream(s.waspIn);
	}

	@Test
	public void shouldReadDigitalValueMessages() throws Exception {
		addMessage(MSG_DIGITAL_IN, usHigh(1), usLow(1), 'H');
		addMessage(MSG_DIGITAL_IN, usHigh(2), usLow(2), 'L');
		addMessage(MSG_DIGITAL_IN, usHigh(3), usLow(3), 'T');

		WaspMessage msgHigh = stream.readMessage();
		WaspMessage msgLow = stream.readMessage();
		WaspMessage msgToggle = stream.readMessage();

		assertIsDigitalValueMessage(msgHigh, 1, DigitalValueMessage.Value.High);
		assertIsDigitalValueMessage(msgLow, 2, DigitalValueMessage.Value.Low);
		assertIsDigitalValueMessage(msgToggle, 3, DigitalValueMessage.Value.Toggle);
	}

	@Test
	public void shouldReadAnalogValueMessages() throws Exception {
		addMessage(MSG_ANALOG_IN, usHigh(1), usLow(1), ssHigh(0), ssLow(0));
		addMessage(MSG_ANALOG_IN, usHigh(2), usLow(2), ssHigh(15), ssLow(15));
		addMessage(MSG_ANALOG_IN, usHigh(3), usLow(3), ssHigh(-5), ssLow(-5));
		addMessage(MSG_ANALOG_IN, usHigh(4), usLow(4), ssHigh(Short.MIN_VALUE), ssLow(Short.MIN_VALUE));
		addMessage(MSG_ANALOG_IN, usHigh(5), usLow(5), ssHigh(Short.MAX_VALUE), ssLow(Short.MAX_VALUE));

		WaspMessage m1 = stream.readMessage();
		WaspMessage m2 = stream.readMessage();
		WaspMessage m3 = stream.readMessage();
		WaspMessage m4 = stream.readMessage();
		WaspMessage m5 = stream.readMessage();

		assertIsAnalogValueMessage(m1, 1, 0);
		assertIsAnalogValueMessage(m2, 2, 15);
		assertIsAnalogValueMessage(m3, 3, -5);
		assertIsAnalogValueMessage(m4, 4, Short.MIN_VALUE);
		assertIsAnalogValueMessage(m5, 5, Short.MAX_VALUE);
	}

	@Test
	public void shouldReadHeartbeatMessage() throws Exception {
		addMessage(MSG_HEARTBEAT);
		WaspMessage m = stream.readMessage();
		assertIsHeartbeatMessage(m);
	}

	private void addMessage(int... b) throws Exception {
		byte[] bytes = buildByteArray(b);
		s.waspOut.writeMessage(bytes);
	}

	private void assertIsDigitalValueMessage(WaspMessage message, int pin, DigitalValueMessage.Value value) {
		assertEquals(DigitalValueMessage.class, message.getClass());

		DigitalValueMessage dvMessage = (DigitalValueMessage)message;
		assertEquals(pin, dvMessage.getPin());
		assertEquals(value, dvMessage.getValue());
	}

	private void assertIsAnalogValueMessage(WaspMessage message, int pin, int value) {
		assertEquals(AnalogValueMessage.class, message.getClass());

		AnalogValueMessage avMessage = (AnalogValueMessage)message;
		assertEquals(pin, avMessage.getPin());
		assertEquals(value, avMessage.getValue());
	}

	private void assertIsHeartbeatMessage(WaspMessage message) {
		assertEquals(HeartbeatMessage.class, message.getClass());
	}
}
