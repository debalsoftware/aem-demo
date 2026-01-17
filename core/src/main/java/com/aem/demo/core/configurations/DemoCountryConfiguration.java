/**
 * 
 */
package com.aem.demo.core.configurations;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * @author debal
 * 
 *         This is country specific Context aware configuration
 *
 */
@Configuration(label = "Country Information Configuration", description = "Country Information Configuration")
public @interface DemoCountryConfiguration {

	@Property(label = "Country Name", description = "Country Name")
	String countryName() default "US";

	@Property(label = "Capital Name", description = "Capital Name")
	String capitalName() default "Washington D.C.";

	@Property(label = "Financial Capital Name", description = "Financial Capital Name")
	String financialcapitalName() default "New York City";

	@Property(label = "Financial Capital Name", description = "Financial Capital Name")
	String prefix() default "";
}
