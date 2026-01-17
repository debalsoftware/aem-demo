package com.aem.demo.core.services.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class CreateAssetApi {

	public static void main(String[] args) throws IOException {
		URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/*");

		String credToEncode = "admin" + ":" + "admin";
		String encrypted = Base64.getEncoder().encodeToString(credToEncode.getBytes());
		HttpURLConnection httpUrlconnection = (HttpURLConnection) url.openConnection();
		httpUrlconnection.setDoOutput(true);
		httpUrlconnection.setRequestMethod("POST");

		httpUrlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpUrlconnection.setRequestProperty("Authorization", "Basic " + encrypted);

		String urlparam = "name=adobe-india&title=Adobe India";

		try (DataOutputStream dataOutputStream = new DataOutputStream(httpUrlconnection.getOutputStream())) {
			dataOutputStream.writeBytes(urlparam);

			dataOutputStream.flush();
			dataOutputStream.close();
			System.out.println(httpUrlconnection.getResponseCode());

			httpUrlconnection.disconnect();

		}

		catch (IOException e) {

			e.printStackTrace();
		}

	}

}
