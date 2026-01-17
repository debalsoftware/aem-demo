package com.aem.demo.core.workflows;

import java.util.Objects;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.drew.lang.annotations.Nullable;

@Component(service = WorkflowProcess.class, immediate = true,  property = {
		Constants.SERVICE_DESCRIPTION
				+ "=This workflow process step is responsible to set Inbox Notification for Reviewer",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Content Review Notification" })
public class ContentReviewNotification implements WorkflowProcess {

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		@Nullable
		Resource payloadResource = resourceResolver.getResource(payloadPath);

		if (Objects.nonNull(payloadResource) && payloadResource.isResourceType("cq:Page")) {
			Resource metadataresource = payloadResource.getChild("jcr:content");
			ModifiableValueMap modifiableValueMap = metadataresource.adaptTo(ModifiableValueMap.class);

			@Nullable
			String reviewer = modifiableValueMap.get("reviewer", String.class);

			MetaDataMap map = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

			// Putting some values in the map
			map.put("Reviewer", reviewer);

		}

	}

}
