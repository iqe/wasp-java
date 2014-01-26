package de.edling2.wasp.config;

import java.util.EnumSet;
import org.junit.Test;
import static org.junit.Assert.*;

public class PinFlagTest {
	@Test
	public void shouldSetCorrectFlags() {
		int flags = 0;

		flags = PinFlag.toBitField(EnumSet.noneOf(PinFlag.class));
		assertEquals(0, flags);

		flags = PinFlag.toBitField(EnumSet.of(PinFlag.InputPullup));
		assertEquals(1, flags);

		flags = PinFlag.toBitField(EnumSet.of(PinFlag.Reversed));
		assertEquals(2, flags);

		flags = PinFlag.toBitField(EnumSet.of(PinFlag.InputPullup, PinFlag.Reversed));
		assertEquals(3, flags);
	}

	@Test
	public void shouldReadFlagsCorrectly() {
		EnumSet<PinFlag> set;

		set = EnumSet.noneOf(PinFlag.class);
		assertEquals(set, PinFlag.fromBitField(0));

		set = EnumSet.of(PinFlag.InputPullup);
		assertEquals(set, PinFlag.fromBitField(1));

		set = EnumSet.of(PinFlag.Reversed);
		assertEquals(set, PinFlag.fromBitField(2));

		set = EnumSet.of(PinFlag.InputPullup, PinFlag.Reversed);
		assertEquals(set, PinFlag.fromBitField(3));
	}
}