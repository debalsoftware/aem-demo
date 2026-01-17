/**
 * 
 */
package com.aem.demo.core.services.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.aem.demo.core.services.AsianGamesService;
import com.aem.demo.core.services.PrintService;

/**
 * @author debal
 *
 */
public class PrintServiceImpl implements PrintService {

	String displayMessage = StringUtils.EMPTY;
	
	@Override
	public String congratsMessage() {
		Bundle bundle = FrameworkUtil.getBundle(AsianGamesService.class);
		if (Objects.nonNull(bundle)) {
			BundleContext bundleContext = bundle.getBundleContext();
			ServiceReference<AsianGamesService> serviceReference = bundleContext.getServiceReference(AsianGamesService.class);
			AsianGamesService asianGamesService = (AsianGamesService)bundleContext.getService(serviceReference);
			displayMessage = asianGamesService.getSuccessMessage();
		}
		return displayMessage;
	}

}
