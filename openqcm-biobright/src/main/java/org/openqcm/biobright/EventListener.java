package org.openqcm.biobright;

import static org.openqcm.biobright.PublishingInfo.Type.FREQUENCY;
import static org.openqcm.biobright.PublishingInfo.Type.TEMPERATURE;

import org.openqcm.core.event.AbstractOQCMListener;
import org.openqcm.core.event.OpenQCMEvent;;

public class EventListener extends AbstractOQCMListener {
	
	private BiobrightClient biobrightClient;

	public EventListener(ConnectionInfo connectionInfo) {
		super();
		this.biobrightClient = new BiobrightClient(connectionInfo);
		this.biobrightClient.connect();
	}

	public boolean isConnected() {
		return biobrightClient.isConnected();
	}

	public void disconnect() {
		
		stopConsumerThread();
		biobrightClient.disconnect();
	}

	@Override
	public void consumeEvent(OpenQCMEvent event) {
        // call biobright
        if(isConnected()) {
        	long now = System.currentTimeMillis();
        	biobrightClient.publish(new PublishingInfo(FREQUENCY, now, String.format("%.1f", event.getValue().getFrequency()), event.getLinkID()));
        	biobrightClient.publish(new PublishingInfo(TEMPERATURE, now, String.format("%.1f", event.getValue().getTemperature()), event.getLinkID()));
        }
	}
	
	

}
