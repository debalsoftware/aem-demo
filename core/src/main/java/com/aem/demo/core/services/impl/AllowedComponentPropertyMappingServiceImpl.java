package com.aem.demo.core.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.aem.demo.core.configurations.AllowedComponentPropertyMappingConfiguration;
import com.aem.demo.core.services.AllowedComponentPropertyMappingService;

@Component(service = AllowedComponentPropertyMappingService.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION
				+ "= Allowed AEM Component and Dialog Editor Field Mapping Read Service" }, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = AllowedComponentPropertyMappingConfiguration.class)
public class AllowedComponentPropertyMappingServiceImpl implements AllowedComponentPropertyMappingService {

	private String[] listofResourceTypedialogfieldmapping;

	Set<String> resourceTypedialogfieldmapping = new HashSet<>();

	@Activate
	@Modified
	private void activate(AllowedComponentPropertyMappingConfiguration configuration) {
		listofResourceTypedialogfieldmapping = configuration.resourceTypedialogfieldmapping();

	}

	/**
	 * 
	 */
	@Override
	public boolean isComponentAllowed(String mappingData) {
		resourceTypedialogfieldmapping = new HashSet<>(Arrays.asList(listofResourceTypedialogfieldmapping));
		return resourceTypedialogfieldmapping.contains(mappingData);
	}

}
