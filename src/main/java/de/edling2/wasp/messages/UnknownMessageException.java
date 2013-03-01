package de.edling2.wasp.messages;

public class UnknownMessageException extends RuntimeException {
	public UnknownMessageException(int messageType) {
		super("Message type " + toHex(messageType) + " is unknown");
	}

	private static String toHex(int b) {
		return String.format("0x%02X", b);
	}
}
