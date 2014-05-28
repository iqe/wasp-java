package de.edling2.wasp;

import static de.edling2.wasp.WaspStream.*;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Low-level wrapper around an {@link InputStream}.
 *
 * This stream extracts message payloads from the underlying stream and
 * validates message checksums.
 */
public class WaspInputStream implements Closeable {
	private InputStream in;
	private Crc16Ccitt crc;

	public WaspInputStream(InputStream in) {
		this.in = in;
		crc = new Crc16Ccitt();
	}

	/**
	 * Reads the next message into the given {@code buffer}.
	 *
	 * @return the size of the message payload
	 * @throws IOException if the stream is closed before a message arrives
	 */
	public int readMessageIntoBuffer(byte[] buffer) throws IOException {
		int b, byteCount = 0;
		boolean inMessage = false, afterEsc = false;

		while ((b = in.read()) != -1) {
			checkBufferSize(buffer, byteCount);

			switch(b) {
			case SFLAG:
				inMessage = true;
				byteCount = 0;
				break;
			case EFLAG:
				if (inMessage) {
					checkLength(byteCount);
					checkCrc(buffer, byteCount);

					return byteCount - 2;
				}
				inMessage = false;
				break;
			case ESC:
				afterEsc = true;
				break;
			default:
				if (inMessage) {
					if (afterEsc) {
						b ^= ESC_XOR;
						afterEsc = false;
					}
					buffer[byteCount++] = (byte)b;
				}
			}
		}

		throw new EOFException("End of stream");
	}

	private void checkBufferSize(byte[] buffer, int byteCount) throws IOException {
		if (buffer.length <= byteCount) {
			throw new BufferSizeException(buffer.length);
		}
	}

	private void checkLength(int length) throws IOException {
		if (length < 2) {
			throw new CrcMissingException();
		}
	}

	private void checkCrc(byte[] buffer, int byteCount) throws IOException {
		crc.reset();
		crc.update(buffer, 0, byteCount - 2);

		int crcValue = readUnsignedShort(buffer, byteCount - 2);
		if (crc.getValue() != crcValue) {
			throw new CrcMismatchException(crc.getValue(), crcValue);
		}
	}

	private int readUnsignedShort(byte[] buffer, int offset) {
		byte low = buffer[offset];
		byte high = buffer[offset + 1];

		return ((high & 0xFF) << 8) | (low & 0xFF);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
