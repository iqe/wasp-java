package io.iqe.wasp;

import static io.iqe.wasp.WaspStream.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Low-level wrapper around an {@link OutputStream}.
 *
 * This stream encloses a payload into the necessary wasp message bytes, escapes
 * illegal values and adds the message checksum.
 */
public class WaspOutputStream implements Closeable {
    private OutputStream out;
    private Crc16Ccitt crc;

    public WaspOutputStream(OutputStream out) {
        this.out = out;
        crc = new Crc16Ccitt();
    }

    public void writeMessage(byte[] bytes) throws IOException {
        writeMessage(bytes, 0, bytes.length);
    }

    public void writeMessage(byte[] bytes, int offset, int length) throws IOException {
        out.write(SFLAG);
        writeContent(bytes, offset, length);
        writeCrc(bytes, offset, length);
        out.write(EFLAG);
        out.flush();
    }

    private void writeCrc(byte[] bytes, int offset, int length) throws IOException {
        crc.reset();
        crc.update(bytes, offset, length);

        byte low = (byte) crc.getValue();
        byte high = (byte) (crc.getValue() >>> 8);

        writeContent(new byte[] { low, high }, 0, 2);
    }

    private void writeContent(byte[] bytes, int offset, int length) throws IOException {
        if (offset < 0 || length < 0 || offset + length > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid offset '%d' and length '%d' for byte array of length '%d'", offset, length, bytes.length));
        }

        byte b;
        for (int i = offset; i < offset + length; i++) {
            b = bytes[i];

            if (b == SFLAG || b == EFLAG || b == ESC) {
                out.write(ESC);
                out.write(b ^ ESC_XOR);
            } else {
                out.write(b);
            }
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
