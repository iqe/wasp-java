package de.edling2.wasp.messages;

import de.edling2.nio.MultiSignByteBuffer;

public class WaspMessageFactory {

	public static final int MSG_DIGITAL_IN = 0x01;
	public static final int MSG_ANALOG_IN  = 0x02;
	public static final int MSG_MOTOR_IN = 0x03;
	public static final int MSG_HEARTBEAT  = 0xFF;

	private String sourcePrefix;

	public WaspMessageFactory(String sourcePrefix) {
		this.sourcePrefix = sourcePrefix;
	}

	public WaspMessage buildMessage(byte[] buffer, int byteCount) {
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(buffer, 0, byteCount);

		int messageType = bb.getUnsigned();
		switch (messageType) {
			case MSG_DIGITAL_IN:
				return buildDigitalValueMessage(bb);
			case MSG_ANALOG_IN:
				return buildAnalogValueMessage(bb);
			case MSG_MOTOR_IN:
				return buildDigitalMotorMessage(bb);
			case MSG_HEARTBEAT:
				return buildHeartbeatMessage();
			default:
				throw new UnknownMessageException(messageType);
		}
	}

	private DigitalValueMessage buildDigitalValueMessage(MultiSignByteBuffer bb) {
		int pin = bb.getUnsignedShort();
		DigitalValueMessage.Value value = DigitalValueMessage.Value.parse((char)bb.get());

		return new DigitalValueMessage(buildSource(pin), value);
	}

	private AnalogValueMessage buildAnalogValueMessage(MultiSignByteBuffer bb) {
		int pin = bb.getUnsignedShort();
		int value = bb.getShort();

		return new AnalogValueMessage(buildSource(pin), value);
	}

	private WaspMessage buildDigitalMotorMessage(MultiSignByteBuffer bb) {
		int pin = bb.getUnsignedShort();
		DigitalMotorMessage.Direction direction = DigitalMotorMessage.Direction.parse((char)bb.getUnsigned());

		return new DigitalMotorMessage(buildSource(pin), direction);
	}

	private HeartbeatMessage buildHeartbeatMessage() {
		return new HeartbeatMessage(sourcePrefix);
	}

	private String buildSource(int pin) {
		return sourcePrefix + "." + pin;
	}

	public byte[] buildMessageBytes(WaspMessage message) {
		if (message instanceof DigitalValueMessage) {
			return buildDigitalValueMessageBytes((DigitalValueMessage)message);
		}
		if (message instanceof AnalogValueMessage) {
			return buildAnalogValueMessageBytes((AnalogValueMessage)message);
		}
		if (message instanceof DigitalMotorMessage) {
			return buildDigitalMotorMessageBytes((DigitalMotorMessage)message);
		}
		if (message instanceof HeartbeatMessage) {
			return buildHeartbeatMessageBytes((HeartbeatMessage)message);
		}

		throw new IllegalArgumentException("Don't know how to build bytes from message " + message);
	}

	private byte[] buildDigitalValueMessageBytes(DigitalValueMessage message) {
		byte[] bytes = new byte[3];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.put((byte)message.getValue().getChar());

		return bytes;
	}

	private byte[] buildAnalogValueMessageBytes(AnalogValueMessage message) {
		byte[] bytes = new byte[4];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.putShort((short)message.getValue());

		return bytes;
	}

	private byte[] buildDigitalMotorMessageBytes(DigitalMotorMessage message) {
		byte[] bytes = new byte[3];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.putUnsigned(message.getDirection().getValue());

		return bytes;
	}

	private byte[] buildHeartbeatMessageBytes(HeartbeatMessage message) {
		return new byte[] { (byte)MSG_HEARTBEAT };
	}

	private int parsePin(String source) {
		String s = source.substring(sourcePrefix.length() + 1);
		return Integer.parseInt(s);
	}
}
