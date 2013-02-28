package de.edling2.wasp.messages;

import de.edling2.nio.MultiSignByteBuffer;

public class AnalogValueMessage extends AbstractWaspMessage {
	private int pin;
	private int value;

	public AnalogValueMessage(int pin, int value) {
		this.pin = pin;
		this.value = value;
	}

	AnalogValueMessage(MultiSignByteBuffer bb) {
		super();
		parseBuffer(bb);
	}

	private void parseBuffer(MultiSignByteBuffer bb) {
		if (bb.limit() != 3) {
			throw new IllegalArgumentException(); // FIXME throw proper exception
		}

		pin = bb.getUnsigned();
		value = bb.getShort();
	}

	public int getPin() {
		return pin;
	}

	public int getValue() {
		return value;
	}
}
