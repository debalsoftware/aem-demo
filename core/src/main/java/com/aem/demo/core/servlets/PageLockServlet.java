/**
 * 
 */
package com.aem.demo.core.servlets;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

/**
 * @author debal This servlet is used to lock web page. Page path will be read
 *         as request parameter
 */

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/pagelock",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class PageLockServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 817468999783619498L;
	private final Logger logger = LoggerFactory.getLogger(PageLockServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String pagePath = slingHttpServletRequest.getParameter("pagePath");

		if (StringUtils.isNotBlank(pagePath)) {
			ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

			Resource resource = resourceResolver.getResource(pagePath);
			if (resource.isResourceType("cq:Page")) {
				Page page = resource.adaptTo(Page.class);
				logger.info("**** Page Name **** {} ", page.getName());

				try {
					page.lock();
				} catch (WCMException e) {

					logger.error("Unable to lock the given Page", e.getMessage());
				}

			}

			else {
				logger.info("**** Resource isn't a page**** {} ");
			}
		}
	}

}
