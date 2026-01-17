/**
 * 
 */
package com.aem.demo.core.services;

/**
 * @author debal
 *
 *         This service will help to read Country specific information like
 *         Country name, Capital and Financial capital of that country
 */
public interface ReadCountryInfoService {

	public String getCountryName();

	public String getCapitalName();

	public String getFinancialCapital();
}
