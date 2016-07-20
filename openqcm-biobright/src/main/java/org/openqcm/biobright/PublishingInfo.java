package org.openqcm.biobright;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublishingInfo {
	
	public enum Type {
		
		TEMPERATURE("openqcm-temperature"), FREQUENCY("openqcm-frequency");
		
		private String readingId;
		
		private Type(String reading_id) {
			this.readingId = reading_id;
		}
		
		public String getReadingId() {
			return readingId;
		}

		@Override
		public String toString() {
			return getReadingId();
		}
		
	}
	
	@JsonProperty("reading_id")
	private Type type;

	@JsonProperty("x")
	private long timestamp = System.currentTimeMillis();

	@JsonProperty("y")
	private String value;

	@JsonProperty("source")
	private String deviceID;
	
	public PublishingInfo() {
		super();
	}

	public PublishingInfo(Type type, long timestamp, String value, String deviceID) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.value = value;
		this.deviceID = deviceID;
	}

	public Type getType() {
		return type;
	}
	public PublishingInfo setType(Type type) {
		this.type = type;
		return this;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public PublishingInfo setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	public String getValue() {
		return value;
	}
	public PublishingInfo setValue(String value) {
		this.value = value;
		return this;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public PublishingInfo setDeviceID(String deviceID) {
		this.deviceID = deviceID;
		return this;
	}
}
