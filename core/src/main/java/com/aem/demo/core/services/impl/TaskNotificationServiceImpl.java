/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.adobe.granite.workflow.exec.InboxItem;
import com.aem.demo.core.services.TaskNotificationService;
import com.drew.lang.annotations.Nullable;

/**
 * @author debal
 * 
 *         This Implementation class is responsible to responsible to set/send
 *         notification in AEM inbox in case we set expiration datetime of an
 *         asset after current date time. Task Management in AEM is the set of
 *         APIs that manage Tasks, which can show up in AEM user's inbox. This
 *         can be done using Task and TaskManager API
 */
@Component(service = TaskNotificationService.class, immediate = true)
public class TaskNotificationServiceImpl implements TaskNotificationService {

	private final Logger logger = LoggerFactory.getLogger(TaskNotificationServiceImpl.class);

	public static final String NOTIFICATION_TASK_TYPE = "Notification";

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Override
	public void setTaskNotification(String assetPath) {

		ResourceResolver serviceResourceResolver = getResourceResolver();

		try {

			TaskManager taskManager = serviceResourceResolver.adaptTo(TaskManager.class);
			TaskManagerFactory taskManagerFactory = taskManager.getTaskManagerFactory();
			if (StringUtils.isNotBlank(assetPath)) {
				Resource resource = serviceResourceResolver.getResource(assetPath);
				if (Objects.nonNull(resource) && resource.isResourceType("dam:Asset")) {
					Resource metadataresource = resource.getChild("jcr:content/metadata");
					ModifiableValueMap modifiableValueMap = metadataresource.adaptTo(ModifiableValueMap.class);

					@Nullable
					String creatorName = modifiableValueMap.get("dc:creator", String.class);

					@Nullable
					Date expirydate = modifiableValueMap.get("prism:expirationDate", Date.class);
					logger.info("*** Expiration Date ***{}", expirydate);

					Date currentDate = new Date();
					if (Objects.nonNull(expirydate) && StringUtils.isNotBlank(creatorName)) {
						if (expirydate.after(currentDate)) {
							Task newTask = taskManagerFactory.newTask(NOTIFICATION_TASK_TYPE);
							newTask.setName("Content Expiry Notification");
							newTask.setContentPath(assetPath);
							// Optionally set priority (High, Medium, Low)
							newTask.setPriority(InboxItem.Priority.HIGH);
							newTask.setDescription("Content Expiry Notification");
							newTask.setInstructions("Content Expiry Notification");
							newTask.setCurrentAssignee(creatorName);
							taskManager.createTask(newTask);

						}

					}

				}

			}

		} catch (TaskManagerException te) {
			logger.error("Could not create task {} ", te.getMessage());
		} finally {
			if (Objects.nonNull(serviceResourceResolver)) {
				serviceResourceResolver.close();
			}

		}
	}

	private ResourceResolver getResourceResolver() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(resourceResolverFactory.SUBSERVICE, "readWriteService");
		ResourceResolver serviceResourceResolver = null;
		try {

			serviceResourceResolver = resourceResolverFactory.getServiceResourceResolver(map);
		} catch (LoginException e) {
			logger.error("Could not get service user [ {} ]", "demoSystemUser", e.getMessage());
		}
		return serviceResourceResolver;

	}

}
