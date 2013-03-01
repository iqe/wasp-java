package de.edling2.wasp.messages;

public class AnalogValueMessage extends AbstractWaspMessage {
	private int pin;
	private int value;

	public AnalogValueMessage(int pin, int value) {
		this.pin = pin;
		this.value = value;
	}

	public int getPin() {
		return pin;
	}

	public int getValue() {
		return value;
	}
}
