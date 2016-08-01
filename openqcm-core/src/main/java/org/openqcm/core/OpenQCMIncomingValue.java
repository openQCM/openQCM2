package org.openqcm.core;

public class OpenQCMIncomingValue {
	
	private double temperature;
	private double frequency;
	
	public OpenQCMIncomingValue() {
		super();
	}

	public OpenQCMIncomingValue(double temperature, double frequency) {
		super();
		this.temperature = temperature;
		this.frequency = frequency;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public OpenQCMIncomingValue setTemperature(double temperature) {
		this.temperature = temperature;
		return this;
	}
	public double getFrequency() {
		return frequency;
	}
	public OpenQCMIncomingValue setFrequency(double frequency) {
		this.frequency = frequency;
		return this;
	}
}
