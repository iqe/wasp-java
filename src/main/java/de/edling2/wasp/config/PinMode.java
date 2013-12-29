package de.edling2.wasp.config;

public enum PinMode {
	DigitalInSwitch(1),
	DigitalInButtonPress(2),
	DigitalInButtonRelease(3),
	DigitalOut(4),
	AnalogInChangeBased(5),
	AnalogInTimeBased(6),
	AnalogOut(7);

	private int value;

	private PinMode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
