package com.aem.demo.core.servlets;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/referencePages",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReferencePageSearchServlet extends SlingAllMethodsServlet {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 817468999783619498L;
	private final Logger logger = LoggerFactory.getLogger(ReferencePageSearchServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String tagName = slingHttpServletRequest.getParameter("tagName");

		if (StringUtils.isNotBlank(tagName)) {
			
			
		}
	}



}
