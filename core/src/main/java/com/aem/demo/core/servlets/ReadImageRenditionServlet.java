package com.aem.demo.core.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
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

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.Rendition;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/imageRendition",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReadImageRenditionServlet extends SlingAllMethodsServlet {

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
			Asset asset = resource.adaptTo(Asset.class);
			Rendition rendition = asset.getRendition("cq5dam.thumbnail.140.100.png");

			try {
				BufferedImage bimg = ImageIO.read(rendition.getStream());
				int height = bimg.getHeight();

				slingHttpServletResponse.getWriter().println(height);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
