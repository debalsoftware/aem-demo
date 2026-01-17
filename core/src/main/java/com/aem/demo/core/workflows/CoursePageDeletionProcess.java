package com.aem.demo.core.workflows;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

/**
 * This Workflow process is responsible to automatically delete the page from
 * the content hierarchy
 */
@Component(property = {
		Constants.SERVICE_DESCRIPTION
				+ "=This Workflow process is responsible to automatically delete the page from the content hierarchy",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Course Page Deletion Process" })
public class CoursePageDeletionProcess implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(CoursePageDeletionProcess.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

		if (StringUtils.isNotBlank(payloadPath) && resourceResolver != null) {

			Resource resource = resourceResolver.getResource(payloadPath);
			if (resource != null && resource.isResourceType(NameConstants.NT_PAGE)) {
				Page page = resource.adaptTo(Page.class);
				PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
				if (page != null && pageManager != null) {
					try {
						pageManager.delete(page, false, true);
						logger.info("*** The following page got deleted after unpublish ***", page.getPath());
					} catch (WCMException e) {
						logger.error("*** Unable to Delete Page ***", e.getMessage());
					}
				}
			}
		}
		if (resourceResolver != null && resourceResolver.isLive()) {
			resourceResolver.close();
		}
	}
}
