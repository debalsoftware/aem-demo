package com.aem.demo.core.workflows;

import java.util.Objects;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.demo.core.configurations.RegionalHeadConfiguration;

@Component(service = ParticipantStepChooser.class, immediate = true, property = {
		"chooser.label=" + "Regional Head Reviewer Step Selection" })
public class RegionalHeadDynamicParticipantStep implements ParticipantStepChooser {

	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		String participant = "";
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

		Resource resource = resourceResolver.getResource(payloadPath);
		if (Objects.nonNull(resource)) {

			ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
			if (Objects.nonNull(configurationBuilder)) {
				RegionalHeadConfiguration regionalHeadConfiguration = configurationBuilder
						.as(RegionalHeadConfiguration.class);
				String reginalHead = regionalHeadConfiguration.reginalHead();
				if (!reginalHead.isEmpty()) {

					participant = reginalHead;
				} else {
					participant = "admin";
				}

			}

		}
		return participant;
	}

}
