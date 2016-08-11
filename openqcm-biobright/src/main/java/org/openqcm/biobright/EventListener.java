package org.openqcm.biobright;

import org.openqcm.core.event.OpenQCMEvent;
import org.openqcm.core.event.OpenQCMListener;

import static org.openqcm.biobright.PublishingInfo.Type.FREQUENCY;
import static org.openqcm.biobright.PublishingInfo.Type.TEMPERATURE;;

public class EventListener implements OpenQCMListener {
	
	private BiobrightClient biobrightClient;

	public EventListener(ConnectionInfo connectionInfo) {
		super();
		this.biobrightClient = new BiobrightClient(connectionInfo);
		this.biobrightClient.connect();
	}

	@Override
	public void incomingEvent(OpenQCMEvent event) {
        // call biobright
        if(isConnected()) {
        	long now = System.currentTimeMillis();
        	biobrightClient.publish(new PublishingInfo(FREQUENCY, now, String.format("%.1f", event.getValue().getFrequency()), "deviceIDFake1"));
        	biobrightClient.publish(new PublishingInfo(TEMPERATURE, now, String.format("%.1f", event.getValue().getTemperature()), "deviceIDFake1"));
        }
	}

	public boolean isConnected() {
		return biobrightClient.isConnected();
	}

	public void disconnect() {
		biobrightClient.disconnect();
	}
	
	

}
