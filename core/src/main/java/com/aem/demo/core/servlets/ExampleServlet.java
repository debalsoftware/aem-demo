package com.aem.demo.core.servlets;



import com.aem.demo.core.configurations.ExampleConfig;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/example",
        "sling.servlet.methods=GET"
    }
)
@Designate(ocd = ExampleConfig.class)  // Link to OSGi configuration interface
public class ExampleServlet extends SlingAllMethodsServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ExampleServlet.class);
    
    private String apiUrl;
    private int timeout;

    // Read the OSGi configuration values
    @Activate
    protected void activate(ExampleConfig config) {
        this.apiUrl = config.apiUrl();    // Get the API URL from the configuration
        this.timeout = config.timeout();  // Get the timeout value from the configuration
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Use the configuration values inside the servlet
        response.setContentType("application/json");
        response.getWriter().write("{ \"apiUrl\": \"" + apiUrl + "\", \"timeout\": " + timeout + " }");
        LOG.info("API URL: {}", apiUrl);
        LOG.info("Timeout: {}", timeout);
    }
}
