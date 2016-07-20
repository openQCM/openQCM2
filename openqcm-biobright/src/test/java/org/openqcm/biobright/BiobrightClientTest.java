package org.openqcm.biobright;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqcm.biobright.PublishingInfo.Type;

public class BiobrightClientTest {
	
	private ConnectionInfo info = new ConnectionInfo().setBiobrightUrl("ssl://mqtt-openqcm.biobright.org:8883")
			.setBiobrightUserName("openqcm-dev").setBiobrightPassword("3KGhxc24585NpeTRSqDCV9CT");
	
	@Test
	public void canConnect() {
		
		BiobrightClient client = new BiobrightClient(info);
		assertFalse(client.isConnected());

		client.connect();
		
		assertTrue(client.isConnected());

		client.disconnect();

		assertFalse(client.isConnected());
	}
	
	@Test
	public void canSend() {
		BiobrightClient client = new BiobrightClient(info);
		client.connect();
		
		assertTrue(client.isConnected());
		
		PublishingInfo publishingInfo = new PublishingInfo().setDeviceID("test-2431").setType(Type.FREQUENCY).setValue("100");
		
		client.publish(publishingInfo);
		
		
	}
}
