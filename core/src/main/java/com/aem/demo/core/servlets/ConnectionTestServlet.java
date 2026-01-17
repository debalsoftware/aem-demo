package com.aem.demo.core.servlets;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;


@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/testConnection",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ConnectionTestServlet extends SlingAllMethodsServlet {
	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest, SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		URL url = new URL("http://localhost:7070/var/acs-commons/reports/sample-page-report/_jcr_content.results.html?path=%2Fcontent%2Fwe-retail&template=%2Fconf%2Fwe-retail%2Fsettings%2Fwcm%2Ftemplates%2Fhero-page&order=%5Bjcr%3Acontent%2Fcq%3AlastModified%5D+DESC&order%40Delete=&page=-1");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		int responsecode = conn.getResponseCode();
		if (responsecode != 200) {
			throw new RuntimeException("HttpResponseCode: " + responsecode);
		}
		slingHttpServletResponse.getWriter().print(responsecode);
	}

}
