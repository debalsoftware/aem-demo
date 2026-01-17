package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.security.user.UserProperties;
import com.aem.demo.core.services.JcrUtility;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/username",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class UserPropertiesServlet extends SlingAllMethodsServlet {
	
	@Reference
	JcrUtility jcrUtility;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3561858749807565841L;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest, SlingHttpServletResponse slingHttpServletResponse) {
		
		
		
		//ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
		
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		
		UserProperties userProperties = resourceResolver.adaptTo(UserProperties.class);
		try {
			slingHttpServletResponse.getWriter().write(userProperties.getDisplayName());
		} catch (IOException | RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
