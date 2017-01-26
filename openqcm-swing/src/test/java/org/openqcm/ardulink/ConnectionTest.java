package org.openqcm.ardulink;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ardulink.core.Link;
import org.ardulink.core.events.CustomEvent;
import org.ardulink.core.events.CustomListener;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.events.RplyListener;
import org.ardulink.core.linkmanager.LinkManager;
import org.ardulink.core.linkmanager.LinkManager.ConfigAttribute;
import org.ardulink.core.linkmanager.LinkManager.Configurer;
import org.ardulink.util.URIs;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore
public class ConnectionTest {

	private Link link;
	
	@Test
	public void connectionListeningTest() throws IOException, InterruptedException {
		Configurer configurer = LinkManager.getInstance().getConfigurer(URIs.newURI("ardulink://serial-jssc/"));

		ConfigAttribute portAttribute = configurer.getAttribute("port");
		System.out.println(portAttribute.getChoiceValues()[0]);
		portAttribute.setValue(portAttribute.getChoiceValues()[0]);

		ConfigAttribute pingprobeAttribute = configurer.getAttribute("pingprobe");
		pingprobeAttribute.setValue(false);
		
		link = configurer.newLink();
		
		link.addRplyListener(new RplyListener() {
			
			@Override
			public void rplyReceived(RplyEvent e) {
				Map<String, Object> parameters = e.getParameters();
				for (String key : parameters.keySet()) {
					System.out.println(key + "=" + parameters.get(key));
				}
				
			}
		});
		
		link.addCustomListener(new CustomListener() {
			
			@Override
			public void customEventReceived(CustomEvent e) {
				System.out.println(e.getValue());
			}
		});

		TimeUnit.SECONDS.sleep(10);
		
		sendCustom();
		
		TimeUnit.SECONDS.sleep(10);
		
		link.close();
	}


	public void sendCustom() throws IOException {
		link.sendCustomMessage("getUniqueID", "XXX");
	}

}
