package com.aem.demo.core.servlets;

import java.io.File;
import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.jackrabbit.vault.fs.io.ImportOptions;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.demo.core.services.JcrUtility;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/packageUpload",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class PackgeUploadServlet extends SlingAllMethodsServlet {
	
	@Reference
	JcrUtility jcrUtility;
	
	@Reference
	private Packaging packaging;

	@Reference
	Replicator replicator;

	
	protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
			SlingHttpServletResponse slingHttpServletResponse) {
		String initialpath = "C:\\Users\\debal\\OneDrive\\Desktop\\PIC\\test-component.zip";
		//String payloadPath = "/etc/packages/my_packages/demo-component-1.0.zip";
           
		

		ResourceResolver resourceResolver = jcrUtility.getResourceResolver();
		
		ImportOptions importOption = new ImportOptions();

		//Resource resource = resourceResolver.getResource(payloadPath);

		
		Session session = resourceResolver.adaptTo(Session.class);

		JcrPackageManager jcrPackageManager = packaging.getPackageManager(session);
		try {
			
			
			//String compatiblefilepath = payloadPath.replace("/", "\\");
		//	String completepath = initialpath.concat(payloadPath);
			File file = new File(initialpath);
			

			JcrPackage jcrPackage = jcrPackageManager.upload(file, false, true, "test-component.zip");
			importOption.setStrict(false);
			jcrPackage.install(importOption);
			String path = jcrPackage.getNode().getPath();
			
			replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
			slingHttpServletResponse.getWriter().println("Uploaded");

		} catch (RepositoryException | IOException e) {

			e.printStackTrace();
		} catch (PackageException e) {

			e.printStackTrace();
		} catch (ReplicationException e) {

			e.printStackTrace();
		}

	}


}
