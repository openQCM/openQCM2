package org.openqcm.biobright;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.TrustStrategy;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.openqcm.biobright.PublishingInfo.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BiobrightClient {

    private static Logger logger = Logger.getLogger(BiobrightClient.class.getName());

    private static final String TEMPERATURE_TOPIC_PREFIX = "/openqcm/dev/temperature/";
    private static final String FREQUENCY_TOPIC_PREFIX = "/openqcm/dev/frequency/";

	private ConnectionInfo connectionInfo;
	private BlockingConnection connection;

	public BiobrightClient(ConnectionInfo connectionInfo) {
		super();
		this.connectionInfo = connectionInfo;
	}

	public void connect() {
		MQTT mqtt = new MQTT();
		
		try {
			mqtt.setHost(connectionInfo.getBiobrightUrl());
			mqtt.setUserName(connectionInfo.getBiobrightUserName());
			mqtt.setPassword(connectionInfo.getBiobrightPassword());
			
			// TODO change security policy that is actually disabled with this code.
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			        .loadTrustMaterial(null, acceptingTrustStrategy)
			        .build();
			
			mqtt.setSslContext(sslContext);

			logger.info("Opening MQTT socket.. ");
			connection = mqtt.blockingConnection();
			logger.info("Opened MQTT socket, connecting.. ");
			connection.connect();			
			logger.info("Connected MQTT socket.. ");
		} catch (Exception e) {
			logger.throwing(this.getClass().getName(), "connect()", e);
			if(connection != null) {
				connection = null;
			}
			throw new RuntimeException("Connection failed.", e);
		}
	}

	public boolean isConnected() {
		return (connection == null) ? false : connection.isConnected();
	}

	public void disconnect() {
		if(connection != null) {
			try {
				connection.disconnect();
			} catch (Exception e) {
				logger.throwing(this.getClass().getName(), "disconnect()", e);
				throw new RuntimeException("Disconnection failed.", e);
			}
		}
	}
	
	public void publish(PublishingInfo info) {
		
		String topic;
		if(info.getType() == Type.FREQUENCY) {
			topic = FREQUENCY_TOPIC_PREFIX + info.getDeviceID();
		} else if(info.getType() == Type.TEMPERATURE) {
			topic = TEMPERATURE_TOPIC_PREFIX + info.getDeviceID();
		} else {
			throw new RuntimeException("PublishingInfo hasn't a Type specified.");
		}
		
		byte[] payload;
		ObjectMapper mapper = new ObjectMapper(); // Jackson's JSON marshaller
		try {
			payload = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(info);
			logger.info("Publishing.. " + new String(payload));
		} catch (IOException e) {
			logger.throwing(this.getClass().getName(), "publish()", e);
			throw new RuntimeException("JSON transform failed.", e);
		}
		
		publish(topic, payload, QoS.AT_LEAST_ONCE, false);
		logger.info("published.");
	}

	private void publish(String topic, byte[] payload, QoS qos, boolean retain) {
		if(isConnected()) {
			try {
				connection.publish(topic, payload, qos, retain);
			} catch (Exception e) {
				logger.throwing(this.getClass().getName(), "publish()", e);
				throw new RuntimeException("Publish failed.", e);
			}
		} else {
			throw new RuntimeException("Not Connected.");
		}
	}
}
