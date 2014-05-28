package de.edling2.wasp;

import java.io.IOException;

public class CrcMissingException extends IOException {
	public CrcMissingException() {
		super("Message size too small to contain CRC");
	}
}
