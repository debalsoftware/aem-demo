package com.aem.demo.core.servlets;

import java.io.IOException;
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
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.aem.demo.core.services.GetResolver;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/workflowfailed",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class FailedWorkflowdetails extends SlingAllMethodsServlet {

	@Reference
	private GetResolver getResolver;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		try {

			Session session = getResolver.getWorkflowServiceResolver().adaptTo(Session.class);
			WorkflowSession workflowSession = getResolver.getWorkflowServiceResolver().adaptTo(WorkflowSession.class);
			String failedWorkItemQuery = "SELECT * FROM [cq:WorkItem] AS s WHERE ISDESCENDANTNODE([/var/workflow/instances/server0]) and subType='FailureItem' and status = 'ACTIVE'";
			if (Objects.nonNull(session)) {
				QueryManager queryManager = session.getWorkspace().getQueryManager();
				Query query = queryManager.createQuery(failedWorkItemQuery, Query.JCR_SQL2);
				QueryResult queryResult = query.execute();
				NodeIterator nodeIterator = queryResult.getNodes();
				while (nodeIterator.hasNext()) {
					Node nextNode = nodeIterator.nextNode();
					WorkItem workItem = workflowSession.getWorkItem(nextNode.getPath());
					slingHttpServletResponse.getWriter().write(
							workItem.getItemSubType().concat("****************").concat(workItem.getNode().getTitle()));
				}

			}

		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
