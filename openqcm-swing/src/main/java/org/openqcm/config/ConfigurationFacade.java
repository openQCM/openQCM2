package org.openqcm.config;

import java.util.logging.Logger;

import org.ardulink.io.ReadingException;
import org.ardulink.io.WritingException;

public class ConfigurationFacade {

	private static Logger logger = Logger.getLogger(ConfigurationFacade.class.getName());
	private static Configuration configuration = null;
	
	static {
		try {
			loadConfiguration();
		} catch (ReadingException e) { // maybe config file doesn't exist I'll write it with default values
			try {
				configuration = new Configuration(); // Default
				saveConfiguration();
			} catch (WritingException e1) {
				e1.printStackTrace();
				logger.severe("CONFIGURATION ERROR. APPLICATION IS NOT ABLE TO CONFIGURE ITSELF. EXITING!");
				System.exit(-1);
			}
		}
	}
	
	public static Configuration getConfiguration() {
		return configuration;
	}
	
	public static void saveConfiguration() throws WritingException {
		ConfigurationWriter.write(configuration, Configuration.CONFIGURATION_FILE_NAME);
	}
	
	public static Configuration loadConfiguration() throws ReadingException {
		configuration = ConfigurationReader.read(Configuration.CONFIGURATION_FILE_NAME);
		return configuration;
	}
	
}
