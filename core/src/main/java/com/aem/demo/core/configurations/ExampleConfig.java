package com.aem.demo.core.configurations;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Example Configuration", description = "This is an example configuration.")
public @interface ExampleConfig {

    @AttributeDefinition(name = "API URL", description = "The URL of the external API.")
    String apiUrl() default "https://default-api.com";
    
    @AttributeDefinition(name = "Timeout", description = "Timeout in milliseconds.")
    int timeout() default 5000;
}

