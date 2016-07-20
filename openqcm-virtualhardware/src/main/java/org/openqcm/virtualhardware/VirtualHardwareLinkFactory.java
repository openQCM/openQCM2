package org.openqcm.virtualhardware;

import org.ardulink.core.Link;
import org.ardulink.core.linkmanager.LinkConfig;
import org.ardulink.core.linkmanager.LinkFactory;

public class VirtualHardwareLinkFactory implements LinkFactory<LinkConfig> {

	@Override
	public String getName() {
		return "openQCMVirtualHardware";
	}

	@Override
	public Link newLink(LinkConfig config) throws Exception {
		return new VirtualHardwareLink(config);
	}

	@Override
	public LinkConfig newLinkConfig() {
		return LinkConfig.NO_ATTRIBUTES;
	}

}
