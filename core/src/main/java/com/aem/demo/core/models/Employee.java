/**
 * 
 */
package com.aem.demo.core.models;

/**
 * @author debal
 *
 *         This Employee POJO represents entity Employee
 */
public class Employee {

	private String firstName;
	private String lastName;
	private String designation;

	/**
	 * @param firstName
	 * @param lastName
	 * @param designation
	 */
	public Employee(String firstName, String lastName, String designation) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.designation = designation;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

}
