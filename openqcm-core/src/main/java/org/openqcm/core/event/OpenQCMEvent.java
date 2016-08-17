package org.openqcm.core.event;

import org.openqcm.core.OpenQCMIncomingValue;

public class OpenQCMEvent {
	
	private OpenQCMIncomingValue value;
	private String linkID;

	public OpenQCMEvent(OpenQCMIncomingValue value, String linkID) {
		super();
		this.value = value;
		this.linkID = linkID;
	}

	public OpenQCMIncomingValue getValue() {
		return value;
	}

	public String getLinkID() {
		return linkID;
	}
}
