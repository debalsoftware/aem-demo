/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.aem.demo.core.services.PrintService;
import com.aem.demo.core.services.impl.PrintServiceImpl;

/**
 * @author debal
 *
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/successStory",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class SuccessStoryServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest, SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		PrintService printService = new PrintServiceImpl();
		String congratsMessage = printService.congratsMessage();
		slingHttpServletResponse.getWriter().write(congratsMessage);
	}
}
