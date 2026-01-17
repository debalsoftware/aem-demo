/*
 * package com.aem.demo.core.servlets;
 * 
 * import java.io.IOException;
 * 
 * import javax.servlet.Servlet;
 * 
 * import org.apache.sling.api.SlingHttpServletRequest; import
 * org.apache.sling.api.SlingHttpServletResponse; import
 * org.apache.sling.api.resource.ResourceResolver; import
 * org.apache.sling.api.servlets.HttpConstants; import
 * org.apache.sling.api.servlets.SlingAllMethodsServlet; import
 * org.osgi.service.component.annotations.Component; import
 * org.osgi.service.component.annotations.Reference; import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * import com.aem.demo.core.listeners.PagePropertyUpdateListener; import
 * com.aem.demo.core.services.JcrUtility; import com.day.cq.audit.AuditLog;
 * import com.day.cq.audit.AuditLogEntry;
 * 
 * @Component(service = Servlet.class, property = { "sling.servlet.paths=" +
 * "/bin/pageEvent", "sling.servlet.methods=" + HttpConstants.METHOD_GET })
 * public class ReadPageEventServlet extends SlingAllMethodsServlet {
 * 
 * 
 * @Reference JcrUtility jcrUtility;
 * 
 * 
 * @Reference AuditLog auditLog;
 * 
 *//**
	* 
	*//*
		 * 
		 * private final Logger logger =
		 * LoggerFactory.getLogger(ReadPageEventServlet.class);
		 * 
		 * protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
		 * SlingHttpServletResponse slingHttpServletResponse) throws IOException {
		 * 
		 * //ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		 * 
		 * String path = "/content/we-retail/language-masters/en/men"; String []
		 * categories = { PagePropertyUpdateListener.class.getName() }; AuditLogEntry[]
		 * auditLogEntries =
		 * auditLog.getLatestEventsFromTree(slingHttpServletRequest.getResourceResolver(
		 * ), categories, path, -1);
		 * 
		 * 
		 * 
		 * for (AuditLogEntry auditLogEntry : auditLogEntries) {
		 * logger.info("****  Details Path ****{}", auditLogEntry.getPath());
		 * logger.info("****  User Id ****{}", auditLogEntry.getUserId());
		 * slingHttpServletResponse.getWriter().write(auditLogEntry.getCategory().concat
		 * ("****************").concat(auditLogEntry.getPath())); }
		 * 
		 * }
		 * 
		 * }
		 */