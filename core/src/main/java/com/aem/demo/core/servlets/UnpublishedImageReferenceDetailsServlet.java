/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.demo.core.services.JcrUtility;
import com.day.cq.wcm.commons.ReferenceSearch;
import com.day.cq.wcm.commons.ReferenceSearch.Info;

/**
 * @author debal
 *
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/assetreport",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class UnpublishedImageReferenceDetailsServlet extends SlingAllMethodsServlet{

	@Reference
	private JcrUtility jcrUtility;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		try {
			ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
			Session session = resourceResolver.adaptTo(Session.class);

			String failedWorkItemQuery = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/we-retail/en]) and [jcr:content/cq:lastReplicated] is  null";
			if (Objects.nonNull(session)) {
				QueryManager queryManager = session.getWorkspace().getQueryManager();
				Query query = queryManager.createQuery(failedWorkItemQuery, Query.JCR_SQL2);
				QueryResult queryResult = query.execute();
				NodeIterator nodeIterator = queryResult.getNodes();
				while (nodeIterator.hasNext()) {
					Node nextNode = nodeIterator.nextNode();
					String assetPath = nextNode.getPath();
					Map<String, Info> search = new ReferenceSearch().search(resourceResolver, assetPath, -1, 0);
					for (Map.Entry<String, Info> entry : search.entrySet()) {

						Info info = entry.getValue();
						for (String p : info.getProperties()) {
							String pagepath = info.getPage().getPath();
							slingHttpServletResponse.getWriter().println(pagepath);

						}

					}
				}
			}
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
