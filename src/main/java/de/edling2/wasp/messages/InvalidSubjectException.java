package de.edling2.wasp.messages;

public class InvalidSubjectException extends RuntimeException {
	public InvalidSubjectException(String subject) {
		super("'" + subject + "' is no valid message subject");
	}
}