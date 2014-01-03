package de.edling2.wasp.messages;

import de.edling2.wasp.config.PinConfig;

public class PinConfigMessage extends AbstractWaspMessage {
	private PinConfig config;

	public PinConfigMessage(String source, PinConfig config) {
		super(source);
		this.config = config;
	}

	public PinConfig getConfig() {
		return config;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + config.toString();
	}
}
