package com.aem.demo.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ChildPageModel {

	private final Logger logger = LoggerFactory.getLogger(ChildPageModel.class);

	@ScriptVariable
	Page currentPage;

	@SlingObject
	private ResourceResolver resourceResolver;

	private List<String> pagepathList;

	@PostConstruct
	protected void init() {

		String currentPagepath = currentPage.getPath();
		pagepathList = new ArrayList<String>();

		Resource resource = resourceResolver.getResource(currentPagepath);
		if (resource.hasChildren()) {

			Iterable<Resource> children = resource.getChildren();
			for (Resource childResource : children) {
				if (childResource.isResourceType("cq:Page")) {
					String childPagePath = childResource.getPath();
					logger.info(" **** Path **** {}", childPagePath);
					pagepathList.add(childPagePath);

				}

			}

		}

	}

	public List<String> getPagepathList() {
		return pagepathList;
	}

}
