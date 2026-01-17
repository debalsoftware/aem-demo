package com.aem.demo.core.services;

/**
 * Service responsible for verifying whether a mapping exists between a given
 * AEM component's Sling Resource Type and its dialog editor field.
 */

public interface AllowedComponentPropertyMappingService {

	boolean isComponentAllowed(String mappingData);

}
