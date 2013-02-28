package de.edling2.wasp.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestInputStream extends InputStream {

	private Queue<Integer> bytes;

	public TestInputStream() {
		bytes = new LinkedBlockingQueue<Integer>();
	}

	public void write(int b) {
		bytes.add(b);
	}

	public void clear() {
		bytes.clear();
	}

	@Override
	public int read() throws IOException {
		Integer b = bytes.poll();
		return b == null ? -1 : b;
	}
}
