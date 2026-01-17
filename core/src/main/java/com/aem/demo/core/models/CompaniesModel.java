package com.aem.demo.core.models;

import java.util.List;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(
	    adaptables = {Resource.class},
	    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public interface CompaniesModel {
	@Inject
	  List<Company> getCompanies(); // the name `getCompanies` corresponds to the multifield name="./companies"
	  /**
	   * Company model
	   * has a name and a list of departments
	   */
	  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	  interface Company {
	    @Inject
	    String getName();
	    @Inject
	    List<Department> getDepartments(); // the name `getDepartments` corresponds to the multifield name="./departments"
	  }
	  /**
	   * Department model
	   * has a name and a manager
	   */
	  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	  interface Department {
	    @Inject
	    String getImage();
	    
	  }

}
