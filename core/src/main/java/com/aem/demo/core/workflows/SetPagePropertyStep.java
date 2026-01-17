package com.aem.demo.core.workflows;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.NameConstants;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=This workflow process is responsible to share info about payload",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Share info about payload" })
public class SetPagePropertyStep implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(SetPagePropertyStep.class);
	
	@Reference
	JobManager jobManager;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		WorkflowData workflowData = workItem.getWorkflowData();

		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workflowData.getPayload().toString();
		logger.info(" *** Page Path *** {}", payloadPath);
		
		if (StringUtils.isNotBlank(payloadPath)) {
			Resource resource = resourceResolver.getResource(payloadPath);
			if (Objects.nonNull(resource) && resource.isResourceType(NameConstants.NT_PAGE)) {
				
				Map<String, Object> jobProperties = new HashMap<>();
				jobProperties.put("pagePath", payloadPath);
				jobProperties.put("organization", "Adobe");

				jobManager.addJob("setproperty/job", jobProperties);

			}
		}
	}
}
