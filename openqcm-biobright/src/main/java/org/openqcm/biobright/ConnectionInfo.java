package org.openqcm.biobright;

public class ConnectionInfo {

	private String biobrightUrl;
	private String biobrightUserName;
	private String biobrightPassword;
	
	public ConnectionInfo() {
	}

	public ConnectionInfo(String biobrightUrl, String biobrightUserName, String biobrightPassword) {
		this.biobrightUrl = biobrightUrl;
		this.biobrightUserName = biobrightUserName;
		this.biobrightPassword = biobrightPassword;
	}

	public String getBiobrightUrl() {
		return biobrightUrl;
	}

	public ConnectionInfo setBiobrightUrl(String biobrightUrl) {
		this.biobrightUrl = biobrightUrl;
		return this;
	}

	public String getBiobrightUserName() {
		return biobrightUserName;
	}

	public ConnectionInfo setBiobrightUserName(String biobrightUserName) {
		this.biobrightUserName = biobrightUserName;
		return this;
	}

	public String getBiobrightPassword() {
		return biobrightPassword;
	}

	public ConnectionInfo setBiobrightPassword(String biobrightPassword) {
		this.biobrightPassword = biobrightPassword;
		return this;
	}
}
