package de.edling2.wasp.messages;

import java.io.IOException;

import de.edling2.wasp.comm.WaspOutputStream;

public class WaspMessageOutputStream {
	private WaspOutputStream out;
	private WaspMessageFactory factory;

	public WaspMessageOutputStream(WaspOutputStream out) {
		this(out, new WaspMessageFactory());
	}

	public WaspMessageOutputStream(WaspOutputStream out, WaspMessageFactory factory) {
		this.out = out;
		this.factory = factory;
	}

	public void writeMessage(WaspMessage message) throws IOException {
		byte[] bytes = factory.buildMessageBytes(message);
		out.writeMessage(bytes);
	}
}
