package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This OSGi configuration contains set of properties and these properties are
 * required to integrate AEM with ClickUp
 * 
 */

@ObjectClassDefinition(name = "ClickUpIntegrationConfiguration", description = "ClickUp Integration Configuration")
public @interface ClickUpIntegrationConfiguration {

	@AttributeDefinition(name = "API Token", description = "API Token", type = AttributeType.PASSWORD)
	String apiToken() default "";

	@AttributeDefinition(name = "List of Content Paths", description = "List of Content Paths")
	String[] contentPaths() default {};

	@AttributeDefinition(name = "ClickUp API End Point", description = "ClickUp API End Point", type = AttributeType.STRING)
	String apiEndPoint() default "";

	@AttributeDefinition(name = "ClickUp List ID", description = "ClickUp List ID", type = AttributeType.LONG)
	long clickUpListID();
	
	@AttributeDefinition(name = "Get ClickUp Authorized User API End Point", description = "Get ClickUp Authorized User API End Point", type = AttributeType.STRING)
	String authorizedUserDetailEndPoint() default "";

}
