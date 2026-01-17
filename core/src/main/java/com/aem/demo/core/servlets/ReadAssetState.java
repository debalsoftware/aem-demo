package com.aem.demo.core.servlets;

import java.io.IOException;

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

import com.day.cq.commons.jcr.JcrConstants;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/assetStatus",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReadAssetState extends SlingAllMethodsServlet {

	private final Logger logger = LoggerFactory.getLogger(PageLockServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String assetPath = slingHttpServletRequest.getParameter("assetPath");

		if (StringUtils.isNotBlank(assetPath)) {

			ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

			Resource resource = resourceResolver.getResource(assetPath.concat("/").concat(JcrConstants.JCR_CONTENT));

			String assetstate = resource.getValueMap().get("dam:assetState", String.class);

			try {
				slingHttpServletResponse.getWriter().print(assetstate);

			} catch (IOException e) {
				logger.error(" Unable to read asset state {}", e.getMessage());

			}

		}

	}

}
