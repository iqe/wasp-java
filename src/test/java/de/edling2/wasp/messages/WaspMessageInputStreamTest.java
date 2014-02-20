package de.edling2.wasp.messages;

import de.edling2.wasp.config.PinConfig;
import de.edling2.wasp.comm.TestStream;
import org.junit.Before;
import org.junit.Test;

import static de.edling2.wasp.comm.TestStream.*;
import de.edling2.wasp.config.PinFlag;
import de.edling2.wasp.config.PinMode;
import static de.edling2.wasp.messages.WaspMessageFactory.*;
import static org.junit.Assert.*;

public class WaspMessageInputStreamTest {
	private TestStream s;
	private WaspMessageInputStream stream;

	@Before
	public void setUp() throws Exception {
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
	public void shouldReadPinConfigMessage() throws Exception {
		// Defaults: Pin 1, digital out, no flags, no debounce, analog range 0-1023
		PinConfig config = new PinConfig();
		addMessage(MSG_PIN_CONFIG, 0, 1, 4, 0, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));

		WaspMessage m = stream.readMessage();
		assertIsPinConfigMessage(m, "X.1", config);

		// Different modes
		config = new PinConfig();
		for (PinMode mode : PinMode.values()) {
			config.setMode(mode);
			addMessage(MSG_PIN_CONFIG, 0, 1, mode.getValue(), 0, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));

			m = stream.readMessage();
			assertIsPinConfigMessage(m, "X.1", config);
		}

		// Flags
		config = new PinConfig();
		config.getFlags().add(PinFlag.InputPullup);

		addMessage(MSG_PIN_CONFIG, 0, 1, 4, 1, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));
		m = stream.readMessage();
		assertIsPinConfigMessage(m, "X.1", config);

		config.getFlags().add(PinFlag.Reversed);
		addMessage(MSG_PIN_CONFIG, 0, 1, 4, 3, 0, 0, 0, 0, 0, 0, ssHigh(1023), ssLow(1023));
		m = stream.readMessage();
		assertIsPinConfigMessage(m, "X.1", config);

		// Debounce interval
		config = new PinConfig();
		config.setDebounceMillis((long)Math.pow(2, 32) - 1);

		addMessage(MSG_PIN_CONFIG, 0, 1, 4, 0, 255, 255, 255, 255, 0, 0, ssHigh(1023), ssLow(1023));
		m = stream.readMessage();
		assertIsPinConfigMessage(m, "X.1", config);

		// Analog range
		config = new PinConfig();
		config.setAnalogMinValue(Short.MIN_VALUE);
		config.setAnalogMaxValue(Short.MAX_VALUE);

		addMessage(MSG_PIN_CONFIG, 0, 1, 4, 0, 0, 0, 0, 0, ssHigh(Short.MIN_VALUE), ssLow(Short.MIN_VALUE), ssHigh(Short.MAX_VALUE), ssLow(Short.MAX_VALUE));
		m = stream.readMessage();
		assertIsPinConfigMessage(m, "X.1", config);
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

	private void assertIsDigitalValueMessage(WaspMessage message, String subject, DigitalValueMessage.Value value) {
		assertEquals(DigitalValueMessage.class, message.getClass());

		DigitalValueMessage dvMessage = (DigitalValueMessage)message;
		assertEquals(subject, dvMessage.getSubject());
		assertEquals(value, dvMessage.getValue());
	}

	private void assertIsAnalogValueMessage(WaspMessage message, String subject, int value) {
		assertEquals(AnalogValueMessage.class, message.getClass());

		AnalogValueMessage avMessage = (AnalogValueMessage)message;
		assertEquals(subject, avMessage.getSubject());
		assertEquals(value, avMessage.getValue());
	}

	private void assertIsPinConfigMessage(WaspMessage message, String subject, PinConfig config) {
		assertEquals(PinConfigMessage.class, message.getClass());

		PinConfigMessage pcMessage = (PinConfigMessage)message;

		assertEquals(subject, pcMessage.getSubject());
		assertEquals(config, pcMessage.getConfig());
	}

	private void assertIsHeartbeatMessage(WaspMessage message, String subject) {
		assertEquals(HeartbeatMessage.class, message.getClass());
		assertEquals(subject, message.getSubject());
	}
}
