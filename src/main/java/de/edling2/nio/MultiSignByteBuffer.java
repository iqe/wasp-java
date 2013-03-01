package de.edling2.nio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MultiSignByteBuffer {

	public static MultiSignByteBuffer allocate(int capacity) {
		return new MultiSignByteBuffer(ByteBuffer.allocate(capacity));
	}

	public static MultiSignByteBuffer allocateDirect(int capacity) {
		return new MultiSignByteBuffer(ByteBuffer.allocateDirect(capacity));
	}

	public static MultiSignByteBuffer wrap(byte[] array) {
		return new MultiSignByteBuffer(ByteBuffer.wrap(array));
	}

	public static MultiSignByteBuffer wrap(byte[] array, int offset, int length) {
		return new MultiSignByteBuffer(ByteBuffer.wrap(array, offset, length));
	}

	private ByteBuffer buf;

	private MultiSignByteBuffer(ByteBuffer buf) {
		this.buf = buf;
	}

	/* common methods */

	public boolean isDirect() {
		return buf.isDirect();
	}

	public MultiSignByteBuffer slice() {
		ByteBuffer b = buf.slice();
		return new MultiSignByteBuffer(b);
	}

	public boolean isReadOnly() {
		return buf.isReadOnly();
	}

	public int limit() {
		return buf.limit();
	}

	public MultiSignByteBuffer order(ByteOrder bo) {
		buf.order(bo);
		return this;
	}

	public MultiSignByteBuffer position(int newPosition) {
		buf.position(newPosition);
		return this;
	}

	/* byte */

	public byte get() {
		return buf.get();
	}

	public short getUnsigned() {
		return ((short) (buf.get() & 0xFF));
	}

	public byte get(int index) {
		return buf.get(index);
	}

	public short getUnsigned(int index) {
		return ((short) (buf.get(index) & (short) 0xFF));
	}

	public MultiSignByteBuffer put(byte b) {
		buf.put(b);
		return this;
	}

	public MultiSignByteBuffer put(int index, byte b) {
		buf.put(index, b);
		return this;
	}

	public MultiSignByteBuffer putUnsigned(int value) {
		buf.put((byte) (value & 0xFF));
		return this;
	}

	public MultiSignByteBuffer putUnsigned(int index, int value) {
		buf.put(index, (byte) (value & 0xFF));
		return this;
	}

	/* short */

	public short getShort() {
		return buf.getShort();
	}

	public short getShort(int index) {
		return buf.getShort(index);
	}

	public int getUnsignedShort() {
		return buf.getShort() & 0xFFFF;
	}

	public int getUnsignedShort(int index) {
		return buf.getShort(index) & 0xFFFF;
	}

	public MultiSignByteBuffer putShort(short value) {
		buf.putShort(value);
		return this;
	}

	public MultiSignByteBuffer putShort(int index, short value) {
		buf.putShort(index, value);
		return this;
	}

	public MultiSignByteBuffer putUnsignedShort(int value) {
		buf.putShort((short) (value & 0xFFFF));
		return this;
	}

	public MultiSignByteBuffer putUnsignedShort(int position, int value) {
		buf.putShort(position, (short) (value & 0xFFFF));
		return this;
	}

	/* int */

	public int getInt() {
		return buf.getInt();
	}

	public int getInt(int index) {
		return buf.getInt(index);
	}

	public long getUnsignedInt(MultiSignByteBuffer bb) {
		return ((long) bb.getInt() & 0xFFFFFFFFL);
	}

	public long getUnsignedInt(int index) {
		return ((long) buf.getInt(index) & 0xFFFFFFFFL);
	}

	public MultiSignByteBuffer putInt(int value) {
		buf.putInt(value);
		return this;
	}

	public MultiSignByteBuffer putInt(int index, int value) {
		buf.putInt(index, value);
		return this;
	}

	public MultiSignByteBuffer putUnsignedInt(long value) {
		buf.putInt((int) (value & 0xFFFFFFFFL));
		return this;
	}

	public MultiSignByteBuffer putUnsignedInt(int position, long value) {
		buf.putInt(position, (int) (value & 0xFFFFFFFFL));
		return this;
	}

	/* long */

	public long getLong() {
		return buf.getLong();
	}

	public long getLong(int index) {
		return buf.getLong(index);
	}

	public MultiSignByteBuffer putLong(long value) {
		buf.putLong(value);
		return this;
	}

	public MultiSignByteBuffer putLong(int index, long value) {
		buf.putLong(index, value);
		return this;
	}

	/* char */

	public char getChar() {
		return buf.getChar();
	}

	public char getChar(int index) {
		return buf.getChar(index);
	}

	public MultiSignByteBuffer putChar(char value) {
		buf.putChar(value);
		return this;
	}

	public MultiSignByteBuffer putChar(int index, char value) {
		buf.putChar(index, value);
		return this;
	}

	/* float */

	public float getFloat() {
		return buf.getFloat();
	}

	public float getFloat(int index) {
		return buf.getFloat(index);
	}

	public MultiSignByteBuffer putFloat(float value) {
		buf.putFloat(value);
		return this;
	}

	public MultiSignByteBuffer putFloat(int index, float value) {
		buf.putFloat(index, value);
		return this;
	}

	/* double */

	public double getDouble() {
		return buf.getDouble();
	}

	public double getDouble(int index) {
		return buf.getDouble(index);
	}

	public MultiSignByteBuffer putDouble(double value) {
		buf.putDouble(value);
		return this;
	}

	public MultiSignByteBuffer putDouble(int index, double value) {
		buf.putDouble(index, value);
		return this;
	}
}
