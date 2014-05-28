package de.edling2.wasp;

import static de.edling2.wasp.WaspStream.*;
import static org.junit.Assert.*;

import java.io.EOFException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.edling2.wasp.BufferSizeException;
import de.edling2.wasp.CrcMismatchException;
import de.edling2.wasp.CrcMissingException;
import de.edling2.wasp.WaspInputStream;

public class WaspInputStreamTest {

	private TestInputStream inputStream;
	private WaspInputStream stream;
	private byte[] buffer;

	@Before
	public void setUp() {
		inputStream = new TestInputStream();
		stream = new WaspInputStream(inputStream);
		buffer = new byte[DEFAULT_BUFFER_SIZE];
	}

	@Test
	public void shouldEmptyReadMessage() throws Exception {
		addInput(SFLAG, 0xFF, 0xFF, EFLAG);
		expectMessage("");
		expectNoMoreMessages();
	}

	@Test
	public void shouldReadBasicMessage() throws Exception {
		addInput(SFLAG, 'A', 'B', 'C', 0x08, 0xF5, EFLAG);
		expectMessage("ABC");
		expectNoMoreMessages();
	}

	@Test
	public void shouldHandleEscapedBytes() throws Exception {
		addInput(SFLAG, ESC, SFLAG ^ ESC_XOR, 0x6E, 0x0A, EFLAG);
		expectMessage("[");
		expectNoMoreMessages();
	}

	@Test
	public void shouldHandleDuplicateSFLAGs() throws Exception {
		addInput(SFLAG, 'x', SFLAG, 'y', 0x4e, 0x0E, EFLAG, 'z', EFLAG);
		expectMessage("y");
		expectNoMoreMessages();
	}

	@Test
	public void shouldReadMultipleMessages() throws Exception {
		addInput(SFLAG, '1', 0x82, 0xC7, EFLAG, SFLAG, '2', 0xE1, 0xF7, EFLAG);
		expectMessage("1");
		expectMessage("2");
		expectNoMoreMessages();
	}

	@Test
	public void shouldIgnoreGarbageBetweenMessages() throws Exception {
		addInput(EFLAG, 'X', 0x00, SFLAG, '1', 0x82, 0xC7, EFLAG, 0x01, EFLAG, SFLAG, '2', 0xE1, 0xF7, EFLAG, ESC);
		expectMessage("1");
		expectMessage("2");
		expectNoMoreMessages();
	}

	@Test
	public void shouldThrowEOFExceptionOnEndOfStream() throws Exception {
		clearInput();
		expectException(new EOFException("End of stream"));
	}

	@Test
	public void shouldThrowCrcMismatchException() throws Exception {
		addInput(SFLAG, '1', 0x80, 0xC7, EFLAG);
		expectException(new CrcMismatchException(51074, 51072));
	}

	@Test
	public void shouldThrowCrcMissingException() throws Exception {
		addInput(SFLAG, 'x', EFLAG);
		expectException(new CrcMissingException());
	}

	@Test
	public void shouldThrowBufferSizeException() throws Exception {
		addInput(SFLAG, 'A', 'B', 'C', 0x08, 0xF5, EFLAG);
		useBuffer(new byte[4]);
		expectException(new BufferSizeException(4));
	}

	@Test
	public void shouldCloseUnderlyingStream() throws Exception {
		stream.close();
		assertTrue(inputStream.isClosed());
	}

	private void expectMessage(String s) throws Exception {
		expectMessage(s.getBytes());
	}

	private void expectMessage(byte... bytes) throws Exception {
		int byteCount = stream.readMessageIntoBuffer(buffer);
		byte[] output = Arrays.copyOf(buffer, byteCount);

		assertEquals(new String(bytes), new String(output));
	}

	private void expectNoMoreMessages() throws Exception {
		expectException(new EOFException("End of stream"));
	}

	private void expectException(Exception expected) throws Exception {
		try {
			stream.readMessageIntoBuffer(buffer);
			fail("Expected exception '" + expected + "' not thrown");
		} catch (Exception e) {
			assertEquals(expected.getClass(), e.getClass());
			assertEquals(expected.getMessage(), e.getMessage());
		}
	}

	private void addInput(int... bytes) {
		for (int b : bytes) {
			inputStream.write(b);
		}
	}

	private void clearInput() {
		inputStream.clear();
	}

	private void useBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
}
