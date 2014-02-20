package de.edling2.wasp.messages;

import de.edling2.wasp.comm.WaspInputStream;
import de.edling2.wasp.comm.WaspOutputStream;
import de.edling2.wasp.comm.WaspStream;
import java.io.Closeable;
import java.io.IOException;

/**
 * Convenience wrapper around {@link WaspInputStream} and {@link WaspOutputStream}.
 *
 * This is an alternative to using a separate {@link WaspMessageInputStream} and
 * {@link WaspMessageOutputStream}.
 */
public class WaspMessageStream implements Closeable {
	private WaspInputStream input;
	private WaspOutputStream output;
	private WaspMessageFactory messageFactory;
	private byte[] buffer;

	public WaspMessageStream(WaspInputStream input, WaspOutputStream output, String subjectPrefix) {
		this(input, new byte[WaspStream.DEFAULT_BUFFER_SIZE], output, subjectPrefix);
	}

	public WaspMessageStream(WaspInputStream input, byte[] inputBuffer, WaspOutputStream output, String subjectPrefix) {
		this(input, inputBuffer, output, new WaspMessageFactory(subjectPrefix));
	}

	public WaspMessageStream(WaspInputStream input, byte[] inputBuffer, WaspOutputStream output, WaspMessageFactory messageFactory) {
		this.input = input;
		this.output = output;
		this.buffer = inputBuffer;
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

	public void writeMessage(WaspMessage message) throws IOException {
		byte[] bytes = messageFactory.buildMessageBytes(message);
		output.writeMessage(bytes);
	}

	public boolean handlesMessage(WaspMessage message) {
		return messageFactory.handlesMessage(message);
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
	}
}
