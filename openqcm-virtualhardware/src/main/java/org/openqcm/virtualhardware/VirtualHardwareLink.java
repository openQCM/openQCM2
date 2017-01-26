package org.openqcm.virtualhardware;

import static org.ardulink.core.proto.api.MessageIdHolders.addMessageId;

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
import org.ardulink.core.proto.api.MessageIdHolders;
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

	/*
	 * Message ID sequence for each request
	 */
	private long messageId = 0;
	
	/*
	 * Message ID for the current request message
	 */
	private Long requestUniqueID = null;
	
	/*
	 * suggested HARDWARE ID to be set on this virtual hardware
	 */
	private String uniqueIDSuggested = null;
	
	/*
	 * HARDWARE ID
	 */
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
	public long startListening(Pin pin) throws IOException {
		logger.info("{}", pin);
		return MessageIdHolders.NO_ID.getId();
	}


	@Override
	public long stopListening(Pin pin) throws IOException {
		logger.info("{}", pin);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long switchAnalogPin(AnalogPin analogPin, int value)
			throws IOException {
		logger.info("{} set to {}", analogPin, value);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long switchDigitalPin(DigitalPin digitalPin, boolean value)
			throws IOException {
		logger.info("{} set to {}", digitalPin, value);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long sendKeyPressEvent(char keychar, int keycode, int keylocation,
			int keymodifiers, int keymodifiersex) throws IOException {
		logger.info("key pressed ({} {} {} {} {})", keychar, keycode,
				keylocation, keymodifiers, keymodifiersex);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long sendTone(Tone tone) throws IOException {
		logger.info("tone {}", tone);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long sendNoTone(AnalogPin analogPin) throws IOException {
		logger.info("no tone on {}", analogPin);
		return MessageIdHolders.NO_ID.getId();
	}

	@Override
	public long sendCustomMessage(String... messages) throws IOException {
		logger.info("custom message {}", Arrays.asList(messages));
		
		if(messages != null && messages.length == 2 && messages[0].equals("getUniqueID")) {
			logger.info("custom message unique ID request");
			ToDeviceMessageCustom custom = addMessageIdIfNeeded(new DefaultToDeviceMessageCustom(messages));
			if(custom instanceof MessageIdHolder) {
				requestUniqueID = ((MessageIdHolder)custom).getId();
				uniqueIDSuggested = messages[1];
			}
		}
		
		if(requestUniqueID != null) {
			return requestUniqueID;
		}
		return MessageIdHolders.NO_ID.getId();
	}
	
	private <T> T addMessageIdIfNeeded(T event) {
		return hasRplyListeners() ? addMessageId(event, nextId()) : event;
	}

	private long nextId() {
		return ++messageId;
	}


}
