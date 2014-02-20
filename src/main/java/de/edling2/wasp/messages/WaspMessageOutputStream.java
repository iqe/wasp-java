package de.edling2.wasp.messages;

import java.io.IOException;

import de.edling2.wasp.comm.WaspOutputStream;

public class WaspMessageOutputStream {
	private WaspOutputStream out;
	private WaspMessageFactory factory;

	public WaspMessageOutputStream(WaspOutputStream out, String subjectPrefix) {
		this(out, new WaspMessageFactory(subjectPrefix));
	}

	public WaspMessageOutputStream(WaspOutputStream out, WaspMessageFactory factory) {
		this.out = out;
		this.factory = factory;
	}

	public String getSubjectPrefix() {
		return factory.getSubjectPrefix();
	}

	public void setSubjectPrefix(String subjectPrefix) {
		factory.setSubjectPrefix(subjectPrefix);
	}

	public void writeMessage(WaspMessage message) throws IOException {
		byte[] bytes = factory.buildMessageBytes(message);
		out.writeMessage(bytes);
	}

	public boolean handlesMessage(WaspMessage message) {
		return factory.handlesMessage(message);
	}
}
