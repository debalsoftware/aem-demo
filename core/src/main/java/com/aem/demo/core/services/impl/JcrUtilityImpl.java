/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.JcrUtility;

/**
 * @author debal
 *
 */
@Component(service = JcrUtility.class, immediate = true) 
public class JcrUtilityImpl implements JcrUtility {

	private final Logger logger = LoggerFactory.getLogger(JcrUtilityImpl.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Override
	public ResourceResolver getResourceResolver() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(resourceResolverFactory.SUBSERVICE, "readWriteService");
		ResourceResolver serviceResourceResolver = null;
		try {

			serviceResourceResolver = resourceResolverFactory.getServiceResourceResolver(map);
		} catch (LoginException e) {
			logger.error("Could not get service user [ {} ]", "demoSystemUser", e.getMessage());
		}
		return serviceResourceResolver;

	}

	@Override
	public void closeResourceResolver(ResourceResolver resourceResolver) {
		if (Objects.nonNull(resourceResolver)) {
			resourceResolver.close();
		}

	}

}
