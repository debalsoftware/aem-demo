package com.aem.demo.core.models;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


@Model(adaptables = Resource.class , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PagePropertydetailModel {

	@ValueMapValue
	private String pagepath;
	
	@SlingObject
    private ResourceResolver resourceResolver;
	
	private Map<String,Object> pageProperties;
	
	@PostConstruct
	protected void init() {
		
		
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = pageManager.getPage(pagepath);
		pageProperties = page.getProperties();
		
		
		
		
		
	}

	public Map<String, Object> getPageProperties() {
		return pageProperties;
	}
}
