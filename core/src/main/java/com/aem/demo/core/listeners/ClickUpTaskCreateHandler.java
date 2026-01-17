package com.aem.demo.core.listeners;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.ClickUpIntegrationConfigurationService;
import com.aem.demo.core.services.JcrUtility;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

/**
 * This Replication EventHandler class is designed to create a JSON payload 
 * using Page Metadata and subsequently post it to the ClickUp API endpoint,
 * facilitating task creation on the ClickUp website during the page replication.
 */
@Component(service = EventHandler.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "= ClickUp Task Create Handler ",
		EventConstants.EVENT_TOPIC + "="
				+ ReplicationAction.EVENT_TOPIC })
public class ClickUpTaskCreateHandler implements EventHandler {

	private final Logger logger = LoggerFactory.getLogger(ClickUpTaskCreateHandler.class);

	private String clickUpapiToken;
	private String[] listofAEMcontentPaths;

	private String clickUpApiEndPoint;
	private long clickUpListID;

	private List<String> listofPaths;

	@Reference
	private transient JcrUtility jcrUtility;
	
	@Reference
	private transient ClickUpIntegrationConfigurationService clickUpIntegrationConfigurationService;


	@Override
	public void handleEvent(Event event) {
		ReplicationAction replicationAction = ReplicationAction.fromEvent(event);
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		
		clickUpapiToken = clickUpIntegrationConfigurationService.getclickUpapiToken();
		listofAEMcontentPaths = clickUpIntegrationConfigurationService.getlistofAEMcontentPaths();
		clickUpApiEndPoint = clickUpIntegrationConfigurationService.getclickUpApiEndPoint();
		clickUpListID = clickUpIntegrationConfigurationService.getclickUpListID();

		if (replicationAction != null && resourceResolver != null) {
			listofPaths = Arrays.asList(listofAEMcontentPaths);
			if (StringUtils.isNotBlank(clickUpapiToken) && StringUtils.isNotBlank(clickUpApiEndPoint)
					&& ListUtils.emptyIfNull(listofPaths).size() > 0 && clickUpListID > 0) {
				String pagePath = replicationAction.getPath();
				if (StringUtils.isNotBlank(pagePath)) {

					boolean isValidPagePath = ListUtils.emptyIfNull(listofPaths).stream()
							.anyMatch(item -> pagePath.contains(item));

					if (isValidPagePath) {
						Resource resource = resourceResolver.getResource(pagePath);
						JSONObject jsonPayload = createJSONPayload(resource);
						createClickUpTask(jsonPayload, clickUpapiToken, clickUpApiEndPoint, clickUpListID);
					}
					jcrUtility.closeResourceResolver(resourceResolver);
				}
			}
		}
	}
	
	/**
	 * Create a JSON payload using Page Metadata
	 * @param resource
	 * @return json
	 */

	private JSONObject createJSONPayload(Resource resource) {
		JSONObject json = new JSONObject();
		if (resource != null) {
			Page page = resource.adaptTo(Page.class);
			if (page != null) {
				try {
					json.put("name", page.getName());
					json.put("description", page.getDescription());
					json.put("title", page.getTitle());

					Tag[] pagetags = page.getTags();
					if (pagetags != null) {
						JSONArray tagArray = new JSONArray();
						for (Tag tag : pagetags) {
							tagArray.put(tag.getTagID());
						}
						json.put("tags", tagArray);
					}

					json.put("status", "to do");
					json.put("priority", "1");

					JSONArray assigneesArray = new JSONArray();
					assigneesArray.put(102791120);
					json.put("assignees", assigneesArray);
				} catch (JSONException e) {
					logger.error("*** Unable to form JSON Data ", e.getMessage());
				}
			}
		}
		return json;
	}

	/**
	 * Subsequently post it to the ClickUp API endpoint, facilitating task creation on the ClickUp website during the page replication
	 * @param jsonPayload
	 * @param apiToken
	 * @param apiEndPoint
	 * @param ListID
	 */
	private void createClickUpTask(JSONObject jsonPayload, String apiToken, String apiEndPoint, long ListID) {

		RequestConfig requestConfig = null;
		String apiURL = apiEndPoint.concat(Long.toString(ListID)).concat("/task");

		if (StringUtils.isNotBlank(apiURL) && jsonPayload != null) {
			try {

				CloseableHttpClient client = HttpClients.createDefault();
				HttpPost post = new HttpPost(apiURL);
				requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000)
						.setSocketTimeout(3000).build();
				post.setConfig(requestConfig);

				// Set requisite headers
				post.setHeader("Content-Type", "application/json");
				post.setHeader("Authorization", apiToken);

				// Add JSON object as StringEntity
				post.setEntity(new StringEntity(jsonPayload.toString()));

				CloseableHttpResponse response = client.execute(post);

				logger.info("Response Code {}", response.getStatusLine().getStatusCode());
				logger.info("Response Body {}", EntityUtils.toString(response.getEntity()));

			} catch (UnsupportedEncodingException e) {
				logger.error("*** Unsupported encoding: ", e.getMessage());
			}  catch (IOException e) {
				logger.error("*** I/O error: ", e.getMessage());
			}
		}
	}

}
