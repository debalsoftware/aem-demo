/**
 * 
 */
package com.aem.demo.core.models;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.JcrUtility;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.drew.lang.annotations.Nullable;

/**
 * @author debal
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class PageModel {

	private final Logger logger = LoggerFactory.getLogger(PageModel.class);

	@OSGiService
	JcrUtility jcrUtility;

	@ScriptVariable
	private Page currentPage;

	private String pageStyle;

	@PostConstruct
	protected void init() {
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		String currentPagepath = currentPage.getPath();
		logger.info(" **** Current Page Path **** {}", currentPagepath);
		@Nullable
		Resource resource = resourceResolver.getResource(currentPagepath);
		if (Objects.nonNull(resource)) {
			ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
			ContentPolicy contentPolicy = contentPolicyManager.getPolicy(resource);
			pageStyle = contentPolicy.getPath();
			logger.info(" **** Page style **** {}", pageStyle);
		}
		jcrUtility.closeResourceResolver(resourceResolver);

	}

	/**
	 * @return the pageStyle
	 */
	public String getPageStyle() {
		return pageStyle;
	}

}
