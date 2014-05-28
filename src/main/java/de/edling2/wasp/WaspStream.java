package de.edling2.wasp;

public class WaspStream {
	public static final int SFLAG = 0x5B;
	public static final int EFLAG = 0x5D;
	public static final int ESC = 0x5C;
	public static final int ESC_XOR = 0x20;

	/**
	 * The CRC algorithm (CCITT) used for wasp messages as defined by pycrc
	 * and jacksum.
	 *
	 * @see http://www.tty1.net/pycrc
	 * @see http://www.jonelo.de/java/jacksum
	 */
	public static final String CRC_ALGORITHM = "crc:16,1021,FFFF,false,false,0"; // CCITT

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	private WaspStream() {
	}
}