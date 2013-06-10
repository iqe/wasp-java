package de.edling2.wasp.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestInputStream extends InputStream {
	private boolean closed;
	private Queue<Byte> bytes;

	public TestInputStream() {
		this(new LinkedBlockingQueue<Byte>());
	}

	public TestInputStream(Queue<Byte> bytes) {
		this.bytes = bytes;
	}

	public void write(int b) {
		bytes.add((byte)b);
	}

	public void clear() {
		bytes.clear();
	}

	@Override
	public int read() throws IOException {
		Byte b = bytes.poll();
		return b == null ? -1 : b & 0xFF;
	}

	@Override
	public void close() throws IOException {
		super.close();
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}
}
