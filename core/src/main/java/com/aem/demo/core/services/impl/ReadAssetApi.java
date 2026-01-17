package com.aem.demo.core.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReadAssetApi {

	public static void main(String[] args) {

		try {
			InputStream inputStream = null;
			//URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/hiking_2.jpg.json");
			URL url = new URL("http://localhost:4502/api/assets/we-retail/en/activities/hiking/Adobe-logo.png.json");
			String credToEncode = "admin" + ":" + "admin";
			String encrypted = Base64.getEncoder().encodeToString(credToEncode.getBytes());
			HttpURLConnection httpUrlconnection = (HttpURLConnection) url.openConnection();
			httpUrlconnection.setDoOutput(true);
			httpUrlconnection.setRequestMethod("GET");
			httpUrlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlconnection.setRequestProperty("Authorization", "Basic " + encrypted);

			int responseCode = httpUrlconnection.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);

			} else {
				inputStream = httpUrlconnection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder response = new StringBuilder();
				String currentLine;

				while ((currentLine = in.readLine()) != null) {
					response.append(currentLine);
				}
				in.close();
				httpUrlconnection.disconnect();
				JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
				String coauthor = jsonObject.getAsJsonObject("properties").get("co-author").getAsString();

				System.out.println(coauthor);

			}

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
