package de.edling2.wasp.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArduinoConfig {
	private String name;
	private long heartbeatMillis;
	private Map<Integer, PinConfig> pinConfigs;

	public ArduinoConfig(String name) {
		this.name = name;
		heartbeatMillis = 5000;
		pinConfigs = new HashMap<Integer, PinConfig>();
	}

	public String getName() {
		return name;
	}

	public long getHeartbeatMillis() {
		return heartbeatMillis;
	}

	public void setHeartbeatMillis(long heartbeatMillis) {
		this.heartbeatMillis = heartbeatMillis;
	}

	public PinConfig getPinConfig(int pin) {
		return pinConfigs.get(pin);
	}

	public void setPinConfig(int pin, PinConfig config) {
		pinConfigs.put(pin, config);
	}

	public Map<Integer, PinConfig> getPinConfigs() {
		return Collections.unmodifiableMap(pinConfigs);
	}
}
