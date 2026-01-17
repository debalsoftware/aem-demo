package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/createServlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class TagServlet extends SlingAllMethodsServlet {

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {
		
		String[] tagsFromParent = { "dam:fruit/apple", "dam:fruit/mango" };
		ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		for (String tagId : tagsFromParent) {
			Tag	tag = tagManager.resolve(tagId);
		}

		try {
			slingHttpServletResponse.getWriter().println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
