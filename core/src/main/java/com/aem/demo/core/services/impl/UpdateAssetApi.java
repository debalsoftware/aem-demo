package com.aem.demo.core.services.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAssetApi {

	public static void main(String[] args) throws IOException, JSONException {
		URL url = new URL ("http://localhost:7070/api/assets/we-retail/en/activities/hiking-camping/men-debal-one-test-bng");
	//	String credToEncode = "admin" + ":" + "admin";
		
		String credToEncode = "debal-bangalore" + ":" + "{da3c9f6437bfc88152c379cbfac93e81563c62248562155eefd9ef5ef133da3d}";
		String encrypted = Base64.getEncoder().encodeToString(credToEncode.getBytes());
		HttpURLConnection httpUrlconnection = (HttpURLConnection)url.openConnection();
		httpUrlconnection.setDoOutput(true);
		httpUrlconnection.setRequestMethod("PUT");
		httpUrlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpUrlconnection.setRequestProperty("Authorization", "Basic " + encrypted);
		
		/*
		 * JSONObject jsonObject = new JSONObject(); jsonObject.put("city",
		 * "Bangalore");
		 */
		
		
		
		
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpUrlconnection.getOutputStream());
		outputStreamWriter.write("Bangalore");
		outputStreamWriter.flush();
		
		
		
		
		
		
		System.out.println(httpUrlconnection.getResponseCode());
		httpUrlconnection.disconnect();
		
		
		

	}

}
