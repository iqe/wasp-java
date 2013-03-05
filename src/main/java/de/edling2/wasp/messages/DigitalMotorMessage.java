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

	private Direction direction;

	public DigitalMotorMessage(String source, Direction direction) {
		super(source);
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}
}

