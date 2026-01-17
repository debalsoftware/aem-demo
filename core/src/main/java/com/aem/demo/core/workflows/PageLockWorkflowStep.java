/**
 * 
 */
package com.aem.demo.core.workflows;

import java.util.Objects;

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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

/**
 * @author debal This workflow step is responsible to lock AEM page.
 */

@Component(property = { Constants.SERVICE_DESCRIPTION + "=This workflow step is responsible to lock AEM page",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Page lock process" })
public class PageLockWorkflowStep implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(PageLockWorkflowStep.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		
		
		
		
		logger.info(" Work Item Metadata map{}", workItem.getWorkflow().getWorkflowData().getMetaDataMap());
		logger.info(" Work Item Metadata map 1{}", workItem.getMetaDataMap());
		logger.info(" Work Item Metadata map 2{}", workItem.getWorkflowData().getMetaDataMap());
		
		logger.debug(" ***** Payload Path  ***** {}", payloadPath);

		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

		Resource resource = resourceResolver.getResource(payloadPath);

		if (Objects.nonNull(resource) && resource.isResourceType("cq:Page")) {

			Page page = resource.adaptTo(Page.class);
			try {
				page.lock();
			} catch (WCMException e) {

				logger.error("Unable to lock the given Page", e.getMessage());
			}

		} else {
			logger.info("**** Resource isn't a page**** {} ");
		}
	}

}
