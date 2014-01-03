package de.edling2.wasp.comm;

import static de.edling2.wasp.comm.WaspStream.*;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

public class WaspOutputStream {
	private OutputStream out;
	private AbstractChecksum crc;

	public WaspOutputStream(OutputStream out) {
		this.out = out;

		try {
			crc = JacksumAPI.getChecksumInstance(CRC_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeMessage(byte[] bytes) throws IOException {
		out.write(SFLAG);
		writeContent(bytes);
		writeCrc(bytes);
		out.write(EFLAG);
		out.flush();
	}

	private void writeCrc(byte[] bytes) throws IOException {
		crc.reset();
		crc.update(bytes);

		byte low = (byte)crc.getValue();
		byte high = (byte)(crc.getValue() >>> 8);

		writeContent(new byte[] {low, high});
	}

	private void writeContent(byte[] bytes) throws IOException {
		byte b;
		for (int i = 0; i < bytes.length; i++) {
			b = bytes[i];

			if (b == SFLAG || b == EFLAG || b == ESC) {
				out.write(ESC);
				out.write(b ^ ESC_XOR);
			} else {
				out.write(b);
			}
		}
	}

	public void close() throws IOException {
		out.close();
	}
}
