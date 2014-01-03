package de.edling2.wasp.config;

import java.util.EnumSet;

public class PinConfig {
	private PinMode mode;
	private EnumSet<PinFlag> flags;

	private long debounceMillis;
	private int analogMinValue;
	private int analogMaxValue;

	public PinConfig() {
		this.mode = PinMode.DigitalOut;

		this.flags = EnumSet.noneOf(PinFlag.class);
		this.debounceMillis = 0;

		this.analogMinValue = 0;
		this.analogMaxValue = 1023;
	}

	public PinMode getMode() {
		return mode;
	}

	public void setMode(PinMode mode) {
		this.mode = mode;
	}

	public EnumSet<PinFlag> getFlags() {
		return flags;
	}

	public long getDebounceMillis() {
		return debounceMillis;
	}

	public void setDebounceMillis(long debounceMillis) {
		this.debounceMillis = debounceMillis;
	}

	public int getAnalogMinValue() {
		return analogMinValue;
	}

	public void setAnalogMinValue(int analogMinValue) {
		this.analogMinValue = analogMinValue;
	}

	public int getAnalogMaxValue() {
		return analogMaxValue;
	}

	public void setAnalogMaxValue(int analogMaxValue) {
		this.analogMaxValue = analogMaxValue;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.mode != null ? this.mode.hashCode() : 0);
		hash = 97 * hash + (this.flags != null ? this.flags.hashCode() : 0);
		hash = 97 * hash + (int) (this.debounceMillis ^ (this.debounceMillis >>> 32));
		hash = 97 * hash + this.analogMinValue;
		hash = 97 * hash + this.analogMaxValue;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PinConfig other = (PinConfig) obj;
		if (this.mode != other.mode) {
			return false;
		}
		if (this.flags != other.flags && (this.flags == null || !this.flags.equals(other.flags))) {
			return false;
		}
		if (this.debounceMillis != other.debounceMillis) {
			return false;
		}
		if (this.analogMinValue != other.analogMinValue) {
			return false;
		}
		if (this.analogMaxValue != other.analogMaxValue) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s, (debounce) interval %dms, range %d-%d, flags %s", mode, debounceMillis, analogMinValue, analogMaxValue, flags);
	}
}
