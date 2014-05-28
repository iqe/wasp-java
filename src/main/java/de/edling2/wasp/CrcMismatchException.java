package de.edling2.wasp;

import java.io.IOException;

public class CrcMismatchException extends IOException {
	public CrcMismatchException(long expectedCrc, long actualCrc) {
		super("CRC mismatch (Expected " + expectedCrc + ", but was " + actualCrc + ")");
	}
}
