package de.edling2.wasp.messages;


public abstract class AbstractWaspMessage implements WaspMessage {
	private String subject;
	private long timestamp;

	public AbstractWaspMessage(String subject) {
		this(subject, System.currentTimeMillis());
	}

	public AbstractWaspMessage(String subject, long timestamp) {
		this.timestamp = timestamp;
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getSubject() + "'";
	}
}
