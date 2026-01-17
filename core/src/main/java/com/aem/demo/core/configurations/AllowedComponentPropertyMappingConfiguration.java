package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This OSGi configuration maintains a mapping between Sling Resource Types and
 * related component properties (one dialog editor field per component) like
 * demo/components/text|text, helping to clearly identify configuration usage
 * across AEM components.
 */

@ObjectClassDefinition(name = "Allowed AEM Component and Dialog Editor Field Mapping Configuration", description = "Allowed AEM Component and Dialog Editor Field Mapping Configuration")
public @interface AllowedComponentPropertyMappingConfiguration {

	@AttributeDefinition(name = "List of AEM Component and Dialog Editor Field Mapping", description = "List of AEM Component and Dialog Editor Field Mapping")
	String[] resourceTypedialogfieldmapping() default {};

}
