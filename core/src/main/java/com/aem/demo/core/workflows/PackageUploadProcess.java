/*
 * package com.aem.demo.core.workflows;
 * 
 * import java.io.File; import java.io.IOException;
 * 
 * import javax.jcr.RepositoryException; import javax.jcr.Session;
 * 
 * import org.apache.jackrabbit.vault.fs.io.ImportOptions; import
 * org.apache.jackrabbit.vault.packaging.JcrPackage; import
 * org.apache.jackrabbit.vault.packaging.JcrPackageManager; import
 * org.apache.jackrabbit.vault.packaging.PackageException; import
 * org.apache.jackrabbit.vault.packaging.PackageManager; import
 * org.apache.jackrabbit.vault.packaging.Packaging; import
 * org.apache.sling.api.resource.Resource; import
 * org.apache.sling.api.resource.ResourceResolver; import
 * org.osgi.framework.Constants; import
 * org.osgi.service.component.annotations.Component; import
 * org.osgi.service.component.annotations.Reference; import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * import com.adobe.granite.workflow.WorkflowException; import
 * com.adobe.granite.workflow.WorkflowSession; import
 * com.adobe.granite.workflow.exec.WorkItem; import
 * com.adobe.granite.workflow.exec.WorkflowProcess; import
 * com.adobe.granite.workflow.metadata.MetaDataMap; import
 * com.day.cq.replication.ReplicationActionType; import
 * com.day.cq.replication.ReplicationException; import
 * com.day.cq.replication.Replicator;
 * 
 * @Component(property = { Constants.SERVICE_DESCRIPTION +
 * "=This workflow step is responsible to upload AEM Package",
 * Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" +
 * "=Package Upload process" }) public class PackageUploadProcess implements
 * WorkflowProcess {
 * 
 * private final Logger logger =
 * LoggerFactory.getLogger(PackageUploadProcess.class);
 * 
 * @Reference private Packaging packaging;
 * 
 * @Reference Replicator replicator;
 * 
 * @Override public void execute(WorkItem workItem, WorkflowSession
 * workflowSession, MetaDataMap metaDataMap) throws WorkflowException { String
 * initialpath = "http:\\\\\\\\localhost:7070\\crx\\packmgr\\service\\.json";
 * String payloadPath = workItem.getWorkflowData().getPayload().toString();
 * 
 * logger.info("***** Payload Path ***** {}", payloadPath);
 * 
 * ResourceResolver resourceResolver =
 * workflowSession.adaptTo(ResourceResolver.class);
 * 
 * ImportOptions importOption = new ImportOptions();
 * 
 * Resource resource = resourceResolver.getResource(payloadPath);
 * 
 * logger.info("***** Resource Name ***** {}", resource.getName()); Session
 * session = resourceResolver.adaptTo(Session.class);
 * 
 * PackageManager packageManager = packaging.getPackageManager();
 * packageManager.a
 * 
 * JcrPackageManager jcrPackageManager = packaging.getPackageManager(session);
 * try {
 * 
 * 
 * String compatiblefilepath = payloadPath.replace("/", "\\"); String
 * completepath = initialpath.concat(compatiblefilepath); File file = new
 * File(completepath); logger.info("***** File Path ***** {}", file.getPath());
 * 
 * 
 * 
 * JcrPackage jcrPackage = jcrPackageManager.upload(file, false, true,
 * resource.getName()); importOption.setStrict(false);
 * jcrPackage.install(importOption); String path =
 * jcrPackage.getNode().getPath(); logger.info("***** Package Path ***** {}",
 * path); replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
 * 
 * } catch (RepositoryException | IOException e) {
 * 
 * e.printStackTrace(); } catch (PackageException e) {
 * 
 * e.printStackTrace(); } catch (ReplicationException e) {
 * 
 * e.printStackTrace(); }
 * 
 * }
 * 
 * }
 */