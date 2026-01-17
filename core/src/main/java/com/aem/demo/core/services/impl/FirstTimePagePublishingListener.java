/**
 * 
 */

package com.aem.demo.core.services.impl;

import java.util.Objects;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.sling.api.resource.ResourceResolver;
import com.drew.lang.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.JcrUtility;

/**
 * @author debal
 *
 */

@Component(service = EventListener.class, immediate = true)
public class FirstTimePagePublishingListener implements EventListener {

	private static final Logger log = LoggerFactory.getLogger(FirstTimePagePublishingListener.class);

	private String[] nodeTypes = { "cq:PageContent" };

	@Reference
	JcrUtility jcrUtility;

	@Activate
	public void acivate() {
		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();

		@Nullable
		Session session = resourceResolver.adaptTo(Session.class);
		if (Objects.nonNull(session)) {
			try {
				session.getWorkspace().getObservationManager().addEventListener(this, Event.NODE_ADDED | Event.PROPERTY_ADDED,
						"/content/we-retail/language-masters/en", true, null, nodeTypes, false);
			} catch (RepositoryException e) {
				log.error("*** Error While adding Event Listener ***{}", e.getMessage());

			}

		}

	}

	@Override
	public void onEvent(EventIterator eventIterator) {
		while (eventIterator.hasNext()) {
			Event event = eventIterator.nextEvent();
			try {
				String path = event.getPath();
				log.info("*** Resource Path ***{}", path);
			} catch (RepositoryException e) {

				e.printStackTrace();
			}

		}

	}

}
