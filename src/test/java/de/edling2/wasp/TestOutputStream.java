package de.edling2.wasp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestOutputStream extends OutputStream {
	private boolean closed;
	private Queue<Byte> bytes;

	public TestOutputStream() {
		this(new LinkedBlockingQueue<Byte>());
	}

	public TestOutputStream(Queue<Byte> bytes) {
		this.bytes = bytes;
	}

	public int read() {
		Byte b = bytes.poll();
		return b == null ? -1 : b & 0xFF;
	}

	public void clear() {
		bytes.clear();
	}

	@Override
	public void write(int b) throws IOException {
		bytes.add((byte)b);
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
