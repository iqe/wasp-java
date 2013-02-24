package de.edling2.wasp.messages;

import de.edling2.nio.MultiSignByteBuffer;

public class WaspMessageFactory {

	public static final int MSG_DIGITAL_IN = 0x01;
	public static final int MSG_ANALOG_IN  = 0x02;
	public static final int MSG_HEARTBEAT  = 0xFF;

	public WaspMessage buildMessage(byte[] buffer, int byteCount) {
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(buffer, 0, byteCount);
		int command = bb.getUnsigned();

		switch (command) {
			case MSG_DIGITAL_IN:
				return new DigitalValueMessage(bb);
			case MSG_ANALOG_IN:
				return new AnalogValueMessage(bb);
			case MSG_HEARTBEAT:
				return new HeartbeatMessage();
			default:
				throw new IllegalArgumentException("Invalid command '" + command + "' in input buffer");
		}
	}
}
