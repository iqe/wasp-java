package de.edling2.wasp.config;

public enum PinMode {
	DigitalInSwitch(1),
	DigitalInButtonPress(2),
	DigitalInButtonRelease(3),
	DigitalOut(4),
	AnalogInChangeBased(5),
	AnalogInTimeBased(6),
	AnalogOut(7);

	public static PinMode fromValue(int value) {
		for (PinMode mode : values()) {
			if (mode.getValue() == value) {
				return mode;
			}
		}
		throw new IllegalArgumentException("'" + value + "' is not a valid PinMode value");
	}

	private int value;

	private PinMode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
