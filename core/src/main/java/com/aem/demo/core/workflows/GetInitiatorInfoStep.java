package com.aem.demo.core.workflows;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=This workflow step is responsible to put initiator details",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Initiator details" })
public class GetInitiatorInfoStep implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(GetInitiatorInfoStep.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		// String initiator = workItem.getWorkflow().getInitiator();

		WorkflowData workflowData = workItem.getWorkflowData();

		workflowData.getMetaDataMap().put("initiator", workItem.getWorkflow().getInitiator());
		

	}

}
