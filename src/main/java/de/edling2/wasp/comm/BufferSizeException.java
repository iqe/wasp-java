package de.edling2.wasp.comm;

import java.io.IOException;

public class BufferSizeException extends IOException {
	public BufferSizeException(int bufferSize) {
		super("Message too big for buffer of size " + bufferSize + " bytes");
	}
}
