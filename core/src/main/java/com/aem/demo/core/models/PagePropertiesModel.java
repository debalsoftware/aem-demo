package com.aem.demo.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;

@Model(adaptables = SlingHttpServletRequest.class)
public class PagePropertiesModel {

	
	private ValueMap componentPropertymap;

	
	private List<String> propertylist;
	private String namepropertyvalue;

	@SlingObject
	SlingHttpServletRequest slingHttpServletRequest;

	@PostConstruct
	protected void init() {

		String relativecomponentPath = slingHttpServletRequest.getResource().getResourceType();

		 propertylist = new ArrayList<String>();

		ComponentManager componentManager = slingHttpServletRequest.getResourceResolver()
				.adaptTo(ComponentManager.class);

		Component component = componentManager.getComponent(relativecomponentPath);
		Resource localResource = component.getLocalResource("cq:dialog");

		Resource childresource = localResource
				.getChild("content/items/tabs/items/socialmedia/items/column/items/section/items");
		
		for (Resource resource : childresource.getChildren()) {
			componentPropertymap = resource.getValueMap();
			namepropertyvalue = componentPropertymap.get("name", String.class);
			propertylist.add(namepropertyvalue);
			
		}

		

	}

	/**
	 * @return the propertylist
	 */
	public List<String> getPropertylist() {
		return propertylist;
	}

	

}
