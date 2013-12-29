package de.edling2.wasp.messages;

import de.edling2.nio.MultiSignByteBuffer;
import de.edling2.wasp.config.PinConfig;

public class WaspMessageFactory {
	public static final int MSG_DIGITAL_IN = 0x01;
	public static final int MSG_ANALOG_IN  = 0x02;
	public static final int MSG_MOTOR_IN   = 0x03;
	public static final int MSG_PIN_CONFIG = 0x10;
	public static final int MSG_HEARTBEAT  = 0xFF;

	private String sourcePrefix;

	public WaspMessageFactory() {
		this.sourcePrefix = "W" + hashCode();
	}

	public WaspMessageFactory(String sourcePrefix) {
		this.sourcePrefix = sourcePrefix;
	}

	public String getSourcePrefix() {
		return sourcePrefix;
	}

	public void setSourcePrefix(String sourcePrefix) {
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
			case MSG_PIN_CONFIG:
				return buildPinConfigMessage(bb);
			case MSG_HEARTBEAT:
				return buildHeartbeatMessage(bb);
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

	private PinConfigMessage buildPinConfigMessage(MultiSignByteBuffer bb) {
		int pin = bb.getUnsignedShort();
		PinConfig config = new PinConfig();

		return new PinConfigMessage(buildSource(pin), config);
	}

	private HeartbeatMessage buildHeartbeatMessage(MultiSignByteBuffer bb) {
		int length = bb.getUnsigned();
		StringBuilder name = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			name.append((char)bb.get());
		}
		return new HeartbeatMessage(sourcePrefix, name.toString());
	}

	private String buildSource(int pin) {
		return sourcePrefix + "." + pin;
	}

	public boolean handlesMessage(WaspMessage message) {
		return message.getSource().matches(sourcePrefix + "\\.\\d+");
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
		byte[] bytes = new byte[4];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsigned(MSG_DIGITAL_IN);
		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.put((byte)message.getValue().getChar());

		return bytes;
	}

	private byte[] buildAnalogValueMessageBytes(AnalogValueMessage message) {
		byte[] bytes = new byte[5];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsigned(MSG_ANALOG_IN);
		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.putShort((short)message.getValue());

		return bytes;
	}

	private byte[] buildDigitalMotorMessageBytes(DigitalMotorMessage message) {
		byte[] bytes = new byte[4];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);

		bb.putUnsigned(MSG_MOTOR_IN);
		bb.putUnsignedShort(parsePin(message.getSource()));
		bb.putUnsigned(message.getDirection().getValue());

		return bytes;
	}

	private byte[] buildHeartbeatMessageBytes(HeartbeatMessage message) {
		byte[] nameBytes = message.getName().getBytes();
		byte[] messageBytes = new byte[nameBytes.length + 2];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(messageBytes);

		bb.putUnsigned(MSG_HEARTBEAT);
		bb.putUnsigned(nameBytes.length);
		System.arraycopy(nameBytes, 0, messageBytes, 2, nameBytes.length);

		return messageBytes;
	}

	private int parsePin(String source) {
		try {
			String s = source.substring(sourcePrefix.length() + 1);
			return Integer.parseInt(s);
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidSubjectException(source);
		} catch (NumberFormatException e) {
			throw new InvalidSubjectException(source);
		}
	}
}
