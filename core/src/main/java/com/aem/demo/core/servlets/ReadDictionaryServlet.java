/**
 * 
 */
package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.acs.commons.i18n.I18nProvider;

/**
 * @author debal
 *
 */
@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/readdictionary",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class ReadDictionaryServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	private I18nProvider i18nProvider;

	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {

		String localeName = slingHttpServletRequest.getParameter("locale");
		String keyName = slingHttpServletRequest.getParameter("key");

		if (Objects.nonNull(localeName) && Objects.nonNull(keyName)) {
			Locale locale = new Locale(localeName);

			try {
				String translatedValue = i18nProvider.translate(keyName, locale);
				slingHttpServletResponse.setContentType("text/plain");
				slingHttpServletResponse.setCharacterEncoding("UTF-8");
				slingHttpServletResponse.getWriter().write(translatedValue);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

}
