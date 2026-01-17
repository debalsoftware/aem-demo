/**
 * 
 */
package com.aem.demo.core.schedulers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

import com.aem.demo.core.configurations.DigitalAssetPurgeSchedulerConfiguration;

/**
 * @author debal
 *
 */
@Component(service = Job.class, immediate = true)
@Designate(ocd = DigitalAssetPurgeSchedulerConfiguration.class)
public class AssetPurgeScheduler implements Job {

	private final Logger logger = LoggerFactory.getLogger(AssetPurgeScheduler.class);

	@Reference
	Scheduler scheduler;

	String schedulerName;

	@Activate
	private void activate(DigitalAssetPurgeSchedulerConfiguration configuration) {

		this.schedulerName = configuration.updateSchdulerName();

		logger.info("**** Asset Update Scheduler ****");
		// This scheduler will continue to run automatically even after the server
		// reboot, otherwise the scheduled tasks will stop running after the server
		// reboot.
		addScheduler(configuration);
	}

	@Modified
	protected void modified(DigitalAssetPurgeSchedulerConfiguration configuration) {
		// Remove the scheduler registered with old configuration
		removeScheduler(configuration);

		// Add the scheduler registered with new configuration
		addScheduler(configuration);

	}

	@Deactivate
	protected void deactivated(DigitalAssetPurgeSchedulerConfiguration configuration) {
		logger.info("**** Removing Scheduler Successfully on deactivation ****");
		removeScheduler(configuration);
	}

	private void removeScheduler(DigitalAssetPurgeSchedulerConfiguration configuration) {
		logger.info("**** Removing Scheduler Successfully **** {}", schedulerName);
		scheduler.unschedule(schedulerName);

	}

	private void addScheduler(DigitalAssetPurgeSchedulerConfiguration configuration) {

		boolean enabled = configuration.enabled();

		if (enabled) {

			/**
			 * Create a schedule options to schedule the job for English locale
			 */
			String enCronExpression = configuration.enCronExpression();
			ScheduleOptions enscheduleOptions = scheduler.EXPR(enCronExpression);
			Map<String, Serializable> enMap = new HashMap<String, Serializable>();
			String enAssetPath = configuration.enAssetPath();
			enMap.put("assetPath", enAssetPath);
			enscheduleOptions.config(enMap);
			enscheduleOptions.canRunConcurrently(false);
			scheduler.schedule(this, enscheduleOptions);

			/**
			 * Create a schedule options to schedule the job for French locale
			 */
			String frCronExpression = configuration.frCronExpression();
			ScheduleOptions frscheduleOptions = scheduler.EXPR(frCronExpression);
			Map<String, Serializable> frMap = new HashMap<String, Serializable>();
			String frAssetPath = configuration.frAssetPath();
			frMap.put("assetPath", frAssetPath);
			frscheduleOptions.config(frMap);
			frscheduleOptions.canRunConcurrently(false);
			scheduler.schedule(this, frscheduleOptions);

			/**
			 * Create a schedule options to schedule the job for India
			 */

			String inCronExpression = configuration.inCronExpression();
			ScheduleOptions inscheduleOptions = scheduler.EXPR(inCronExpression);
			Map<String, Serializable> inMap = new HashMap<String, Serializable>();
			String inAssetPath = configuration.inAssetPath();
			inMap.put("assetPath", inAssetPath);
			inscheduleOptions.config(inMap);
			inscheduleOptions.canRunConcurrently(false);
			scheduler.schedule(this, inscheduleOptions);

		}

	}

	@Override
	public void execute(JobContext jobContext) {

		logger.info("**** Asset Path****{}", jobContext.getConfiguration().get("assetPath"));
		/**
		 * Purge business logic will be included here
		 */

	}

}
