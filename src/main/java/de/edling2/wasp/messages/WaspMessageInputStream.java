package de.edling2.wasp.messages;

import java.io.IOException;

import de.edling2.wasp.comm.WaspInputStream;

public class WaspMessageInputStream {
	private WaspInputStream input;
	private WaspMessageFactory messageFactory;
	private byte[] buffer;

	public WaspMessageInputStream(WaspInputStream input, String subjectPrefix) {
		this(input, new byte[1024], subjectPrefix);
	}

	public WaspMessageInputStream(WaspInputStream input, byte[] buffer, String subjectPrefix) {
		this(input, buffer, new WaspMessageFactory(subjectPrefix));
	}

	public WaspMessageInputStream(WaspInputStream input, byte[] buffer, WaspMessageFactory messageFactory) {
		this.input = input;
		this.buffer = buffer;
		this.messageFactory = messageFactory;
	}

	public String getSubjectPrefix() {
		return messageFactory.getSubjectPrefix();
	}

	public void setSubjectPrefix(String subjectPrefix) {
		messageFactory.setSubjectPrefix(subjectPrefix);
	}

	public WaspMessage readMessage() throws IOException {
		int length = input.readMessageIntoBuffer(buffer);
		return messageFactory.buildMessage(buffer, length);
	}
}
