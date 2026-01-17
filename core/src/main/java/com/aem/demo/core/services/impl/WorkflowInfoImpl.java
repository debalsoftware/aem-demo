/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.Date;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.aem.demo.core.configurations.WorkflowInfo;
import com.aem.demo.core.services.JcrUtility;
import com.drew.lang.annotations.Nullable;

/**
 * @author debal
 *
 */
@Component(service = DynamicMBean.class, immediate = true, property = {
		"jmx.objectname = com.aem.demo.core.services.impl:type=Workflow Instances Info MBean" })
public class WorkflowInfoImpl extends AnnotatedStandardMBean implements WorkflowInfo {

	private static final Logger log = LoggerFactory.getLogger(WorkflowInfoImpl.class);

	@Reference
	JcrUtility jcrUtility;

	
	  public WorkflowInfoImpl() throws NotCompliantMBeanException {
	  super(WorkflowInfo.class);
	  
	  }
	 
	@Override
	public String getWorkflowInfo(String workflowModelId) {
		JSONObject jsonsearchResultObject = new JSONObject();
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		String workflowModelquery = "SELECT * FROM [cq:Workflow] AS s WHERE ISDESCENDANTNODE([/var/workflow/instances/server0]) and modelId = '"
				+ workflowModelId + "'";
		@Nullable
		Session session = resourceResolver.adaptTo(Session.class);
		if (Objects.nonNull(session)) {
			Workspace workspace = session.getWorkspace();
			try {
				QueryManager queryManager = workspace.getQueryManager();
				Query createQuery = queryManager.createQuery(workflowModelquery, Query.JCR_SQL2);
				QueryResult queryResult = createQuery.execute();
				NodeIterator nodeIterator = queryResult.getNodes();
				JSONArray jsonArray = new JSONArray();

				while (nodeIterator.hasNext()) {
					JSONObject jsonObject = new JSONObject();
					Node workflowinstanceNode = nodeIterator.nextNode();
					String workflowinstanceStatus = workflowinstanceNode.getProperty("status").getValue().getString();
					Date startTime = workflowinstanceNode.getProperty("startTime").getValue().getDate().getTime();
					Date endTime = workflowinstanceNode.getProperty("endTime").getValue().getDate().getTime();
					Node payloadNode = workflowinstanceNode.getNode("data/payload");
					String payloadPath = payloadNode.getProperty("path").getValue().getString();
					jsonObject.put("Status", workflowinstanceStatus);
					jsonObject.put("WorkflowInstance StartTime", startTime);
					jsonObject.put("WorkflowInstance EndTime", endTime);
					jsonObject.put("PayLoad Path", payloadPath);
					jsonArray.put(jsonObject);

				}
				jsonsearchResultObject.put("Workflow Instances", jsonArray);
			} catch (RepositoryException e) {

				log.error("*** Unable to form Query Manager Object ", e.getMessage());
			} catch (JSONException e) {

				log.error("*** Unable to form JSON Data ", e.getMessage());
			} finally {
				if (Objects.nonNull(resourceResolver)) {
					resourceResolver.close();
				}

			}

		}
		return jsonsearchResultObject.toString();
	}

}
