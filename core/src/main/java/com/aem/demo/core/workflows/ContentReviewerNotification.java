package com.aem.demo.core.workflows;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.InboxItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.drew.lang.annotations.Nullable;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=This workflow step is responsible to set Inbox Notification for Reviewer",
		Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" + "=Content Reviewer Notification" })
public class ContentReviewerNotification implements WorkflowProcess {

	private final Logger logger = LoggerFactory.getLogger(ContentReviewerNotification.class);

	public static final String NOTIFICATION_TASK_TYPE = "Review Notification";

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {

		ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
		try {
			TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);
			TaskManagerFactory taskManagerFactory = taskManager.getTaskManagerFactory();
			String payloadPath = workItem.getWorkflowData().getPayload().toString();
			@Nullable
			Resource payloadResource = resourceResolver.getResource(payloadPath);

			if (Objects.nonNull(payloadResource) && payloadResource.isResourceType("cq:Page")) {
				Resource metadataresource = payloadResource.getChild("jcr:content");
				ModifiableValueMap modifiableValueMap = metadataresource.adaptTo(ModifiableValueMap.class);

				@Nullable
				String reviewer = modifiableValueMap.get("reviewer", String.class);

				if (StringUtils.isNotBlank(reviewer)) {

					Task newTask = taskManagerFactory.newTask(NOTIFICATION_TASK_TYPE);
					newTask.setName("Content Expiry Notification");
					newTask.setContentPath(payloadPath);
					// Optionally set priority (High, Medium, Low)
					newTask.setPriority(InboxItem.Priority.HIGH);
					newTask.setDescription("Content Review Notification");
					newTask.setInstructions("Content Review Notification");
					newTask.setCurrentAssignee(reviewer);
					taskManager.createTask(newTask);
				}

			}

		} catch (TaskManagerException te) {
			logger.error("Could not create task {} ", te.getMessage());
		}

	}

}
