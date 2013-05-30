package de.edling2.wasp.messages;

import org.junit.Before;
import org.junit.Test;

import de.edling2.wasp.comm.TestStream;
import de.edling2.wasp.messages.DigitalValueMessage.Value;

import static de.edling2.wasp.comm.TestStream.*;
import static de.edling2.wasp.messages.WaspMessageFactory.*;
import static org.junit.Assert.*;

public class WaspMessageOutputStreamTest {
	private TestStream s;
	private WaspMessageOutputStream stream;

	@Before
	public void setUp() {
		s = new TestStream();
		stream = new WaspMessageOutputStream(s.waspOut, "X");
	}

	@Test
	public void shouldWriteDigitalValueMessage() throws Exception {
		stream.writeMessage(new DigitalValueMessage("X.1", Value.High));
		stream.writeMessage(new DigitalValueMessage("X.2", Value.Low));
		stream.writeMessage(new DigitalValueMessage("X.3", Value.Toggle));

		byte[] m1 = s.waspIn.readMessage();
		byte[] m2 = s.waspIn.readMessage();
		byte[] m3 = s.waspIn.readMessage();

		assertIsMessage(m1, MSG_DIGITAL_IN, 0x00, 0x01, 'H');
		assertIsMessage(m2, MSG_DIGITAL_IN, 0x00, 0x02, 'L');
		assertIsMessage(m3, MSG_DIGITAL_IN, 0x00, 0x03, 'T');
	}

	@Test
	public void shouldWriteAnalogValueMessage() throws Exception {
		stream.writeMessage(new AnalogValueMessage("X.1", 0));
		stream.writeMessage(new AnalogValueMessage("X.2", 15));
		stream.writeMessage(new AnalogValueMessage("X.3", -5));
		stream.writeMessage(new AnalogValueMessage("X.4", Short.MIN_VALUE));
		stream.writeMessage(new AnalogValueMessage("X.5", Short.MAX_VALUE));

		byte[] m1 = s.waspIn.readMessage();
		byte[] m2 = s.waspIn.readMessage();
		byte[] m3 = s.waspIn.readMessage();
		byte[] m4 = s.waspIn.readMessage();
		byte[] m5 = s.waspIn.readMessage();

		assertIsMessage(m1, MSG_ANALOG_IN, 0x00, 0x01, ssHigh(0), ssLow(0));
		assertIsMessage(m2, MSG_ANALOG_IN, 0x00, 0x02, ssHigh(15), ssLow(15));
		assertIsMessage(m3, MSG_ANALOG_IN, 0x00, 0x03, ssHigh(-5), ssLow(-5));
		assertIsMessage(m4, MSG_ANALOG_IN, 0x00, 0x04, ssHigh(Short.MIN_VALUE), ssLow(Short.MIN_VALUE));
		assertIsMessage(m5, MSG_ANALOG_IN, 0x00, 0x05, ssHigh(Short.MAX_VALUE), ssLow(Short.MAX_VALUE));
	}

	@Test
	public void shouldWriteDigitalMotorMessage() throws Exception {
		stream.writeMessage(new DigitalMotorMessage("X.1", DigitalMotorMessage.Direction.Stop));
		stream.writeMessage(new DigitalMotorMessage("X.2", DigitalMotorMessage.Direction.Forward));
		stream.writeMessage(new DigitalMotorMessage("X.3", DigitalMotorMessage.Direction.Reverse));

		byte[] m1 = s.waspIn.readMessage();
		byte[] m2 = s.waspIn.readMessage();
		byte[] m3 = s.waspIn.readMessage();

		assertIsMessage(m1, MSG_MOTOR_IN, 0x00, 0x01, 0);
		assertIsMessage(m2, MSG_MOTOR_IN, 0x00, 0x02, 1);
		assertIsMessage(m3, MSG_MOTOR_IN, 0x00, 0x03, 2);
	}

	@Test
	public void shouldWriteHeartbeatMessage() throws Exception {
		stream.writeMessage(new HeartbeatMessage("X"));

		byte[] m = s.waspIn.readMessage();

		assertIsMessage(m, WaspMessageFactory.MSG_HEARTBEAT, 1, 'X');
	}

	private void assertIsMessage(byte[] actual, int... expectedBytes) {
		byte[] expected = buildByteArray(expectedBytes);
		assertArrayEquals(expected, actual);
	}
}
