/**
 * 
 */
package com.aem.demo.core.configurations;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * @author debal
 *
 */
@Configuration(label = "Regional Head Configuration", description = "Regional Head Configuration")
public @interface RegionalHeadConfiguration {

	@Property(label = "Regional Head", description = "Regional Head")
	String reginalHead() default "debal";

}
