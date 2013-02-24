package de.edling2.wasp.comm;

import static de.edling2.wasp.comm.WaspMessage.*;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

public class WaspInputStream {
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

	public WaspMessage readMessage() throws IOException {
		int b;
		boolean inMessage = false, afterEsc = false;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		while ((b = in.read()) != -1) {

			switch(b) {
			case SFLAG:
				inMessage = true;
				buffer = new ByteArrayOutputStream();
				break;
			case EFLAG:
				if (inMessage) {
					byte[] message = buffer.toByteArray();

					checkLength(message);
					checkCrc(message);

					return new WaspMessage(Arrays.copyOfRange(message, 0, message.length - 2));
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
					buffer.write(b);
				}
			}
		}

		throw new EOFException("End of stream");
	}

	private void checkLength(byte[] message) throws IOException {
		if (message.length < 2) {
			throw new CrcMissingException();
		}
	}

	private void checkCrc(byte[] message) throws IOException {
		crc.reset();
		crc.update(message, 0, message.length - 2);

		byte low = message[message.length - 2];
		byte high = message[message.length - 1];
		int crcValue = ((high & 0xFF) << 8) | (low & 0xFF);

		if (crc.getValue() != crcValue) {
			throw new CrcMismatchException(crc.getValue(), crcValue);
		}

	}
}
