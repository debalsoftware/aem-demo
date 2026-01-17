/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.models.Employee;
import com.aem.demo.core.services.WriteEmployeeData;

/**
 * @author debal This servlet will be invoked during Employee Form submission
 *
 */

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/employeeForm",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
public class EmployeeFormServlet extends SlingAllMethodsServlet {
	private final Logger logger = LoggerFactory.getLogger(EmployeeFormServlet.class);

	/**
	 * 
	 */
	@Reference
	WriteEmployeeData writeEmploeeData;

	private static final long serialVersionUID = -3743088247910006615L;

	protected void doPost(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String employeeFname = slingHttpServletRequest.getParameter("fname");
		String employeelname = slingHttpServletRequest.getParameter("lname");
		String employeeDesignation = slingHttpServletRequest.getParameter("designation");

		logger.info("**** Employee First Name ***** {}", employeeFname);

		logger.info("**** Employee Last Name ***** {}", employeelname);
		logger.info("**** Employee Designation ***** {}", employeeDesignation);

		if (StringUtils.isNotBlank(employeeFname) && StringUtils.isNotBlank(employeelname)
				&& StringUtils.isNotBlank(employeeDesignation)) {

			Employee employee = new Employee(employeeFname, employeelname, employeeDesignation);
			writeEmploeeData.storeEmployeeData(employee);
			try {
				slingHttpServletResponse.sendRedirect("/content/demo/us/en/reports.html");

			} catch (IOException e) {
				logger.error(" Unable to submit Form {}", e.getMessage());

			}

		}

	}
}