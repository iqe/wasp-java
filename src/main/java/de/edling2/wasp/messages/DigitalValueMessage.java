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

	private Value value;

	public DigitalValueMessage(String source, Value value) {
		super(source);
		this.value = value;
	}

	public Value getValue() {
		return value;
	}
}
