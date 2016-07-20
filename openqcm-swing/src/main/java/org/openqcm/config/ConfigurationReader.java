package org.openqcm.config;

import java.io.File;

import org.ardulink.io.JAXBReaderWriter;
import org.ardulink.io.ReadingException;

public class ConfigurationReader {

	public static Configuration read(String file) throws ReadingException {
		return read(new File(file));
	} 
	
	public static Configuration read(File file) throws ReadingException {
		JAXBReaderWriter<Configuration> reader = new JAXBReaderWriter<Configuration>(Configuration.class);
		return reader.read(file);
	} 
	
}
