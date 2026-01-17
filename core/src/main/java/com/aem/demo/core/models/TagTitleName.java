package com.aem.demo.core.models;

/**
 * This TagTitleName POJO represents entity associated with Tag Title and Tag
 * Name
 */
public class TagTitleName {

	private String Title;
	private String Name;

	/**
	 * @param title
	 * @param name
	 */
	public TagTitleName(String title, String name) {
		Title = title;
		Name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

}
