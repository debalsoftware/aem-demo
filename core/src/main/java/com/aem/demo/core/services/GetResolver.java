package com.aem.demo.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface GetResolver {

	public ResourceResolver getWorkflowServiceResolver() throws LoginException;
}
