package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Demo Two Scheduler Configuration", description ="Demo Two Scheduler Configuration")
public @interface DemoTwoSchedulerConfiguration {


	@AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
	public String schedulerName() default "Demo Scheduler Configuration";

	
	@AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
	public boolean enabled() default false;

	
	@AttributeDefinition(name = "Cron Expression Associates for every weekday", description = "Cron Expression Associates for every weekday", type = AttributeType.STRING)
	public String weekdayCronExpression() default "0 * * * * ?";
	
	
	
	@AttributeDefinition(name = "Cron Expression Associates for every weekend", description = "Cron Expression Associates for every weekend", type = AttributeType.STRING)
	public String weekendCronExpression() default "0 * * * * ?";
	
	
	


}
