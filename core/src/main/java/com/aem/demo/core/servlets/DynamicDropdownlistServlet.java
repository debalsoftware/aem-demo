package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * This servlet is used to populate dropdown options dynamically via accessing component policy
 * using OOTB ContentPolicyManager interface
 */

@Component(service = Servlet.class, property = { "sling.servlet.resourceTypes=" + "demo/components/dynamic",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class DynamicDropdownlistServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 3276962670977184902L;
	
	private final Logger logger = LoggerFactory.getLogger(DynamicDropdownlistServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
		ValueMap valueMap;
		List<Resource> resourceList = new ArrayList<>();

		try {

			if (null == resourceResolver) {
				slingHttpServletResponse.sendError(500, "resource resolver is null");
				return;
			}

			Resource resource = slingHttpServletRequest.getRequestPathInfo().getSuffixResource();

			if (null == resource) {
				slingHttpServletResponse.sendError(404, "Component resource node not found");
				return;
			}

			ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);

			if (null == contentPolicyManager) {
				slingHttpServletResponse.sendError(404, "Component resource not found");
				return;
			}

			ContentPolicy contentPolicy = contentPolicyManager.getPolicy(resource);

			if (contentPolicy != null && contentPolicy.getProperties().containsKey("cities")) {
				String[] cities = contentPolicy.getProperties().get("cities", String[].class);
				if (ArrayUtils.isNotEmpty(cities)) {
					for (String city : cities) {
						valueMap = new ValueMapDecorator(new LinkedHashMap<>());
						String value = city;
						valueMap.put("value", value);
						valueMap.put("text", value);
						resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(),
								"nt:unstructured", valueMap));
					}
				}
			}

			DataSource dataSource = new SimpleDataSource(resourceList.iterator());
			slingHttpServletRequest.setAttribute(DataSource.class.getName(), dataSource);
		} catch (IOException e) {
			logger.error("Error occured:", e.getMessage());
		}
	}
}
