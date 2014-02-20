package de.edling2.wasp.comm;

import static de.edling2.wasp.comm.WaspStream.*;
import java.io.Closeable;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

public class WaspInputStream implements Closeable {
	private InputStream in;
	private AbstractChecksum crc;

	public WaspInputStream(InputStream in) {
		this.in = in;

		try {
			crc = JacksumAPI.getChecksumInstance(CRC_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] readMessage() throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int byteCount = readMessageIntoBuffer(buffer);

		return Arrays.copyOf(buffer, byteCount);
	}

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
