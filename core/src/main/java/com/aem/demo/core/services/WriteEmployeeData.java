/**
 * 
 */
package com.aem.demo.core.services;

import com.aem.demo.core.models.Employee;

/**
 * @author debal
 * 
 *         This service is responsible to store Employee From Data into AEM
 *         repository
 *
 */
public interface WriteEmployeeData {

	public void storeEmployeeData(Employee employee);

}
