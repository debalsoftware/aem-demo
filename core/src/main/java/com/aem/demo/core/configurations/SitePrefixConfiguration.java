package com.aem.demo.core.configurations;

import org.apache.sling.caconfig.annotation.Property;

public @interface SitePrefixConfiguration {
	


	@Property(label = "WebPage Path", description = "WebPage Path")
	String pagePath() default "/content/we-retail";

	@Property(label = "DAM Path", description = "DAM Path")
	String damPath() default "/content/dam/we-retail";

	@Property(label = "Library Path", description = "Library Path")
	String libPath() default "/etc/designs";

	@Property(label = "Prefix", description = "Prefix")
	String prefix() default "wre";


}
