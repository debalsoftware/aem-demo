package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.drew.lang.annotations.Nullable;


@Component(service = Servlet.class , property = {"sling.servlet.paths="+"/bin/cookie" , "sling.servlet.methods="+ HttpConstants.METHOD_GET})
public class ReadAemCookie extends SlingAllMethodsServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2787740508751711068L;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest , SlingHttpServletResponse slingServletResponse) {
		
		@Nullable
		Cookie cookie = slingHttpServletRequest.getCookie("SessionPersistence");
		String userProfile = cookie.getValue();
		
		
		
		try {
			slingServletResponse.getWriter().print(userProfile);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}

}
