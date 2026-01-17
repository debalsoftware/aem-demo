/**
 * 
 */
package com.aem.demo.core.servlets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/**
 * 
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/resourcepath",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ResourcePathServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(ResourcePathServlet.class);
	private static final long serialVersionUID = 3146346924566021647L;
	@Reference
	QueryBuilder queryBuilder;

	@Override
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {
		Map<String, String> map = new HashMap<String, String>();
		String pagePath = "/content/we-retail/language-masters/en/experience";

		try (ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver()) {
			Session session = resourceResolver.adaptTo(Session.class);

			map.put("path", pagePath);
			map.put("type", "cq:PageContent");
			map.put("p.limit", "-1");
			Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map), session);
			SearchResult searchResult = searchquery.getResult();
			Iterator<Resource> resources = searchResult.getResources();
			if (resources.hasNext()) {
				printfirstResource(resources);
				Optional<Resource> first = StreamSupport
						.stream(Spliterators.spliteratorUnknownSize(resources, 0), false).findFirst();
				if (first.isPresent()) {
					String path = first.get().getPath();
					logger.info(" First Resource Path inside doGet Method{} ", path);
				}

			}

		} catch (Exception e) {

		}

	}

	private void printfirstResource(Iterator<Resource> resources) {
		if (resources != null) {
			Resource firstResource = resources.next();
			logger.info(" First Resource Path{} ", firstResource.getPath());
		}
	}

}
