
package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Servlet;

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

import com.day.cq.wcm.api.Page;

@Component(service = Servlet.class, property = { "sling.servlet.resourceTypes=" + "demo/components/page",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReadPageInformationServlet extends SlingAllMethodsServlet {

	private final Logger logger = LoggerFactory.getLogger(ReadPageInformationServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String requestURI = slingHttpServletRequest.getRequestURI();

		String currentpagePath = requestURI.substring(0, requestURI.lastIndexOf("/"));

		ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

		Resource resource = resourceResolver.getResource(currentpagePath);
		if (Objects.nonNull(resource) && resource.isResourceType("cq:Page")) {

			Page page = resource.adaptTo(Page.class);
			ValueMap pageproperties = page.getProperties();
			try {
				slingHttpServletResponse.getWriter().println(pageproperties);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
