package de.edling2.wasp.comm;

public class WaspMessage {
	public static final int SFLAG = 0x5B;
	public static final int EFLAG = 0x5D;
	public static final int ESC = 0x5C;
	public static final int ESC_XOR = 0x20;

	public static final String CRC_ALGORITHM = "crc:16,1021,FFFF,false,false,0"; // CCITT

	private byte[] bytes;

	public WaspMessage(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
