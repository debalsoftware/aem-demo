/**
 * 
 */
package com.aem.demo.core.services;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author debal
 * 
 *         This service will be used as a utility and it will help us to get
 *         resource resolver object , JCR session and close resource resolver
 *
 */
public interface JcrUtility {

	public ResourceResolver getResourceResolver();

	public void closeResourceResolver(ResourceResolver resourceResolver);
}
