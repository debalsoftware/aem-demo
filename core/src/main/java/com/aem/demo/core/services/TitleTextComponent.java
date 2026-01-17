package com.aem.demo.core.services;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * 
 */
public interface TitleTextComponent extends ComponentExporter {

	public String getTitle();

	public String getDescription();

}
