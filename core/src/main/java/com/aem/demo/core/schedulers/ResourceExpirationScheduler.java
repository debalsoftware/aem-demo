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
import com.aem.demo.core.configurations.ExpirationSchedulerConfiguration;
import com.aem.demo.core.services.ReportGenerationService;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ExpirationSchedulerConfiguration.class)

public class ResourceExpirationScheduler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(ResourceExpirationScheduler.class);

	@Reference
	ReportGenerationService reportGenerationService;

	@Reference
	Scheduler scheduler;

	private String contentDampath;
	private String contentPagePath;
	private String schedulerName;
	

	@Activate
	private void activate(ExpirationSchedulerConfiguration configuration) {

		this.schedulerName = configuration.schdulerName();
		this.contentPagePath = configuration.contentPath();
		this.contentDampath = configuration.assetPath();
		logger.info("**** Task Notification Scheduler ****");
		//This scheduler will continue to run automatically even after the server reboot, otherwise the scheduled tasks will stop running after the server reboot.
		addScheduler(configuration);
	}

	@Modified
	protected void modified(ExpirationSchedulerConfiguration configuration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configuration);
		
		contentDampath = configuration.assetPath();
		contentPagePath = configuration.contentPath();
		// Add the scheduler registered with new configuration
		addScheduler(configuration);

	}

	private void addScheduler(ExpirationSchedulerConfiguration configuration) {

		boolean enabled = configuration.enabled();
		if (enabled) {
			ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());

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
	protected void deactivated(ExpirationSchedulerConfiguration configuration) {
		logger.info("**** Removing Task Notification Scheduler Successfully on deactivation ****");
		removeScheduler(configuration);
	}

	private void removeScheduler(ExpirationSchedulerConfiguration configuration) {

		logger.info("**** Removing Task Notification Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	@Override
	public void run() {
		
		logger.info("******Inside Task Notification Scheduler  ******");
		reportGenerationService.getPageExpirationReport(contentPagePath);

	}

}
