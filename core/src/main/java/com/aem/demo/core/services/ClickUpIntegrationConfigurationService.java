package com.aem.demo.core.services;

/**
 * This service is responsible to read ClickUp Configuration Details
 */
public interface ClickUpIntegrationConfigurationService {
	
	public String getclickUpapiToken();
	
	public String getclickUpApiEndPoint();
	
	public String[] getlistofAEMcontentPaths();
	
	public long getclickUpListID();
	
	public String getUserDetailsAPI();
	
}
