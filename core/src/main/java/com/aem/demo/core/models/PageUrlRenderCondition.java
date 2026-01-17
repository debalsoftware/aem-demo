/**
 * 
 */
package com.aem.demo.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;

/**
 * @author debal
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageUrlRenderCondition {

	@ValueMapValue
	private String languageCode;

	@ScriptVariable
	Page currentPage;

	@Self
	SlingHttpServletRequest slingHttpServletRequest;

	@PostConstruct
	protected void init() {
		boolean vote = false;
		String pageURL = slingHttpServletRequest.getHeader("Referer");
		String contentPath = pageURL.substring(pageURL.indexOf("/content"));
		if (languageCode != null) {
			vote = contentPath.contains(languageCode);
		}
		slingHttpServletRequest.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(vote));
	}

}
