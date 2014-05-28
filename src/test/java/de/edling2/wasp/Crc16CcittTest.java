package de.edling2.wasp;

import static org.junit.Assert.*;

import java.io.IOException;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import org.junit.Before;
import org.junit.Test;

public class Crc16CcittTest {
	private byte[] buffer;

	private AbstractChecksum jacksum;
	private Crc16Ccitt custom;

	@Before
	public void setUp() throws Exception {
		buffer = new byte[32];
		jacksum = JacksumAPI.getChecksumInstance("crc:16,1021,FFFF,false,false,0");
		custom = new Crc16Ccitt();
	}

	@Test
	public void shouldCalculateSameChecksumAsJacksum() throws Exception {
		compareCrcOfAsciiStrings(0, 4); // tests 26^4 permutations
	}

	private void compareCrcOfAsciiStrings(int length, int maxLength) throws IOException {
		long jacksumCrc;
		long customCrc;

		if (length >= maxLength) {
			return;
		}

		for (int i = 0; i < 26; i++) {
			buffer[length] = (byte)('a' + i);

			jacksumCrc = calculateJacksumCrc(buffer, length);
			customCrc = calculateCustomCrc(buffer, length);

			assertEquals(jacksumCrc, customCrc);

			compareCrcOfAsciiStrings(length + 1, maxLength);
		}
	}

	private long calculateJacksumCrc(byte[] buffer, int length) throws IOException {
		jacksum.reset();
		jacksum.update(buffer, 0, length);

		return jacksum.getValue();
	}

	private long calculateCustomCrc(byte[] buffer, int length) {
		custom.reset();
		custom.update(buffer, 0, length);

		return custom.getValue();
	}
}
