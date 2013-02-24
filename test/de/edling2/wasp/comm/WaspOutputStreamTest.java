package de.edling2.wasp.comm;

import static de.edling2.wasp.comm.WaspMessage.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WaspOutputStreamTest {

	TestOutputStream outputStream;
	WaspOutputStream stream;

	@Before
	public void setUp() {
		outputStream = new TestOutputStream();
		stream = new WaspOutputStream(outputStream);
	}

	@Test
	public void shouldWriteMessage() throws Exception {
		writeMessage("ABC");
		expectMessage(SFLAG, 'A', 'B', 'C', 0x08, 0xF5, EFLAG);
		expectNoMoreMessages();
	}

	@Test
	public void shouldEscapeControlBytesInMessage() throws Exception {
		writeMessage(SFLAG, EFLAG, ESC);
		expectMessage(SFLAG, ESC, SFLAG ^ ESC_XOR, ESC, EFLAG ^ ESC_XOR, ESC, ESC ^ ESC_XOR, 0x39, 0x81, EFLAG);
		expectNoMoreMessages();
	}

	private void writeMessage(String s) throws Exception {
		stream.writeMessage(s.getBytes());
	}

	private void writeMessage(int... bytes) throws Exception {
		byte[] b = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			b[i] = (byte) bytes[i];
		}
		stream.writeMessage(b);
	}

	private void expectMessage(int... bytes) throws Exception {
		byte[] expected = new byte[bytes.length];

		for (int i = 0; i < bytes.length; i++) {
			expected[i] = (byte)bytes[i];
		}

		expectMessage(expected);
	}

	private void expectMessage(byte... bytes) throws Exception {
		byte[] output = new byte[bytes.length];

		for (int i = 0; i < bytes.length; i++) {
			output[i] = (byte) outputStream.read();
		}

		assertEquals(new String(bytes), new String(output));
	}

	private void expectNoMoreMessages() throws Exception {
		assertTrue(outputStream.read() == -1);
	}
}
