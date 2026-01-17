package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/userdetailinfo",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class UserDetailInfoServlet extends SlingAllMethodsServlet {
	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {
		
		try (ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver()) {

			Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);
			try {
				String id = authorizable.getID();
				String name = authorizable.getPrincipal().getName();
				slingHttpServletResponse.getWriter().print(id.concat("******").concat(name));
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};
		
	}


}
