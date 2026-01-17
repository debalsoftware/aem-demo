/**
 * 
 */
package com.aem.demo.core.servlets;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
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
 * @author debal This servlet is used to Unlock web page. Page path will be read
 *         as request parameter
 *
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/pageUnlock",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class PageUnLockServlet extends SlingAllMethodsServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5325572267755465737L;
	private final Logger logger = LoggerFactory.getLogger(PageUnLockServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletReponse) {

		String pagePath = slingHttpServletRequest.getParameter("pagePath");

		if (StringUtils.isNotBlank(pagePath)) {

			ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

			Resource resourcePage = resourceResolver.getResource(pagePath);

			if (resourcePage.isResourceType("cq:Page")) {

				Page page = resourcePage.adaptTo(Page.class);

				logger.info("**** Page path**** {} ", page.getPath());
				if (page.canUnlock()) {
					try {
						page.unlock();
					} catch (WCMException e) {
						logger.error("Unable to Unlock the given Page", e.getMessage());
					}

				}

			}

		}

	}

}
