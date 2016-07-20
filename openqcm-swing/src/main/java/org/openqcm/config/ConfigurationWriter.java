package org.openqcm.config;

import java.io.File;

import org.ardulink.io.JAXBReaderWriter;
import org.ardulink.io.WritingException;

public class ConfigurationWriter {

	public static void write(Configuration configuration, String file) throws WritingException {
		write(configuration, new File(file));
	} 
	
	public static void write(Configuration configuration, File file) throws WritingException {
		JAXBReaderWriter<Configuration> writer = new JAXBReaderWriter<Configuration>(Configuration.class);
		writer.write(configuration, file);
	} 
}
