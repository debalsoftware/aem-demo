package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/agentDetails",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class AgentDetailsServlet extends SlingAllMethodsServlet {
	
	
	@Reference
	AgentManager agentManager;

	/**
	 * 
	 */
	
	private final Logger logger = LoggerFactory.getLogger(AgentDetailsServlet.class);

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		
		for (Agent agent : agentManager.getAgents().values()) {
			if (agent.isInMaintenanceMode()) {
				
				logger.info("**** Agent Details ****{}", agent.getId());
				logger.info("**** Agent Name ****{}", agent.getConfiguration().getName());
				slingHttpServletResponse.getWriter().write(agent.getId().concat("****************").concat(agent.getConfiguration().getName()));
				
			}
			
			
		}
	}

}
