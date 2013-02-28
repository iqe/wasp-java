package de.edling2.wasp.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestOutputStream extends OutputStream {

	private Queue<Integer> bytes;

	public TestOutputStream() {
		bytes = new LinkedBlockingQueue<Integer>();
	}

	public int read() {
		Integer b = bytes.poll();
		return b == null ? -1 : b;
	}

	public void clear() {
		bytes.clear();
	}

	@Override
	public void write(int b) throws IOException {
		bytes.add(b);
	}
}
