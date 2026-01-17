package com.aem.demo.core.workflows;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = ParticipantStepChooser.class, immediate = true, property = {"chooser.label" + "=Initiator details info" })
public class InitiatorDetailsStep implements ParticipantStepChooser {

	private final Logger logger = LoggerFactory.getLogger(InitiatorDetailsStep.class);

	
	
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		String participant = "";

		MetaDataMap map = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
		String reviewer = (String) map.get("initiator");
		
		logger.info("*** Reviewer ***", reviewer);

		if (!reviewer.isEmpty()) {

			participant = reviewer;
		} else {
			participant = "reviewers";
		}

		return participant;
	}


}
