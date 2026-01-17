/**
 * 
 */
package com.aem.demo.core.services.impl;

import org.osgi.service.component.annotations.Component;

import com.aem.demo.core.services.AsianGamesService;

/**
 * @author debal
 *
 */
@Component(service = AsianGamesService.class , immediate = true)
public class AsianGamesServiceImpl implements AsianGamesService {

	@Override
	public String getSuccessMessage() {
		String message = "India reached a historic milestone at the Asian Games 2023, securing an unprecedented tally of over 100 medals";
		return message;
	}

}
