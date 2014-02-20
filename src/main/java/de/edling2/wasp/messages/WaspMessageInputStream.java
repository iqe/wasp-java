package de.edling2.wasp.messages;

import java.io.IOException;

import de.edling2.wasp.comm.WaspInputStream;
import de.edling2.wasp.comm.WaspStream;

/**
 * Wrapper to read {@link WaspMessage}s from a {@link WaspInputStream}.
 */
public class WaspMessageInputStream {

	private WaspInputStream input;
	private WaspMessageFactory messageFactory;
	private byte[] buffer;

	/**
	 * Creates a new WaspMessageInputStream.
	 *
	 * An internal buffer is used for the messages which supports messages up to
	 * a size of {@link WaspStream#DEFAULT_BUFFER_SIZE} bytes.
	 *
	 * The given {@code subjectPrefix} will be used for all messages from this
	 * stream.
	 */
	public WaspMessageInputStream(WaspInputStream input, String subjectPrefix) {
		this(input, new byte[WaspStream.DEFAULT_BUFFER_SIZE], subjectPrefix);
	}

	/**
	 * Creates a new WaspMessageInputStream.
	 *
	 * Messages will be read into to the passed {@code buffer}. Make sure it is
	 * big enough for the messages you expect.
	 *
	 * The given {@code subjectPrefix} will be used for all messages from this
	 * stream.
	 */
	public WaspMessageInputStream(WaspInputStream input, byte[] buffer, String subjectPrefix) {
		this(input, buffer, new WaspMessageFactory(subjectPrefix));
	}

	/**
	 * Creates a new WaspMessageInputStream.
	 *
	 * Messages will be read into to the passed {@code buffer}. Make sure it is
	 * big enough for the messages you expect.
	 *
	 * Messages will be unmarshalled from the raw message bytes by the given
	 * {@code messageFactory}.
	 */
	public WaspMessageInputStream(WaspInputStream input, byte[] buffer, WaspMessageFactory messageFactory) {
		this.input = input;
		this.buffer = buffer;
		this.messageFactory = messageFactory;
	}

	/**
	 * Returns the subject prefix used for all messages from this stream.
	 */
	public String getSubjectPrefix() {
		return messageFactory.getSubjectPrefix();
	}

	/**
	 * Sets the subject prefix to use for all messages from this stream.
	 */
	public void setSubjectPrefix(String subjectPrefix) {
		messageFactory.setSubjectPrefix(subjectPrefix);
	}

	/**
	 * Reads the next message from the stream.
	 *
	 * This method blocks until a message is available.
	 */
	public WaspMessage readMessage() throws IOException {
		int length = input.readMessageIntoBuffer(buffer);
		return messageFactory.buildMessage(buffer, length);
	}
}
