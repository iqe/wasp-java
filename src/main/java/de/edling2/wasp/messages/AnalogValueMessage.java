package de.edling2.wasp.messages;

public class AnalogValueMessage extends AbstractWaspMessage {
	private int value;

	public AnalogValueMessage(String source, int value) {
		super(source);
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
