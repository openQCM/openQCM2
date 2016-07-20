package org.openqcm.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class Configuration {

	public static final String CONFIGURATION_FILE_NAME = "config.xml";
	public static final String DEFAULT_BIOBRIGHT_URL = "ssl://mqtt-openqcm.biobright.org:8883";
	public static final String DEFAULT_BIOBRIGHT_USERNAME = "openqcm-dev";
	public static final String DEFAULT_BIOBRIGHT_PASSWORD = "3KGhxc24585NpeTRSqDCV9CT";

	private String biobrightUrl = DEFAULT_BIOBRIGHT_URL;
	private String biobrightUserName = DEFAULT_BIOBRIGHT_USERNAME;
	private String biobrightPassword = DEFAULT_BIOBRIGHT_PASSWORD;

	@XmlElement(name = "biobrightUrl")
    public String getBiobrightUrl() {
		return biobrightUrl;
	}
	public void setBiobrightUrl(String biobrightUrl) {
		this.biobrightUrl = biobrightUrl;
	}
	@XmlElement(name = "biobrightUserName")
	public String getBiobrightUserName() {
		return biobrightUserName;
	}
	public void setBiobrightUserName(String biobrightUserName) {
		this.biobrightUserName = biobrightUserName;
	}
	@XmlElement(name = "biobrightPassword")
	public String getBiobrightPassword() {
		return biobrightPassword;
	}
	public void setBiobrightPassword(String biobrightPassword) {
		this.biobrightPassword = biobrightPassword;
	}
}
