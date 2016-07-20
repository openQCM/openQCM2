package org.openqcm.virtualhardware;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ardulink.core.AbstractListenerLink;
import org.ardulink.core.Pin;
import org.ardulink.core.Pin.AnalogPin;
import org.ardulink.core.Pin.DigitalPin;
import org.ardulink.core.Tone;
import org.ardulink.core.events.DefaultCustomEvent;
import org.ardulink.core.events.DefaultRplyEvent;
import org.ardulink.core.linkmanager.LinkConfig;
import org.ardulink.core.messages.api.ToDeviceMessageCustom;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageCustom;
import org.ardulink.core.proto.api.MessageIdHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualHardwareLink extends AbstractListenerLink {

	private final Logger logger = LoggerFactory.getLogger(VirtualHardwareLink.class);

	private final Thread thread = new Thread() {

		{
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			while (true) {
				
				manageValuesToBeReturned();
				
				manageUniqueIDResponse();
				
				try {
					TimeUnit.MILLISECONDS.sleep(250);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		private void manageValuesToBeReturned() {
			degree += 1;
			degree = degree % 360;
			
			double freq = Math.sin(Math.toRadians(degree)) * 1000;
			double temp = Math.cos(Math.toRadians(degree)) * 1000;

			String value = "RAWMONITOR" + (int)freq + "_" + (int)temp;
			
			fireCustomReceived(new DefaultCustomEvent(value));
		}

		private void manageUniqueIDResponse() {
			// requested device ID see DeviceID class
			if(requestUniqueID != null) {
				if(uniqueID == null) {
					uniqueID = uniqueIDSuggested;
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("UniqueID", uniqueID);
				fireReplyReceived(new DefaultRplyEvent(true, requestUniqueID, parameters));
				requestUniqueID = null;
				uniqueIDSuggested = null;
			}
		}

	};

	private Long requestUniqueID = null;
	private String uniqueIDSuggested = null;
	private String uniqueID = null;
	
	private int degree = 0;

	public VirtualHardwareLink(LinkConfig config) {
		super();
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.thread.interrupt();
	}

	@Override
	public void startListening(Pin pin) throws IOException {
		logger.info("{}", pin);
	}


	@Override
	public void stopListening(Pin pin) throws IOException {
		logger.info("{}", pin);
	}

	@Override
	public void switchAnalogPin(AnalogPin analogPin, int value)
			throws IOException {
		logger.info("{} set to {}", analogPin, value);
	}

	@Override
	public void switchDigitalPin(DigitalPin digitalPin, boolean value)
			throws IOException {
		logger.info("{} set to {}", digitalPin, value);
	}

	@Override
	public void sendKeyPressEvent(char keychar, int keycode, int keylocation,
			int keymodifiers, int keymodifiersex) throws IOException {
		logger.info("key pressed ({} {} {} {} {})", keychar, keycode,
				keylocation, keymodifiers, keymodifiersex);
	}

	@Override
	public void sendTone(Tone tone) throws IOException {
		logger.info("tone {}", tone);
	}

	@Override
	public void sendNoTone(AnalogPin analogPin) throws IOException {
		logger.info("no tone on {}", analogPin);
	}

	@Override
	public void sendCustomMessage(String... messages) throws IOException {
		logger.info("custom message {}", Arrays.asList(messages));
		
		// If it's a request for get device Unique ID then (see DeviceID class)
		if(messages != null && messages.length == 2 && messages[0].equals("getUniqueID")) {
			logger.info("custom message unique ID request");
			ToDeviceMessageCustom custom = addMessageIdIfNeeded(new DefaultToDeviceMessageCustom(messages));
			if(custom instanceof MessageIdHolder) {
				requestUniqueID = ((MessageIdHolder)custom).getId();
				uniqueIDSuggested = messages[1];
			}
		}
	}

}
