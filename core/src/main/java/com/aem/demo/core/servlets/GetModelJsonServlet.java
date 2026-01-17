package com.aem.demo.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Custom Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/getmodeljson",
                "sling.servlet.methods=GET"
        }
)
public class GetModelJsonServlet extends SlingSafeMethodsServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        // Get the path of the page whose model.json you want to retrieve
        String pagePath = "/content/we-retail/language-masters/en"; // Change this to the actual path of the page
        
        // Construct the URL of the model.json endpoint for the page
        String modelJsonUrl = request.getContextPath() + pagePath + "/model.json";
        
        // Perform an HTTP GET request to fetch the Sling Model JSON data
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(modelJsonUrl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                // Convert the HTTP response entity to a String
                try (InputStream inputStream = entity.getContent()) {
                    String modelJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    
                    // Write the Sling Model JSON data to the servlet response
                    response.setContentType("application/json");
                    response.getWriter().write(modelJson);
                }
            }
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error fetching model.json: " + e.getMessage());
        }
    }
}

