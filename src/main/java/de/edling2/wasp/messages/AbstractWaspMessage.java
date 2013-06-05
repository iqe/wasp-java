package de.edling2.wasp.messages;


public abstract class AbstractWaspMessage implements WaspMessage {
	private String source;
	private long timestamp;

	public AbstractWaspMessage(String source) {
		this(source, System.currentTimeMillis());
	}

	public AbstractWaspMessage(String source, long timestamp) {
		this.timestamp = timestamp;
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getSource() + "'";
	}
}
