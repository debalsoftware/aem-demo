package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.serviceusermapping.Mapping;
import org.apache.sling.serviceusermapping.ServiceUserMapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/serviceuserdetails",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReadServiceUserServlet extends SlingAllMethodsServlet {

	@Reference
	ServiceUserMapper serviceUserMapper;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		for (Mapping mapping : serviceUserMapper.getActiveMappings()) {

			String serviceName = mapping.getServiceName();
			String subServiceName = mapping.getSubServiceName();

			try {
				if (Objects.nonNull(serviceName) && Objects.nonNull(subServiceName)) {
					slingHttpServletResponse.getWriter().println("***Sub-service***".concat(subServiceName)
							.concat("*****Symbolic-Name*****").concat(serviceName));
				}

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

}
