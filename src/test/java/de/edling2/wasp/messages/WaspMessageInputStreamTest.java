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
		stream = new WaspMessageInputStream(s.waspIn, "X");
	}

	@Test
	public void shouldReadDigitalValueMessages() throws Exception {
		addMessage(MSG_DIGITAL_IN, usHigh(1), usLow(1), 'H');
		addMessage(MSG_DIGITAL_IN, usHigh(2), usLow(2), 'L');
		addMessage(MSG_DIGITAL_IN, usHigh(3), usLow(3), 'T');

		WaspMessage msgHigh = stream.readMessage();
		WaspMessage msgLow = stream.readMessage();
		WaspMessage msgToggle = stream.readMessage();

		assertIsDigitalValueMessage(msgHigh, "X.1", DigitalValueMessage.Value.High);
		assertIsDigitalValueMessage(msgLow, "X.2", DigitalValueMessage.Value.Low);
		assertIsDigitalValueMessage(msgToggle, "X.3", DigitalValueMessage.Value.Toggle);
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

		assertIsAnalogValueMessage(m1, "X.1", 0);
		assertIsAnalogValueMessage(m2, "X.2", 15);
		assertIsAnalogValueMessage(m3, "X.3", -5);
		assertIsAnalogValueMessage(m4, "X.4", Short.MIN_VALUE);
		assertIsAnalogValueMessage(m5, "X.5", Short.MAX_VALUE);
	}

	@Test
	public void shouldReadDigitalMotorMessage() throws Exception {
		addMessage(MSG_MOTOR_IN, usHigh(1), usLow(1), 0);
		addMessage(MSG_MOTOR_IN, usHigh(2), usLow(2), 1);
		addMessage(MSG_MOTOR_IN, usHigh(3), usLow(3), 2);

		WaspMessage m1 = stream.readMessage();
		WaspMessage m2 = stream.readMessage();
		WaspMessage m3 = stream.readMessage();

		assertIsDigitalMotorMessage(m1, "X.1", DigitalMotorMessage.Direction.Stop);
		assertIsDigitalMotorMessage(m2, "X.2", DigitalMotorMessage.Direction.Forward);
		assertIsDigitalMotorMessage(m3, "X.3", DigitalMotorMessage.Direction.Reverse);
	}

	@Test
	public void shouldReadHeartbeatMessage() throws Exception {
		addMessage(MSG_HEARTBEAT, 1, 'X');
		WaspMessage m = stream.readMessage();
		assertIsHeartbeatMessage(m, "X");
	}

	private void addMessage(int... b) throws Exception {
		byte[] bytes = buildByteArray(b);
		s.waspOut.writeMessage(bytes);
	}

	private void assertIsDigitalValueMessage(WaspMessage message, String source, DigitalValueMessage.Value value) {
		assertEquals(DigitalValueMessage.class, message.getClass());

		DigitalValueMessage dvMessage = (DigitalValueMessage)message;
		assertEquals(source, dvMessage.getSource());
		assertEquals(value, dvMessage.getValue());
	}

	private void assertIsAnalogValueMessage(WaspMessage message, String source, int value) {
		assertEquals(AnalogValueMessage.class, message.getClass());

		AnalogValueMessage avMessage = (AnalogValueMessage)message;
		assertEquals(source, avMessage.getSource());
		assertEquals(value, avMessage.getValue());
	}

	private void assertIsDigitalMotorMessage(WaspMessage message, String source, DigitalMotorMessage.Direction direction) {
		assertEquals(DigitalMotorMessage.class, message.getClass());

		DigitalMotorMessage dmMessage = (DigitalMotorMessage)message;
		assertEquals(source, dmMessage.getSource());
		assertEquals(direction, dmMessage.getDirection());
	}

	private void assertIsHeartbeatMessage(WaspMessage message, String source) {
		assertEquals(HeartbeatMessage.class, message.getClass());
		assertEquals(source, message.getSource());
	}
}
