package com.aem.demo.core.services;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletResponse;

/**
 * 
 * @author debal
 * This interface is used to generate report in excel format
 */
public interface WriteDataToFile {
	
	public void addDataToFile(Map<Integer, String> data , SlingHttpServletResponse slingHttpServletResponse);
	
}
