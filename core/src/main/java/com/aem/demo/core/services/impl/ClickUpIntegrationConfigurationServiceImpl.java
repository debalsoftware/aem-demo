package com.aem.demo.core.services.impl;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.aem.demo.core.configurations.ClickUpIntegrationConfiguration;
import com.aem.demo.core.services.ClickUpIntegrationConfigurationService;

@Component(service = ClickUpIntegrationConfigurationService.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION
				+ "= ClickUp Configuration Read Service " }, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = ClickUpIntegrationConfiguration.class)
public class ClickUpIntegrationConfigurationServiceImpl implements ClickUpIntegrationConfigurationService {

	private String clickUpapiToken;
	private String[] listofAEMcontentPaths;

	private String clickUpApiEndPoint;
	private long clickUpListID;
	private String userdetailsAPI;
	

	@Activate
	@Modified
	private void activate(ClickUpIntegrationConfiguration configuration) {
		clickUpapiToken = configuration.apiToken();
		listofAEMcontentPaths = configuration.contentPaths();
		clickUpApiEndPoint = configuration.apiEndPoint();
		clickUpListID = configuration.clickUpListID();
		userdetailsAPI = configuration.authorizedUserDetailEndPoint();

	}

	@Override
	public String getclickUpapiToken() {
		return clickUpapiToken;
	}

	@Override
	public String getclickUpApiEndPoint() {
		return clickUpApiEndPoint;
	}

	@Override
	public String[] getlistofAEMcontentPaths() {
		return listofAEMcontentPaths;
	}

	@Override
	public long getclickUpListID() {
		return clickUpListID;
	}

	@Override
	public String getUserDetailsAPI() {
		return userdetailsAPI;
	}

}
