/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Get Access and Secret Key", "sling.servlet.methods=GET",
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + ReadAccessandSecretKeyServlet.SERVLET_PATH })
public class ReadAccessandSecretKeyServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 8405662107501494736L;
	protected static final String SERVLET_PATH = "/bin/accessandsecretKey";
	
	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest , SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		String property = System.getProperty("java.awt.headless");
			slingHttpServletResponse.getWriter().println(property);
		
		
	}
}
