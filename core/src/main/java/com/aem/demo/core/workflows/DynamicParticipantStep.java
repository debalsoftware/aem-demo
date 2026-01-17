package com.aem.demo.core.workflows;

import org.osgi.service.component.annotations.Component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = ParticipantStepChooser.class, immediate = true, property = { "chooser.label=" + "Dynamic Reviewe Step Selection"})
public class DynamicParticipantStep implements ParticipantStepChooser {

	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		String participant = "";

		MetaDataMap map = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
		String reviewer = (String) map.get("Reviewer");

		if (!reviewer.isEmpty()) {

			participant = reviewer;
		} else {
			participant = "reviewers";
		}

		return participant;
	}
}
