package de.edling2.wasp.messages;

import de.edling2.nio.MultiSignByteBuffer;

public class DigitalValueMessage extends AbstractWaspMessage {
	public enum Value {
		High('H'),
		Low('L'),
		Toggle('T');

		public static Value parse(char c) {
			for (Value v : values()) {
				if (v.c == c) {
					return v;
				}
			}
			throw new IllegalArgumentException(); // FIXME throw proper exception
		}

		private char c;

		private Value(char c) {
			this.c = c;
		}
	}

	private int pin;
	private Value value;

	DigitalValueMessage(MultiSignByteBuffer bb) {
		super();
		parseBuffer(bb);
	}

	private void parseBuffer(MultiSignByteBuffer bb) {
		pin = bb.getUnsigned();
		value = Value.parse((char)bb.get());
	}

	public int getPin() {
		return pin;
	}

	public Value getValue() {
		return value;
	}
}
