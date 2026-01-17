/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections.Predicate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.predicate.AbstractNodePredicate;

/**
 * 
 * @author debal 
 * This Implementation class will help content authors to select
 * only image/video using touch ui pathbrowser
 */

@Component(service = Predicate.class, property = { "predicate.name=my-imagevideopredicate" })
public class ImageVideoAssetPredicate extends AbstractNodePredicate {
	private final static Logger log = LoggerFactory.getLogger(ImageVideoAssetPredicate.class);

	@Override
	public final boolean evaluate(final Node node) {

		if (Objects.nonNull(node)) {

			try {
				String nodeName = node.getName();
				log.info("**** Node name **** {}", nodeName);
				if (node.isNodeType("dam:Asset") && nodeName.indexOf(".") >= 0) {
					String extension = nodeName.substring(nodeName.lastIndexOf("."), nodeName.length());

					if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")
							|| extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".mp4")) {
						return true;
					}

				}
			} catch (RepositoryException re) {

				log.error("**** Unable to read node name****{}", re.getMessage());
			}

		}

		return false;

	}

}
