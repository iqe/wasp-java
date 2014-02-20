package de.edling2.wasp.messages;

public class HeartbeatMessage extends AbstractWaspMessage {
	private String name;

	public HeartbeatMessage(String subject, String name) {
		super(subject);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + getName();
	}
}
