package de.edling2.wasp.messages;

import java.io.IOException;

import de.edling2.wasp.comm.WaspOutputStream;
import java.io.OutputStream;

/**
 * Wraps an {@link OutputStream} to write {@link WaspMessage}s.
 */
public class WaspMessageOutputStream {
	private WaspOutputStream out;
	private WaspMessageFactory factory;

	/**
	 * Create a new WaspMessageOutputStream.
	 *
	 * Only messages with the given subject prefix will be accepted by
	 * {@link #writeMessage(de.edling2.wasp.messages.WaspMessage)}.
	 */
	public WaspMessageOutputStream(WaspOutputStream out, String subjectPrefix) {
		this(out, new WaspMessageFactory(subjectPrefix));
	}

	/**
	 * Create a new WaspMessageOutputStream.
	 *
	 * Messages will be marshalled by the given {@code factory}.
	 */
	public WaspMessageOutputStream(WaspOutputStream out, WaspMessageFactory factory) {
		this.out = out;
		this.factory = factory;
	}

	/**
	 * Returns the required subject prefix for messages written to this stream.
	 */
	public String getSubjectPrefix() {
		return factory.getSubjectPrefix();
	}

	/**
	 * Sets the required subject prefix for messages written to this stream.
	 */
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
