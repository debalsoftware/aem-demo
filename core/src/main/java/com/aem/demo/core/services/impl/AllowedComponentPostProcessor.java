package com.aem.demo.core.services.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.AllowedComponentPropertyMappingService;
import com.aem.demo.core.services.JcrUtility;

/**
 * This Sling Post Processor is responsible to read a property before they are
 * modified and after modification
 */
@Component(service = SlingPostProcessor.class, property = {
		Constants.SERVICE_RANKING + ":Integer=-1" }, immediate = true)
public class AllowedComponentPostProcessor implements SlingPostProcessor {

	private final Logger logger = LoggerFactory.getLogger(AllowedComponentPostProcessor.class);

	@Reference
	private AllowedComponentPropertyMappingService allowedComponentPropertyMappingService;

	@Reference
	private JcrUtility jcrUtility;

	private static final String MAPPING_SEPARATOR = "|";

	@Override
	public void process(SlingHttpServletRequest slingHttpServletRequest, List<Modification> listOfModifications) {

		try (ResourceResolver resourceResolver = jcrUtility.getResourceResolver()) {
			String resourceType = slingHttpServletRequest.getResource().getResourceType();
			logger.info(" Allowed Component's Resource Type {}", resourceType);

			@NotNull
			Resource resource = slingHttpServletRequest.getResource();
			String propertyName = null;

			if (StringUtils.isNotBlank(resourceType)) {
				for (Modification modification : listOfModifications) {
					if (ModificationType.MODIFY.equals(modification.getType())) {
						String source = modification.getSource();
						propertyName = extractPropertyName(source);
						if (StringUtils.isNotBlank(propertyName) && allowedComponentPropertyMappingService
								.isComponentAllowed(resourceType.concat(MAPPING_SEPARATOR).concat(propertyName))) {

							readingOldValue(modification, resourceResolver, propertyName);

							readingUpdatedValue(resource, listOfModifications, propertyName);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error(" Facing issue while reading {}", e.getMessage());
		}
	}

	/**
	 * This method is responsible to read modified property name
	 * 
	 * @param source
	 * @return
	 */
	private String extractPropertyName(String source) {
		return source.substring(source.lastIndexOf('/') + 1);
	}

	/**
	 * This method is responsible to read latest value of modified property
	 * 
	 * @param resource
	 * @param modificationsList
	 * @param propertyName
	 */
	private void readingUpdatedValue(Resource resource, List<Modification> modificationsList, String propertyName) {
		ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
		if (modifiableValueMap != null) {

			// Reading new value
			String newValue = modifiableValueMap.get(propertyName, String.class);
			logger.info(" Value after modification {}",
					StringUtils.isNotBlank(newValue) ? newValue : StringUtils.EMPTY);

			if (StringUtils.isNotBlank(newValue)) {
				modificationsList.add(Modification.onModified(newValue));
			}
		}
	}

	/**
	 * This method is responsible to read previous value of modified property
	 * 
	 * @param modification
	 * @param resourceResolver
	 * @param propertyName
	 */
	private void readingOldValue(Modification modification, ResourceResolver resourceResolver, String propertyName) {

		String resourcePath = modification.getSource();
		String componentNodePath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));

		@Nullable
		Resource resource = resourceResolver.getResource(componentNodePath);
		if (resource != null) {
			ValueMap valueMap = resource.getValueMap();

			// Reading old Value
			@Nullable
			String oldValue = valueMap.get(propertyName, String.class);
			if (StringUtils.isNotBlank(oldValue)) {
				logger.info(" Value before modification {}", oldValue);
			}
		}
	}

}
