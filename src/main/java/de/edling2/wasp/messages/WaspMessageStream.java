package de.edling2.wasp.messages;

import de.edling2.wasp.comm.WaspInputStream;
import de.edling2.wasp.comm.WaspOutputStream;
import java.io.Closeable;
import java.io.IOException;

public class WaspMessageStream implements Closeable {
	private WaspInputStream input;
	private WaspOutputStream output;
	private WaspMessageFactory messageFactory;
	private byte[] buffer;

	public WaspMessageStream(WaspInputStream input, WaspOutputStream output, String sourcePrefix) {
		this(input, new byte[1024], output, sourcePrefix);
	}

	public WaspMessageStream(WaspInputStream input, byte[] inputBuffer, WaspOutputStream output, String sourcePrefix) {
		this(input, inputBuffer, output, new WaspMessageFactory(sourcePrefix));
	}

	public WaspMessageStream(WaspInputStream input, byte[] inputBuffer, WaspOutputStream output, WaspMessageFactory messageFactory) {
		this.input = input;
		this.output = output;
		this.buffer = inputBuffer;
		this.messageFactory = messageFactory;
	}

	public String getSourcePrefix() {
		return messageFactory.getSourcePrefix();
	}

	public void setSourcePrefix(String sourcePrefix) {
		messageFactory.setSourcePrefix(sourcePrefix);
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
