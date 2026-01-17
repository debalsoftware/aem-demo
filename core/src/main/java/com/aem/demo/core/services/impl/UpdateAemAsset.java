package com.aem.demo.core.services.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

public class UpdateAemAsset {

	public static void main(String[] args) {

		try {
			//URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/hiking_2.jpg");
			URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/Adobe-logo.png");
			String credToEncode = "admin" + ":" + "admin";
			String encrypted = Base64.getEncoder().encodeToString(credToEncode.getBytes());
			HttpURLConnection httpUrlconnection = (HttpURLConnection) url.openConnection();
			httpUrlconnection.setDoOutput(true);
			httpUrlconnection.setRequestMethod("PUT");
			httpUrlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlconnection.setRequestProperty("Authorization", "Basic " + encrypted);

			String urlparam = "co-author=Debal Das";

			try (DataOutputStream dataOutputStream = new DataOutputStream(httpUrlconnection.getOutputStream())) {
				dataOutputStream.writeBytes(urlparam);

				dataOutputStream.flush();
				dataOutputStream.close();
				System.out.println(httpUrlconnection.getResponseCode() + "** Peoperty has been updated successfully**");

			} catch (IOException e) {

				e.printStackTrace();
			}

			httpUrlconnection.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (ProtocolException e) {

			e.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

	}

}
