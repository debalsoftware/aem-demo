package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/searchResult",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class SearchResultDetailsServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1313032429840582591L;

	private final Logger logger = LoggerFactory.getLogger(SearchResultDetailsServlet.class);

	@Reference
	private QueryBuilder queryBuilder;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		try {
			ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
			Session session = resourceResolver.adaptTo(Session.class);
			Map<String, String> map = new HashMap<String, String>();

			
			map.put("path", "/content/we-retail");
			map.put("type", "cq:Page");

			map.put("property", "jcr:content/cq:lastReplicationAction");
			map.put("property.value", "Activate");
			map.put("p.excerpt", "true");

			map.put("p.limit", "-1");

			Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map), session);

			SearchResult searchResult = searchquery.getResult();
			logger.info("**** Number of Total Matches {}" + searchResult.getTotalMatches());

			// searchResult.getTotalMatches();

			// slingHttpServletResponse.getWriter().write(Long.toString(searchResult.getTotalMatches()));

			List<Hit> hits = searchResult.getHits();
			for (Hit hit : hits) {
				Map<String, String> excerpts = hit.getExcerpts();
				for (Map.Entry<String, String> entry : excerpts.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					//slingHttpServletResponse.getWriter().write(key.concat("****************").concat(val));
					slingHttpServletResponse.getWriter().println(key);
				}

			}

		} 
			  catch (RepositoryException e) {
			  
			  e.printStackTrace(); }
			  catch (IOException e) {

			e.printStackTrace();
		}

	}
}
