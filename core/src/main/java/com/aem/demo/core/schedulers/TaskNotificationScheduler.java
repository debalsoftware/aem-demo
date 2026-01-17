/**
 * 
 */
package com.aem.demo.core.schedulers;

import org.apache.commons.lang3.StringUtils;
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

import com.aem.demo.core.configurations.TaskNotificationSchedulerConfiguration;
import com.aem.demo.core.services.TaskNotificationService;


/**
 * @author debal
 *
 *         A cron-job like tasks that get executed regularly.
 */
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = TaskNotificationSchedulerConfiguration.class)
public class TaskNotificationScheduler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(TaskNotificationScheduler.class);

	@Reference
	TaskNotificationService taskNotificationService;

	@Reference
	Scheduler scheduler;

	private String contentDampath;
	private String schedulerName;
	

	@Activate
	private void activate(TaskNotificationSchedulerConfiguration configguration) {

		this.contentDampath = configguration.assetPath();
		this.schedulerName = configguration.schdulerName();
		logger.info("**** Task Notification Scheduler ****");
		//This scheduler will continue to run automatically even after the server reboot, otherwise the scheduled tasks will stop running after the server reboot.
		addScheduler(configguration);
	}

	@Modified
	protected void modified(TaskNotificationSchedulerConfiguration configguration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configguration);
		
		contentDampath = configguration.assetPath();
		// Add the scheduler registered with new configuration
		addScheduler(configguration);

	}

	private void addScheduler(TaskNotificationSchedulerConfiguration configguration) {

		boolean enabled = configguration.enabled();
		if (enabled) {
			ScheduleOptions scheduleOptions = scheduler.EXPR(configguration.cronExpression());

			if (StringUtils.isNotBlank(schedulerName)) {
				scheduleOptions.name(schedulerName);
				scheduleOptions.canRunConcurrently(false);
				scheduler.schedule(this, scheduleOptions);
				logger.info("****** Task Notification Scheduler has been added successfully ******");

			}

		} else {
			logger.info("****** Task Notification Scheduler is in disable state ******");
		}

	}

	@Deactivate
	protected void deactivated(TaskNotificationSchedulerConfiguration configguration) {
		logger.info("**** Removing Task Notification Scheduler Successfully on deactivation ****");
		removeScheduler(configguration);
	}

	private void removeScheduler(TaskNotificationSchedulerConfiguration configguration) {

		logger.info("**** Removing Task Notification Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	@Override
	public void run() {
		taskNotificationService.setTaskNotification(contentDampath);
		logger.info("******Inside Task Notification Scheduler  ******");

	}

}
