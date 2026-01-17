package com.aem.demo.core.workflows;

import java.util.Objects;

import org.apache.sling.api.resource.ModifiableValueMap;
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
import com.drew.lang.annotations.Nullable;

@Component(service = WorkflowProcess.class, immediate = true, property = { Constants.SERVICE_DESCRIPTION + "=Custom Participant Step",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Custom Participant Step" })
public class CustomParticipantStep implements WorkflowProcess {
	private final Logger logger = LoggerFactory.getLogger(CustomParticipantStep.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		
		logger.info("**** Payload path ****", payloadPath);

		@Nullable
		Resource payloadResource = resourceResolver.getResource(payloadPath);

		if (Objects.nonNull(payloadResource) && payloadResource.isResourceType("cq:Page")) {
			Resource metadataresource = payloadResource.getChild("jcr:content");
			ModifiableValueMap modifiableValueMap = metadataresource.adaptTo(ModifiableValueMap.class);

			@Nullable
			String reviewer = modifiableValueMap.get("reviewer", String.class);
			
			logger.info("**** Reviewer Name****", reviewer);
			
			MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

			wfd.put("jcr:description", reviewer);
			

			
		}

	}

}
