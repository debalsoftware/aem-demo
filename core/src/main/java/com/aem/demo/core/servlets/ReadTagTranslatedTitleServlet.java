package com.aem.demo.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.Servlet;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.formsndocuments.util.FMConstants;
import com.aem.demo.core.models.TagTitleName;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Read Translated Tag Title Servlet", "sling.servlet.methods={GET}",
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + ReadTagTranslatedTitleServlet.SERVLET_PATH,
		ServletResolverConstants.SLING_SERVLET_SELECTORS + "=language",
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json" })
public class ReadTagTranslatedTitleServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -6481726062721211517L;
	protected static final String SERVLET_PATH = "/bin/readtranslatedtagtitle";
	protected static final Logger LOGGER = LoggerFactory.getLogger(ReadTagTranslatedTitleServlet.class);

	private final String DAM_PATH = "/content/dam/demo";

	@Override
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) throws IOException {

		String[] selectors = slingHttpServletRequest.getRequestPathInfo().getSelectors();
		String extension = slingHttpServletRequest.getRequestPathInfo().getExtension();

		if (StringUtils.isNotBlank(extension) && StringUtils.contains(extension, "json")) {
			if (ArrayUtils.isNotEmpty(selectors) && ArrayUtils.getLength(selectors) == 1) {
				String language = selectors[0];
				ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();

				if (StringUtils.isNotBlank(language) && resourceResolver != null && StringUtils.isNotBlank(DAM_PATH)) {
					try {
						JSONObject tagObject = new JSONObject();
						Locale locale = getLocaleFromLanguageCode(language);
						Resource resource = resourceResolver.getResource(DAM_PATH);
						List<Resource> assetList = assetList(resource);
						if (ListUtils.emptyIfNull(assetList).size() > 0) {
							for (Resource assetResource : assetList) {
								JSONArray jsonArray = new JSONArray();
								if (assetResource != null) {
									List<Tag> tagList = getTagList(resourceResolver, assetResource);
									List<TagTitleName> tagTitleName = getTagTitleName(tagList, locale);
									for (TagTitleName tag : tagTitleName) {
										if (tag != null) {
											JSONObject jsonObject = new JSONObject();
											jsonObject.put("name", tag.getName());
											jsonObject.put("title", tag.getTitle());
											jsonArray.put(jsonObject);
										}
									}
								}
								tagObject.put(assetResource.getPath(), jsonArray);
							}
						}

						slingHttpServletResponse.setContentType("text/plain");
						slingHttpServletResponse.setCharacterEncoding("UTF-8");
						slingHttpServletResponse.getWriter().println(tagObject);

					} catch (JSONException e) {
						LOGGER.error("*** Issue while forming the JSON Data: ", e.getMessage());
					}
				}
			} else {
				slingHttpServletResponse.getOutputStream().print(" Please Add Language Code as Selector");
			}

		} else {
			slingHttpServletResponse.getOutputStream().print("{\"error:\": \"Wrong Extension\"}");
		}
	}

	/**
	 * This method is responsible to read list of assets under a DAM path
	 * 
	 * @param resource
	 * @return
	 */
	private List<Resource> assetList(Resource resource) {
		if (resource != null && resource.hasChildren()) {
			Iterable<Resource> children = resource.getChildren();
			List<Resource> assetList = new ArrayList<>();
			for (Resource childResource : children) {
				if (childResource != null && DamUtil.isAsset(childResource)) {
					assetList.add(childResource);
				}
			}
			return assetList;
		}
		return Collections.emptyList();
	}

	/**
	 * This method is responsible to read cq:tags metadata associated with an Asset
	 * 
	 * @param resourceResolver
	 * @param resource
	 * @return
	 */
	private List<Tag> getTagList(final ResourceResolver resourceResolver, final Resource resource) {

		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if (tagManager != null) {
			Resource metadataResource = resource.getChild(FMConstants.METADATA_PROPERTY);
			if (metadataResource != null) {
				Tag[] tags = tagManager.getTags(metadataResource);
				if (tags != null) {
					return Arrays.asList(tags);
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Derive the locale from the Language Code
	 * 
	 * @param languageCode
	 * @return
	 */
	private Locale getLocaleFromLanguageCode(final String languageCode) {
		if (StringUtils.contains(languageCode, "-")) {
			String[] segments = StringUtils.split(languageCode, "-");
			if (ArrayUtils.isNotEmpty(segments) && ArrayUtils.getLength(segments) == 2
					&& ArrayUtils.contains(Locale.getISOLanguages(), segments[0])
					&& ArrayUtils.contains(Locale.getISOCountries(), segments[1].toUpperCase())) {
				String language = segments[0];
				String countryCode = segments[1].toUpperCase();
				if (StringUtils.isNotBlank(language) && StringUtils.isNotBlank(countryCode)) {
					return LocaleUtils.toLocale(language + "_" + countryCode);
				}
			}
		} else if (ArrayUtils.contains(Locale.getISOLanguages(), languageCode)) {
			return LocaleUtils.toLocale(languageCode);
		} else {
			return LocaleUtils.toLocale(Locale.US.getLanguage() + "_" + Locale.US.getCountry());
		}

		return null;
	}

	private List<TagTitleName> getTagTitleName(List<Tag> tagList, Locale locale) {
		return tagList.stream().map(tag -> new TagTitleName(tag.getTitle(locale), tag.getName()))
				.collect(Collectors.toList());

	}
}
