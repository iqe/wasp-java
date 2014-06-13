package io.iqe.wasp;

public final class WaspStream {
    static final int SFLAG = 0x5B;
    static final int EFLAG = 0x5D;
    static final int ESC = 0x5C;
    static final int ESC_XOR = 0x20;

    /**
     * Bytes required for CRC in all Wasp messages.
     */
    public static final int CRC_SIZE = 2;

    private WaspStream() {
    }
}
