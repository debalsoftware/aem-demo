/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
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

import com.aem.demo.core.services.WriteDataToFile;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/**
 * @author debal
 * 
 *         This servlet is used to generate report associated with activated
 *         page or activated assets only Report will be generated for AEM sites
 *         under specific content path
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/reports/resource",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
public class ResourceResportServlet extends SlingAllMethodsServlet {

	private final Logger logger = LoggerFactory.getLogger(ResourceResportServlet.class);

	private static final long serialVersionUID = 8383266648883534070L;
	@Reference
	private QueryBuilder queryBuilder;

	@Reference
	WriteDataToFile writeDataToFile;

	protected void doPost(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		int rowNumber = 0;
		Map<String, String> map = new HashMap<String, String>();
		Map<Integer, String> resourceMap = new TreeMap<>();
		String resourceSearchPath = slingHttpServletRequest.getParameter("searchPath");
		String lastReplicatedDate = slingHttpServletRequest.getParameter("lastPublishedDate");
		logger.info("**** Report generation has been initiated under {}" + resourceSearchPath);
		logger.info("**** Report generation has been generated from {}" + lastReplicatedDate);

		String rseourceType = null;

		if (StringUtils.isNotBlank(resourceSearchPath) && StringUtils.isNotBlank(lastReplicatedDate)) {

			try (ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver()) {

				Session session = resourceResolver.adaptTo(Session.class);

				Resource resource = resourceResolver.getResource(resourceSearchPath);

				if (Objects.nonNull(resource)) {

					String resourcepath = resource.getPath();
					if (resource.isResourceType("cq:Page")) {
						rseourceType = "cq:Page";
					} else if (resourcepath.contains("/dam/")) {
						rseourceType = "dam:Asset";
					}

					logger.info("**** Resource Type {}" + rseourceType);
					/*
					 * create query description as hash map (simplest way, same as form post)
					 */
					map.put("path", resourceSearchPath.trim());
					map.put("type", rseourceType.trim());

					map.put("1_property", "jcr:content/cq:lastReplicationAction");
					map.put("1_property.value", "Activate");
					map.put("2_daterange.property", "jcr:content/cq:lastReplicated");
					map.put("2_daterange.lowerBound", lastReplicatedDate);
					map.put("p.limit", "-1");

					Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map), session);

					

					SearchResult searchResult = searchquery.getResult();
					logger.info("**** Number of Total Matches {}" + searchResult.getTotalMatches());
					Iterator<Resource> resources = searchResult.getResources();
					while (resources.hasNext()) {

						Resource searchResultResource = resources.next();

						String searchrseourcePath = searchResultResource.getPath();
						slingHttpServletResponse.getWriter().write(searchrseourcePath);
						resourceMap.put(rowNumber++, searchrseourcePath);

					}
					writeDataToFile.addDataToFile(resourceMap, slingHttpServletResponse);
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
			;
		}

	}

}
