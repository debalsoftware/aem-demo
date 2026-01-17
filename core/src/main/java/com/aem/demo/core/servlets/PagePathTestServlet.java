package com.aem.demo.core.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Servlet to check Page path",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/pagePath"})
public class PagePathTestServlet extends SlingAllMethodsServlet {
	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		//String header = slingHttpServletRequest.getHeader("referer");
		String requestURI = slingHttpServletRequest.getRequestURI();
		
		 
			
			slingHttpServletResponse.getWriter().println(requestURI);
		 
		
		
	}

}
