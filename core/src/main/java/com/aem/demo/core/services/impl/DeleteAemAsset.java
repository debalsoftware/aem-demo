package com.aem.demo.core.services.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class DeleteAemAsset {

	public static void main(String[] args) throws IOException {

		URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/adobe-india");
		String credToEncode = "admin" + ":" + "admin";
		String encrypted = Base64.getEncoder().encodeToString(credToEncode.getBytes());
		HttpURLConnection httpUrlconnection = (HttpURLConnection) url.openConnection();
		httpUrlconnection.setDoOutput(true);
		httpUrlconnection.setRequestMethod("DELETE");
		httpUrlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpUrlconnection.setRequestProperty("Authorization", "Basic " + encrypted);

		System.out.println(httpUrlconnection.getResponseCode());
		httpUrlconnection.disconnect();

	}

}
