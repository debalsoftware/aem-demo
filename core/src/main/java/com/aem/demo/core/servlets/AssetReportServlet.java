package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.commons.ReferenceSearch;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/report",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class AssetReportServlet extends SlingAllMethodsServlet {
	
	private final Logger logger = LoggerFactory.getLogger(AssetReportServlet.class);

	@Reference
	private QueryBuilder queryBuilder;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		String pagePath = "/content/we-retail/language-masters/en/men";
		ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
		Session session = resourceResolver.adaptTo(Session.class);

		map.put("path", pagePath);
		map.put("type", "cq:PageContent");
		map.put("p.limit", "-1");
		Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map), session);
		SearchResult searchResult = searchquery.getResult();
		Iterator<Resource> resources = searchResult.getResources();
		while (resources.hasNext()) {
			Resource resource = resources.next();
			if (Objects.nonNull(resource)) {

				Node node = resource.adaptTo(Node.class);

				AssetReferenceSearch assetReferenceSearch = new AssetReferenceSearch(node,
						DamConstants.MOUNTPOINT_ASSETS, resourceResolver);
				Map<String, Asset> allref = new HashMap<String, Asset>();

				allref.putAll(assetReferenceSearch.search());

				for (Map.Entry<String, Asset> entry : allref.entrySet()) {

					String val = entry.getKey();

					ReferenceSearch referenceSearch = new ReferenceSearch();
					referenceSearch.setExact(true);
					referenceSearch.setHollow(true);
					referenceSearch.setMaxReferencesPerPage(-1);
					Collection<ReferenceSearch.Info> resultSet = referenceSearch.search(resourceResolver, val, -1, 0)
							.values();
					for (ReferenceSearch.Info info : resultSet) {
						for (String p : info.getProperties()) {
							slingHttpServletResponse.getWriter().println(val.concat("****************").concat(info.getPagePath()));
						}
					}

				}

			}

		}
	}
}
