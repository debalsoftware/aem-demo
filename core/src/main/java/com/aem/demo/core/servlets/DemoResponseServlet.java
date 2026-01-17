package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Servlet to call API Url",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "weretail/components/structure/page", "sling.servlet.extensions=" + "txt" })
public class DemoResponseServlet extends SlingAllMethodsServlet {

	private final Logger logger = LoggerFactory.getLogger(DemoResponseServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {

		slingHttpServletResponse.setContentType("application/json");
		Map<String, String[]> parameterMap = slingHttpServletRequest.getParameterMap();

		for (String key : parameterMap.keySet()) {
			String[] strArr = (String[]) parameterMap.get(key);
			for (String val : strArr) {
				//slingHttpServletResponse.getWriter().write(val);
			}
		}

	}

}
