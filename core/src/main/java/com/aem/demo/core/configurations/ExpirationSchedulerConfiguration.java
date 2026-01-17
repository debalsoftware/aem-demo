package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Resource Expiration scheduler configuration", description = "Resource Expiration scheduler configuration")
public @interface ExpirationSchedulerConfiguration {

	/**
	 * This method will return the name of the Scheduler
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
	public String schdulerName() default "Expiration scheduler configuration";

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

	@AttributeDefinition(name = "Page Taxonomy", description = "Page Taxonomy", type = AttributeType.STRING)
	public String contentPath() default "/content/we-retail";

}
