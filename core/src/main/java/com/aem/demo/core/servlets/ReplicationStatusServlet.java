package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/status",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReplicationStatusServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 817468999783619498L;
	private final Logger logger = LoggerFactory.getLogger(ReplicationStatusServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {

		String pagePath = slingHttpServletRequest.getParameter("pagePath");

		if (StringUtils.isNotBlank(pagePath)) {
			ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

			Resource resource = resourceResolver.getResource(pagePath);
			if (resource.isResourceType("cq:Page")) {
				Resource resource2 = resource.getChild("jcr:content");
				ValueMap valueMap = resource2.adaptTo(ValueMap.class);
				if (valueMap.containsKey("cq:lastReplicationAction")) {
					String replicationstatus = valueMap.get("cq:lastReplicationAction", String.class);
					slingHttpServletResponse.getWriter().write(replicationstatus);

				}

			}

			else {
				logger.info("**** Resource isn't a page**** {} ");
			}
		}
	}

}
