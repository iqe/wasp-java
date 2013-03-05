package de.edling2.wasp.messages;

public class DigitalMotorMessage extends AbstractWaspMessage {
	public enum Direction {
		Stop(0),
		Forward(1),
		Reverse(2);

		public static Direction parse(int i) {
			for (Direction v : values()) {
				if (v.value == i) {
					return v;
				}
			}
			throw new IllegalArgumentException(i + " is not a valid DigitalMotorMessage.Direction");
		}

		private int value;

		private Direction(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private int pin;
	private Direction direction;

	public DigitalMotorMessage(int pin, Direction direction) {
		super();
		this.pin = pin;
		this.direction = direction;
	}

	public int getPin() {
		return pin;
	}

	public Direction getDirection() {
		return direction;
	}
}

