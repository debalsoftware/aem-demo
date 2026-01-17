package com.aem.demo.core.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.formsndocuments.util.FMConstants;
import com.aem.demo.core.services.JcrUtility;
import com.aem.demo.core.services.ReportGenerationService;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.drew.lang.annotations.Nullable;

@Component(service = ReportGenerationService.class, immediate = true)
public class ReportGenerationServiceImpl implements ReportGenerationService {

	private final Logger logger = LoggerFactory.getLogger(ReportGenerationServiceImpl.class);
	@Reference
	JcrUtility jcrUtility;

	@Reference
	private QueryBuilder queryBuilder;

	private ResourceResolver resourceResolver;

	@Override
	public void getPageExpirationReport(String pagepath) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> datamap = new HashMap<String, Object>();
		List<String[]> data = new ArrayList<String[]>();
		data.add(new String[] { "Page Path", "Page Owner", "Expiry Time", "Replication Action" });
		try {
			resourceResolver = jcrUtility.getResourceResolver();
			// Resource resource = resourceResolver.getResource("/content/forms/portal");
			@Nullable
			Session session = resourceResolver.adaptTo(Session.class);
			if (Objects.nonNull(session)) {
				
			
				map.put("path", pagepath.trim());
				map.put("type", FMConstants.CQ_PAGE_NODETYPE);

				map.put("1_group.daterange.property", "jcr:content/expires");
				map.put("1_group.daterange.lowerBound", getCurrentDate());
				map.put("2_group.relativedaterange.property", "jcr:content/expires");
				map.put("2_group.relativedaterange.upperBound", "14d");
				map.put("p.limit", "-1");
				Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map), session);

				SearchResult searchResult = searchquery.getResult();
				Iterator<Resource> resources = searchResult.getResources();

				while (resources.hasNext()) {
					String replicationstatus = null;
					Date publishedDate = null;
					Resource searchResultResource = resources.next();

					String contentpath = searchResultResource.getPath();
					Resource childresource = searchResultResource.getChild(JcrConstants.JCR_CONTENT);

					ValueMap valueMap = childresource.adaptTo(ValueMap.class);
					
					
					String owner = valueMap.get(FMConstants.CQ_LAST_MODIFIED_BY, String.class);
					Date expiryTime = valueMap.get("expires", Date.class);
					if (valueMap.containsKey("cq:lastReplicationAction")) {
						 replicationstatus = valueMap.get("cq:lastReplicationAction", String.class);
					}
					else {
						replicationstatus = "Newly created page";
					}
					
					if (valueMap.containsKey("cq:lastReplicated")) {
						 
						 publishedDate = valueMap.get("cq:lastReplicated", Date.class);
					}
					
					data.add(new String[] {contentpath, owner, expiryTime.toString(),replicationstatus,publishedDate.toString()});

					try {
						writeDatatoCSV(data, resourceResolver);
					} catch (IOException e) {
						
						logger.error("Error details", e.getMessage());
						
					}

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		finally {
			jcrUtility.closeResourceResolver(resourceResolver);
		}
	}

	private String getCurrentDate() {
		LocalDate dateObj = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String date = dateObj.format(formatter);

		return date;

	}
	
	

	private void writeDatatoCSV(List<String[]> listdata, ResourceResolver resourceResolver) throws IOException {

		AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
		String fileName = "Page"+getCurrentDate()+".csv";
		File csvFile = new File(fileName);

		FileWriter fileWriter = new FileWriter(csvFile);
		for (String[] data : listdata) {
			StringBuilder line = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				line.append("\"");
				line.append(data[i].replaceAll("\"", "\"\""));
				line.append("\"");
				if (i != data.length - 1) {
					line.append(',');
				}
			}
			line.append("\n");
			fileWriter.write(line.toString());
		}
		fileWriter.close();
		InputStream fileInputStream = new FileInputStream(csvFile);
		assetManager.createAsset("/content/dam/we-retail/en/products/"+fileName, fileInputStream, null, true);

	}

}
