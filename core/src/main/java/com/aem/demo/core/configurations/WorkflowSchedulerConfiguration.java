package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Workflow Scheduler Configuration", description = "Workflow Scheduler Configuration")
public @interface WorkflowSchedulerConfiguration {

	/**
	 * This method will return the name of the Scheduler
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
	public String schdulerName() default "Workflow Scheduler configuration";

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
	 * This method returns the Webpage Path
	 * 
	 * 
	 * @return {@link String}
	 */

	@AttributeDefinition(name = "Webpage Path", description = "Webpage Path", type = AttributeType.STRING)
	public String pagePath() default "/content/we-retail/language-masters/en";
	
	@AttributeDefinition(name = "Workflow Model", description = "Workflow Model", type = AttributeType.STRING)
	public String model() default "";

}
