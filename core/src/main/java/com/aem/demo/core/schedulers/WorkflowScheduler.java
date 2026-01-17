package com.aem.demo.core.schedulers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.aem.demo.core.configurations.WorkflowSchedulerConfiguration;
import com.aem.demo.core.services.JcrUtility;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = WorkflowSchedulerConfiguration.class)
public class WorkflowScheduler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(WorkflowScheduler.class);

	@Reference
	JcrUtility jcrUtility;

	@Reference
	Scheduler scheduler;

	private String webpagePath;
	private String schedulerName;
	private String model;

	@Activate
	private void activate(WorkflowSchedulerConfiguration configuration) {

		this.webpagePath = configuration.pagePath();
		this.schedulerName = configuration.schdulerName();
		this.model = configuration.model();
		logger.info("**** Workflow Scheduler ****");
		// This scheduler will continue to run automatically even after the server
		// reboot, otherwise the scheduled tasks will stop running after the server
		// reboot.
		addScheduler(configuration);
	}

	@Modified
	protected void modified(WorkflowSchedulerConfiguration configuration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configuration);

		webpagePath = configuration.pagePath();
		model = configuration.model();
		// Add the scheduler registered with new configuration
		addScheduler(configuration);

	}

	private void addScheduler(WorkflowSchedulerConfiguration configuration) {

		boolean enabled = configuration.enabled();
		if (enabled) {
			ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());

			if (StringUtils.isNotBlank(schedulerName)) {
				scheduleOptions.name(schedulerName);
				scheduleOptions.canRunConcurrently(false);
				scheduler.schedule(this, scheduleOptions);
				logger.info("****** Workflow Scheduler has been added successfully ******");

			}

		} else {
			logger.info("****** Workflow Scheduler is in disable state ******");
		}

	}

	@Deactivate
	protected void deactivated(WorkflowSchedulerConfiguration configuration) {
		logger.info("**** Removing Workflow Scheduler Successfully on deactivation ****");
		removeScheduler(configuration);
	}

	private void removeScheduler(WorkflowSchedulerConfiguration configuration) {

		logger.info("**** Removing Workflow Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	@Override
	public void run() {
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
		try {
			WorkflowModel workflowModel = workflowSession.getModel(model);
			WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", webpagePath);
			workflowSession.startWorkflow(workflowModel, workflowData);
			logger.info("******Workflow Scheduler has been started ******{}", workflowModel.getTitle());
		} catch (WorkflowException e) {

			e.printStackTrace();
		}
          finally {
			jcrUtility.closeResourceResolver(resourceResolver);
		}
	}

}
