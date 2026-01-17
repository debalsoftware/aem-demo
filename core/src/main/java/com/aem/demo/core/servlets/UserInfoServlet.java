package com.aem.demo.core.servlets;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.demo.core.services.JcrUtility;

import com.drew.lang.annotations.Nullable;


@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/userInfo",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class UserInfoServlet extends SlingAllMethodsServlet {
	
	
	@Reference
	JcrUtility jcrUtility;
	
	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest, SlingHttpServletResponse slingHttpServletResponse) {
		
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		@Nullable
		Session session = resourceResolver.adaptTo(Session.class);
		@Nullable
		UserManager userManager = resourceResolver.adaptTo(UserManager.class);
		try {
			User user = (User)userManager.getAuthorizable(session.getUserID());
			
			Credentials credentials = user.getCredentials();
			
			if (credentials instanceof TokenCredentials) {
				TokenCredentials tc = (TokenCredentials) credentials;
				
				
			}
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
