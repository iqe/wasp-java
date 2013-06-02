package de.edling2.wasp.messages;

public class HeartbeatMessage extends AbstractWaspMessage {
	private String name;

	public HeartbeatMessage(String source, String name) {
		super(source);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
