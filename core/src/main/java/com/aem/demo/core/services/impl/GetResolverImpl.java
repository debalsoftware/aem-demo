package com.aem.demo.core.services.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;

import org.apache.sling.api.resource.LoginException;

import com.aem.demo.core.services.GetResolver;


@Component(service = GetResolver.class, immediate = true)
public class GetResolverImpl implements GetResolver {
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;


	@Override
	public ResourceResolver getWorkflowServiceResolver() throws LoginException {
		return getResourceResolver("fmcc-workflow");
	}
	
	private ResourceResolver getResourceResolver(final String subServiceName) throws LoginException {
	    HashMap<String, Object> param = new HashMap<>();
	    param.put(ResourceResolverFactory.SUBSERVICE, subServiceName);
	    return resourceResolverFactory.getServiceResourceResolver(param);
	}

}
