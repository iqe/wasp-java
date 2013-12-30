package de.edling2.wasp.config;

import java.util.EnumSet;

public enum PinFlag {
	InputPullup(1),
	Reversed(2);

	public static EnumSet<PinFlag> fromBitField(int flags) {
		EnumSet<PinFlag> set = EnumSet.noneOf(PinFlag.class);

		for (PinFlag flag : PinFlag.values()) {
			if ((flags & flag.getValue()) != 0) {
				set.add(flag);
			}
		}

		return set;
	}

	public static int toBitField(EnumSet<PinFlag> set) {
		int flags = 0;

		for (PinFlag flag : set) {
			flags |= flag.getValue();
		}

		return flags;
	}

	private int value;

	private PinFlag(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
