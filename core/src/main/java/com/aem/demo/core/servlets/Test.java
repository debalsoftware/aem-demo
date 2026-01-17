package com.aem.demo.core.servlets;

import java.io.UnsupportedEncodingException;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String myString = "Спасибо";
		byte bytes[] = myString.getBytes("UTF-8"); 
		String value = new String(bytes, "UTF-8");
		System.out.println(value);

	}

}
