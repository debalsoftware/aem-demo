package com.aem.demo.core.schedulers;

import java.time.LocalDate;

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

import com.aem.demo.core.configurations.DemoTwoSchedulerConfiguration;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = DemoTwoSchedulerConfiguration.class)
public class DemoTwoScheduler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(DemoTwoScheduler.class);

	@Reference
	Scheduler scheduler;

	private String schedulerName;

	@Activate
	private void activate(DemoTwoSchedulerConfiguration configuration) {

		this.schedulerName = configuration.schedulerName();
		logger.info("****  Scheduler ****");
		// This scheduler will continue to run automatically even after the server
		// reboot, otherwise the scheduled tasks will stop running after the server
		// reboot.
		addScheduler(configuration);
	}

	@Modified
	protected void modified(DemoTwoSchedulerConfiguration configuration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configuration);

		// Add the scheduler registered with new configuration
		addScheduler(configuration);

	}

	private void addScheduler(DemoTwoSchedulerConfiguration configuration) {

		boolean enabled = configuration.enabled();
		if (enabled) {

			if (StringUtils.isNotBlank(schedulerName)) {
				// Weekend
				ScheduleOptions weekendscheduleOptions = scheduler.EXPR(configuration.weekendCronExpression());
				
				weekendscheduleOptions.canRunConcurrently(false);
				logger.info("******  Scheduler has been added successfully during weekend******");
				scheduler.schedule(this, weekendscheduleOptions);
				

				ScheduleOptions weekdayscheduleOptions = scheduler.EXPR(configuration.weekdayCronExpression());
				
				weekdayscheduleOptions.canRunConcurrently(false);
				logger.info("******  Scheduler has been added successfully during weekdays******");
				scheduler.schedule(this, weekdayscheduleOptions);

			}

		} else {
			logger.info("******  Scheduler is in disable state ******");
		}

	}

	@Deactivate
	protected void deactivated(DemoTwoSchedulerConfiguration configuration) {
		logger.info("**** Removing  Scheduler Successfully on deactivation ****");
		removeScheduler(configuration);
	}

	private void removeScheduler(DemoTwoSchedulerConfiguration configuration) {

		logger.info("**** Removing  Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	@Override
	public void run() {
		logger.info("**** Demo Two Scheduler ****{}", LocalDate.now());
		
	}

	

	
}
