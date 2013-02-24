package de.edling2.wasp.messages;


public abstract class AbstractWaspMessage implements WaspMessage {
	private long timestamp;

	public AbstractWaspMessage() {
		this(System.currentTimeMillis());
	}

	public AbstractWaspMessage(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
