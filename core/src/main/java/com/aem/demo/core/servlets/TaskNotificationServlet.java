/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;

/**
 * @author debal
 *
 */

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/taskNotification",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class TaskNotificationServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6129579952150123604L;

	@Override
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

		TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);
		TaskManagerFactory taskManagerFactory = taskManager.getTaskManagerFactory();
		try {
			Task newTask = taskManagerFactory.newTask("Notofication");
			newTask.setName("Demo Task");
			newTask.setContentPath("/content/demo/us/en/reports");
			newTask.setDescription("Demo Task");
			newTask.setInstructions("Demo instruction");
			newTask.setCurrentAssignee("debal");
			taskManager.createTask(newTask);
			slingHttpServletResponse.getWriter().write(newTask.getName());

		} catch (TaskManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
