/**
 * 
 */
package com.aem.demo.core.schedulers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
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

import com.aem.demo.core.configurations.DemoSchedulerConfiguration;

/**
 * @author debal
 *
 */
@Component(service = Job.class, immediate = true)
@Designate(ocd = DemoSchedulerConfiguration.class)
public class DemoScheduler implements Job {

	private final Logger logger = LoggerFactory.getLogger(DemoScheduler.class);

	@Reference
	Scheduler scheduler;

	private String schedulerName;

	@Activate
	private void activate(DemoSchedulerConfiguration configuration) {

		this.schedulerName = configuration.schedulerName();
		logger.info("****  Scheduler ****");
		// This scheduler will continue to run automatically even after the server
		// reboot, otherwise the scheduled tasks will stop running after the server
		// reboot.
		addScheduler(configuration);
	}

	@Modified
	protected void modified(DemoSchedulerConfiguration configuration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configuration);

		// Add the scheduler registered with new configuration
		addScheduler(configuration);

	}

	private void addScheduler(DemoSchedulerConfiguration configuration) {

		boolean enabled = configuration.enabled();
		if (enabled) {

			if (StringUtils.isNotBlank(schedulerName)) {
				// Weekend
				ScheduleOptions weekendscheduleOptions = scheduler.EXPR(configuration.weekendCronExpression());
				Map<String, Serializable> weekendMap = new HashMap<String, Serializable>();
				weekendMap.put("day", "weekend");
				weekendscheduleOptions.config(weekendMap);
				weekendscheduleOptions.canRunConcurrently(false);
				logger.info("******  Scheduler has been added successfully during weekend******");
				scheduler.schedule(this, weekendscheduleOptions);
				

				ScheduleOptions weekdayscheduleOptions = scheduler.EXPR(configuration.weekdayCronExpression());
				Map<String, Serializable> weekdayMap = new HashMap<String, Serializable>();
				weekdayMap.put("day", "weekday");
				weekdayscheduleOptions.config(weekdayMap);
				weekdayscheduleOptions.canRunConcurrently(false);
				logger.info("******  Scheduler has been added successfully during weekdays******");
				scheduler.schedule(this, weekdayscheduleOptions);

			}

		} else {
			logger.info("******  Scheduler is in disable state ******");
		}

	}

	@Deactivate
	protected void deactivated(DemoSchedulerConfiguration configuration) {
		logger.info("**** Removing  Scheduler Successfully on deactivation ****");
		removeScheduler(configuration);
	}

	private void removeScheduler(DemoSchedulerConfiguration configuration) {

		logger.info("**** Removing  Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	

	@Override
	public void execute(JobContext jobContext) {
		logger.info("**** Days ****{}", jobContext.getConfiguration().get("day"));
		
	}

}
