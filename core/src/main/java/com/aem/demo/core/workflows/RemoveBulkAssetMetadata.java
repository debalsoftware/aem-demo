package com.aem.demo.core.workflows;

import java.util.Objects;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
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

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=This workflow process step is responsible to remove bulk asset metadata",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "= Bulk Asset Metadata" })
public class RemoveBulkAssetMetadata implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(RemoveBulkAssetMetadata.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		try {

			ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

			String payLoadPath = workItem.getWorkflowData().getPayload().toString();

			logger.info("*** Payload Path ***{}", payLoadPath);

			Resource resource = resourceResolver.getResource(payLoadPath);

			if (Objects.nonNull(resource) && resource.isResourceType("dam:Asset")) {
				
				logger.info("*** Payload Name ***{}", resource.getName());

				Resource metadataResourec = resource.getChild("jcr:content/metadata");
				ModifiableValueMap modifiableValueMap = metadataResourec.adaptTo(ModifiableValueMap.class);
				if (modifiableValueMap.containsKey("cq:tags")) {
					
					logger.info("*** Inside Metadata*** ");
					modifiableValueMap.remove("cq:tags");
				}

				resourceResolver.commit();

			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
