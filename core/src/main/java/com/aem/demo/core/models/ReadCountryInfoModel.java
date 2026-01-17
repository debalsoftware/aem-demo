/**
 * 
 */

package com.aem.demo.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.aem.demo.core.configurations.DemoCountryConfiguration;
import com.aem.demo.core.services.ReadCountryConfiguration;

/**
 * @author debal
 *
 */

@Model(adaptables = { SlingHttpServletRequest.class }, resourceType = {
		ReadCountryInfoModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ReadCountryInfoModel {

	protected static final String RESOURCE_TYPE = "weretail/components/content/app";

	@OSGiService
	ReadCountryConfiguration readCountryConfiguration;

	private String countryName;

	private String capitalName;

	private String financialCapital;

	private String prefix;

	private DemoCountryConfiguration demoCountryConfiguration;

	/**
	 * @return the countryName
	 */

	public String getCountryName() {
		return countryName;
	}

	/**
	 * @return the capitalName
	 */

	public String getCapitalName() {
		return capitalName;
	}

	/**
	 * @return the financialCapital
	 */

	public String getFinancialCapital() {
		return financialCapital;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	@PostConstruct
	protected void postConstruct() {

		demoCountryConfiguration = readCountryConfiguration.getPrefix();
		capitalName = demoCountryConfiguration.capitalName();
		countryName = demoCountryConfiguration.countryName();
		financialCapital = demoCountryConfiguration.financialcapitalName();
		prefix = demoCountryConfiguration.prefix();

	}

}
