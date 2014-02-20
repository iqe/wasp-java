package de.edling2.wasp.messages;

import org.junit.Before;
import org.junit.Test;

import de.edling2.wasp.comm.TestStream;
import de.edling2.wasp.messages.DigitalValueMessage.Value;

import static de.edling2.wasp.comm.TestStream.*;
import de.edling2.wasp.config.PinConfig;
import de.edling2.wasp.config.PinFlag;
import de.edling2.wasp.config.PinMode;
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
	public void shouldWritePinConfigMessage() throws Exception {
		// Defaults: Pin 1, digital out, no flags, no debounce, analog range 0-1023
		PinConfig config = new PinConfig();
		stream.writeMessage(new PinConfigMessage("X.1", config));
		byte[] m = s.waspIn.readMessage();
		assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, 4, 0, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));

		// Different modes
		config = new PinConfig();
		for (PinMode mode : PinMode.values()) {
			config.setMode(mode);
			stream.writeMessage(new PinConfigMessage("X.1", config));
			m = s.waspIn.readMessage();
			assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, mode.getValue(), 0, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));
		}

		// Flags
		config = new PinConfig();
		config.getFlags().add(PinFlag.InputPullup);
		stream.writeMessage(new PinConfigMessage("X.1", config));
		m = s.waspIn.readMessage();
		assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, 4, 1, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));

		config.getFlags().add(PinFlag.Reversed);
		stream.writeMessage(new PinConfigMessage("X.1", config));
		m = s.waspIn.readMessage();
		assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, 4, 3, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));

		// Debounce interval
		config = new PinConfig();
		config.setDebounceMillis((long)Math.pow(2, 32) - 1);
		stream.writeMessage(new PinConfigMessage("X.1", config));
		m = s.waspIn.readMessage();
		assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, 4, 0, 255, 255, 255, 255, 0, 0, ssHigh(1023), ssLow(1023));

		// Analog range
		config = new PinConfig();
		config.setAnalogMinValue(Short.MIN_VALUE);
		config.setAnalogMaxValue(Short.MAX_VALUE);
		stream.writeMessage(new PinConfigMessage("X.1", config));
		m = s.waspIn.readMessage();
		assertIsMessage(m, MSG_PIN_CONFIG, 0, 1, 4, 0, 0, 0, 0, 0, ssHigh(Short.MIN_VALUE), ssLow(Short.MIN_VALUE), ssHigh(Short.MAX_VALUE), ssLow(Short.MAX_VALUE));
	}

	@Test
	public void shouldWriteHeartbeatMessage() throws Exception {
		stream.writeMessage(new HeartbeatMessage("X", "Name"));

		byte[] m = s.waspIn.readMessage();

		assertIsMessage(m, WaspMessageFactory.MSG_HEARTBEAT, 4, 'N', 'a', 'm', 'e');
	}

	private void assertIsMessage(byte[] actual, int... expectedBytes) {
		byte[] expected = buildByteArray(expectedBytes);
		assertArrayEquals(expected, actual);
	}
}
