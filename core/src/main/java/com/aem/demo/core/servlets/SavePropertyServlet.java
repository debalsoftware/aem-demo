package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/cityname",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class SavePropertyServlet extends SlingAllMethodsServlet {

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		 Map<String, String> citymap = new HashMap<String, String>();

		@Nullable
		Resource resource = slingHttpServletRequest.getResourceResolver()
				.getResource("/etc/acs-commons/lists/cities/jcr:content/list");
		if (Objects.nonNull(resource)) {

			@NotNull
			Iterable<Resource> children = resource.getChildren();
			for (Resource childResource : children) {

				String title = childResource.getValueMap().get("jcr:title", String.class);

				String nodevalue = childResource.getValueMap().get("value", String.class);
				citymap.put(nodevalue, title);

			}

		}
		slingHttpServletResponse.getWriter().println(citymap);
	}
}
