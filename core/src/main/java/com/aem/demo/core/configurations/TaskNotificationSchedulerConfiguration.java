package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * 
 * @author debal This is the configuration class that takes properties for a
 *         scheduler to run
 */

@ObjectClassDefinition(name = "TaskNotificationSchedulerConfiguration", description = "Task Notification scheduler configuration")
public @interface TaskNotificationSchedulerConfiguration {

	/**
	 * This method will return the name of the Scheduler
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
	public String schdulerName() default "Task Notification scheduler configuration";

	/**
	 * This method will set flag to enable the scheduler
	 * 
	 * @return {@link Boolean}
	 */

	@AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
	public boolean enabled() default false;

	/**
	 * This method returns the Cron expression which will decide how the scheduler
	 * will run
	 * 
	 * @return {@link String}
	 */

	@AttributeDefinition(name = "Cron Expression", description = "Cron expression used by the scheduler", type = AttributeType.STRING)
	public String cronExpression() default "0 * * * * ?";
	
	/**
	 * This method returns the Asset Path
	 * 
	 * 
	 * @return {@link String}
	 */

	@AttributeDefinition(name = "Asset Path", description = "Asset Path", type = AttributeType.STRING)
	public String assetPath() default "/content/dam";


}
