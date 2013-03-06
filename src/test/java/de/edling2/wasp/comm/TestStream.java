package de.edling2.wasp.comm;

import de.edling2.nio.MultiSignByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import static de.edling2.wasp.comm.WaspStream.*;

public class TestStream {
	private Queue<Byte> bytes;

	public final TestInputStream in;
	public final TestOutputStream out;

	public final WaspInputStream waspIn;
	public final WaspOutputStream waspOut;

	public TestStream() {
		bytes = new LinkedBlockingQueue<Byte>();
		in = new TestInputStream(bytes);
		out = new TestOutputStream(bytes);
		waspIn = new WaspInputStream(in);
		waspOut = new WaspOutputStream(out);
	}

	// FIXME All the helper methods below should be moved to other classes (but where?)

	public static byte[] buildMessage(int... bytes) {
		byte[] contentBytes = buildByteArray(bytes);
		byte[] crcBytes = crc(contentBytes);
		byte[] messageBytes = new byte[contentBytes.length + crcBytes.length + 2];

		messageBytes[0] = SFLAG;
		System.arraycopy(contentBytes, 0, messageBytes, 1, contentBytes.length);
		System.arraycopy(crcBytes, 0, messageBytes, contentBytes.length + 1, crcBytes.length);
		messageBytes[messageBytes.length - 1] = EFLAG;

		return messageBytes;
	}

	public static byte[] buildByteArray(int[] bytesAsInts) {
		byte[] bytes = new byte[bytesAsInts.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte)bytesAsInts[i];
		}
		return bytes;
	}

	public static int[] buildIntArray(byte[] bytes) {
		int[] bytesAsInts = new int[bytes.length];
		for (int i = 0; i < bytesAsInts.length; i++) {
			bytesAsInts[i] = bytes[i] & 0xFF;
		}
		return bytesAsInts;
	}

	public static byte[] crc(byte... bytes)
	{
		try {
			AbstractChecksum crc = JacksumAPI.getChecksumInstance(CRC_ALGORITHM);

			crc.reset();
			crc.update(bytes);

			byte low = (byte)crc.getValue();
			byte high = (byte)(crc.getValue() >>> 8);

			return new byte[] {high, low};
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String formatBytes(byte[] bytes, int byteCount) {
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < byteCount; i++) {
			b.append(toHex(bb.getUnsigned())).append(" ");
		}
		return b.toString();
	}

	/**
	 * Formats unsigned byte as hex value.
	 */
	public static String toHex(int b) {
		return String.format("0x%02X", b);
	}

	/**
	 * Returns high byte of unsigned short value.
	 */
	public static int usHigh(int value) {
		byte[] bytes = new byte[2];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);
		bb.putUnsignedShort(value);
		return bytes[0];
	}

	/**
	 * Returns low byte of unsigned short value.
	 */
	public static int usLow(int value) {
		byte[] bytes = new byte[2];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);
		bb.putUnsignedShort(value);
		return bytes[1];
	}

	/**
	 * Returns high byte of signed short value.
	 */
	public static int ssHigh(int value) {
		byte[] bytes = new byte[2];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);
		bb.putShort((short)value);
		bb.position(0);

		return bb.getUnsigned();
	}

	/**
	 * Returns low byte of signed short value.
	 */
	public static int ssLow(int value) {
		byte[] bytes = new byte[2];
		MultiSignByteBuffer bb = MultiSignByteBuffer.wrap(bytes);
		bb.putShort((short)value);
		bb.position(0);

		return bb.getUnsigned(1);
	}
}
