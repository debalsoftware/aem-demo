package com.aem.demo.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(
	    adaptables = {Resource.class},
	    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public interface ProductsModel {

	@Inject
	  List<Product> getProducts(); // the name `getProducts` corresponds to the multifield name="./products"
	  /**
	   * Company model
	   * has a name and a list of departments
	   */
	  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	  interface Product {
	    @Inject
	    String getText(); // corresponds name="./text"
	    
	  }
	  
}
