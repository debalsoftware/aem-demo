package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.ClickUpIntegrationConfigurationService;
import com.google.gson.JsonObject;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Get Click Up User Details Servlet", "sling.servlet.methods={GET}",
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + ReadClickUpUserDetail.SERVLET_PATH })
public class ReadClickUpUserDetail extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 947560324744863187L;
	protected static final String SERVLET_PATH = "/bin/clickupuserdetails";
	protected static final Logger LOGGER = LoggerFactory.getLogger(ReadClickUpUserDetail.class);

	protected static final String errorMessage = "There must be an issue with either API Token or User Details API Endpoint. Please try again later.";

	@Reference
	private transient ClickUpIntegrationConfigurationService clickUpIntegrationConfigurationService;

	@Override
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String userDetailsAPI = clickUpIntegrationConfigurationService.getUserDetailsAPI();
		String apiToken = clickUpIntegrationConfigurationService.getclickUpapiToken();
		RequestConfig requestConfig = null;
		if (StringUtils.isNotBlank(userDetailsAPI) && StringUtils.isNotBlank(apiToken)) {

			try {
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(userDetailsAPI);
				requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000)
						.setSocketTimeout(3000).build();
				get.setConfig(requestConfig);

				// Set requisite headers
				get.setHeader("Content-Type", "application/json");
				get.setHeader("Authorization", apiToken);

				CloseableHttpResponse response = client.execute(get);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					slingHttpServletResponse.getWriter().println(EntityUtils.toString(response.getEntity()));
				} else {
					JsonObject errorResponse = new JsonObject();
					errorResponse.addProperty("statusCode", SlingHttpServletResponse.SC_BAD_REQUEST);
					errorResponse.addProperty("message", errorMessage);
					slingHttpServletResponse.getWriter().write(errorResponse.toString());
				}

			} catch (IOException e) {
				LOGGER.error("*** I/O error: ", e.getMessage());
			}
		}
	}

}
