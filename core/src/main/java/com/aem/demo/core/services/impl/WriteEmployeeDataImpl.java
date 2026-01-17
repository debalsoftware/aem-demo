/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import com.aem.demo.core.models.Employee;
import com.aem.demo.core.services.WriteEmployeeData;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * @author debal
 * 
 *         This implementation class is responsible to store Employee form data
 *         into AEM repository Employee information will be persisted in terms
 *         of node and property as user generated content
 */
@Component(service = WriteEmployeeData.class, immediate = true)
public class WriteEmployeeDataImpl implements WriteEmployeeData {

	private final Logger logger = LoggerFactory.getLogger(WriteEmployeeDataImpl.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	Map<String, Object> map = new HashMap<>();

	@Override
	public void storeEmployeeData(Employee employee) {

		String uniqueID = UUID.randomUUID().toString();
		ResourceResolver resourceResolver = getResourceResolver();
		@Nullable
		Resource resource = resourceResolver.getResource("/content/usergenerated/formdata");

		if (Objects.nonNull(resource)) {
			map.put("firstName", employee.getFirstName());
			map.put("lastName", employee.getLastName());
			map.put("designation", employee.getDesignation());
			try {
				@NotNull
				Resource employeeData = resourceResolver.create(resource, uniqueID, map);
				logger.info(" Resource Name {}", employeeData.getName());

				resourceResolver.commit();

			} catch (PersistenceException pe) {
				logger.error("Unable to save Employee details {} ", pe.getMessage());

			} finally {
				if (Objects.nonNull(resourceResolver)) {
					resourceResolver.close();
				}

			}

		}

	}

	private ResourceResolver getResourceResolver() {
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

}
