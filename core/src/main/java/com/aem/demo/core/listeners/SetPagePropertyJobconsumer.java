package com.aem.demo.core.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.JcrUtility;
import com.day.cq.commons.jcr.JcrConstants;

@Component(service = JobConsumer.class, immediate = true, property = {
		JobConsumer.PROPERTY_TOPICS + "=setproperty/job" })
public class SetPagePropertyJobconsumer implements JobConsumer {

	private final Logger logger = LoggerFactory.getLogger(SetPagePropertyJobconsumer.class);

	@Reference
	JcrUtility jcrUtility;

	@Override
	public JobResult process(Job job) {
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		try {

			if (resourceResolver != null) {
				String pagePath = job.getProperty("pagePath", String.class);
				String organizationName = job.getProperty("organization", String.class);

				if (StringUtils.isNotBlank(pagePath) && StringUtils.isNotBlank(organizationName)) {

					Resource jcrContentResource = resourceResolver.getResource(pagePath)
							.getChild(JcrConstants.JCR_CONTENT);
					if (jcrContentResource != null) {
						ModifiableValueMap modifiableValueMap = jcrContentResource.adaptTo(ModifiableValueMap.class);
						if (modifiableValueMap != null) {
							modifiableValueMap.put("Company Name", organizationName);
							resourceResolver.commit();

						}
					}
				}
				logger.info(" Company Name {}", organizationName);
			}
			return JobResult.OK;
		} catch (PersistenceException e) {
			logger.error(" Facing issue to set the property {}", e.getMessage());
			return JobResult.FAILED;
		} finally {
			jcrUtility.closeResourceResolver(resourceResolver);
		}
	}

}
