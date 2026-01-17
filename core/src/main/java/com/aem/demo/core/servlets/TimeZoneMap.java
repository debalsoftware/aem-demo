package com.aem.demo.core.servlets;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class TimeZoneMap {
	public static void main(String[] args) {
	    
	        // Get all available Zone IDs
	        Set<String> zoneIds = ZoneId.getAvailableZoneIds();

	        // TreeSet to store unique short names (to avoid duplicates and sort them)
	        Set<String> shortNames = new TreeSet<>();

	        // Iterate through each time zone ID
	        for (String zoneId : zoneIds) {
	            ZoneId zone = ZoneId.of(zoneId);
	            ZonedDateTime now = ZonedDateTime.now(zone);

	            // Get the short name (abbreviation) for the time zone
	            String shortName = now.getZone().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
	            shortNames.add(shortName);
	        }

	        // Display the list of unique short names
	        System.out.println("List of Time Zone Short Names:");
	        shortNames.forEach(System.out::println);
	    }
}

