package org.openqcm.core.event;

import org.openqcm.core.OpenQCMIncomingValue;

public class OpenQCMEvent {
	
	private OpenQCMIncomingValue value;

	public OpenQCMEvent(OpenQCMIncomingValue value) {
		super();
		this.value = value;
	}

	public OpenQCMIncomingValue getValue() {
		return value;
	}
}
