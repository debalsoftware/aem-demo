package com.aem.demo.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class)
public class RequestHeaderModel {
	
	@SlingObject
	SlingHttpServletRequest slingHttpServletRequest;

	
	private String deviceinfo;
	
	protected void postconstruct() {
		
		String userAgent = slingHttpServletRequest.getHeader("User-Agent");
		
		if (userAgent.contains("Mozilla")) {
			deviceinfo = "mozilla";
	    } else if(userAgent.contains("AppleWebKit")){
	    	deviceinfo = "apple";
	    } 
		
	}

	/**
	 * @return the deviceinfo
	 */
	public String getDeviceinfo() {
		return deviceinfo;
	}
	
}