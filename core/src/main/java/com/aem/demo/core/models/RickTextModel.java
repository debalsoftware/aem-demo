/**
 * 
 */
package com.aem.demo.core.models;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * @author debal
 *
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = {
		"weretail/components/content/text" })

@Exporter(name = "jackson", extensions = "json")
public class RickTextModel {

	@ValueMapValue
	private String text;

	@JsonRawValue
	public String getText() {
		return StringEscapeUtils.unescapeHtml4(text).replaceAll("\\<.*?\\>", "");

	}

}
