package de.edling2.wasp.messages;

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
			throw new IllegalArgumentException("'" + c + "' is not a valid DigitalValueMessage.Value");
		}

		private char c;

		private Value(char c) {
			this.c = c;
		}

		public char getChar() {
			return c;
		}
	}

	private int pin;
	private Value value;

	public DigitalValueMessage(int pin, Value value) {
		super();
		this.pin = pin;
		this.value = value;
	}

	public int getPin() {
		return pin;
	}

	public Value getValue() {
		return value;
	}
}
