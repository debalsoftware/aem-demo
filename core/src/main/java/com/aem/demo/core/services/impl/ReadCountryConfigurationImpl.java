package com.aem.demo.core.services.impl;

import java.util.Objects;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.demo.core.configurations.DemoCountryConfiguration;
import com.aem.demo.core.services.JcrUtility;
import com.aem.demo.core.services.ReadCountryConfiguration;
import com.drew.lang.annotations.Nullable;

@Component(service = ReadCountryConfiguration.class, immediate = true)
public class ReadCountryConfigurationImpl implements ReadCountryConfiguration {

	@Reference
	JcrUtility jcrUtility;

	@Override
	public DemoCountryConfiguration getPrefix() {

		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();

		@Nullable
		Resource resource = resourceResolver.getResource("/content/we-retail");
		if (Objects.nonNull(resource)) {
			@Nullable
			ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
			if (Objects.nonNull(configurationBuilder)) {
				return configurationBuilder.as(DemoCountryConfiguration.class);
			}

		}
		jcrUtility.closeResourceResolver(resourceResolver);
		return null;
	}

}
