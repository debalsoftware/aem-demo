/**
 * 
 */
/*
 * package com.aem.demo.core.workflows;
 * 
 * import java.util.Date; import java.util.Objects;
 * 
 * import org.apache.commons.mail.Email; import
 * org.apache.commons.mail.EmailException; import
 * org.apache.commons.mail.SimpleEmail; import
 * org.apache.jackrabbit.vault.util.JcrConstants; import
 * org.apache.sling.api.resource.Resource; import
 * org.apache.sling.api.resource.ResourceResolver; import
 * org.apache.sling.api.resource.ValueMap; import org.osgi.framework.Constants;
 * import org.osgi.service.component.annotations.Component; import
 * org.osgi.service.component.annotations.Reference; import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * import com.adobe.granite.workflow.WorkflowException; import
 * com.adobe.granite.workflow.WorkflowSession; import
 * com.adobe.granite.workflow.exec.WorkItem; import
 * com.adobe.granite.workflow.exec.WorkflowProcess; import
 * com.adobe.granite.workflow.metadata.MetaDataMap; import
 * com.day.cq.mailer.MessageGateway; import
 * com.day.cq.mailer.MessageGatewayService; import
 * com.day.cq.mailer.email.EmailTemplate; import
 * com.drew.lang.annotations.Nullable;
 * 
 *//**
	 * @author debal This workflow process step is responsible to send email
	 *         notification with payload (content) path and expiry datetime details
	 *
	 *//*
		 * @Component(property = { Constants.SERVICE_DESCRIPTION +
		 * "=This workflow step is responsible to send Email Notification",
		 * Constants.SERVICE_VENDOR + "=AEM Demo Debal", "process.label" +
		 * "=Email Notification process" }) public class ContentExpiryNotificationStep
		 * implements WorkflowProcess {
		 * 
		 * private final Logger logger =
		 * LoggerFactory.getLogger(ContentExpiryNotificationStep.class);
		 * 
		 * 
		 * @Reference EmailTemplate<Email> emailTemplate;
		 * 
		 * @Reference MessageGatewayService messageGatewayService;
		 * 
		 * @Override public void execute(WorkItem workItem, WorkflowSession
		 * workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		 * 
		 * String payLoadPath = workItem.getWorkflowData().getPayload().toString();
		 * logger.info("*** Payload path *** {}", payLoadPath);
		 * 
		 * @Nullable ResourceResolver resourceResolver =
		 * workflowSession.adaptTo(ResourceResolver.class); if
		 * (Objects.nonNull(resourceResolver)) { String resourceContentpath =
		 * payLoadPath.concat("/").concat(JcrConstants.JCR_CONTENT);
		 * 
		 * logger.info("*** Resource Content Path *** {}", resourceContentpath);
		 * 
		 * @Nullable Resource resource =
		 * resourceResolver.getResource(resourceContentpath); if
		 * (Objects.nonNull(resource)) {
		 * 
		 * @Nullable ValueMap resourceValueMap = resource.adaptTo(ValueMap.class); if
		 * (Objects.nonNull(resourceValueMap)) {
		 * 
		 * @Nullable Date resourceOffedate = resourceValueMap.get("offTime",
		 * Date.class); logger.info("*** Content Expiration Date  *** {}",
		 * resourceOffedate); if (Objects.nonNull(resourceOffedate)) {
		 * sendMail(payLoadPath, resourceOffedate); }
		 * 
		 * 
		 * } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * private void sendMail(String payLoadPath, Date expiryDate) {
		 * 
		 * MessageGateway<Email> messageGateway; Email email = new SimpleEmail();
		 * StringBuilder sb = new StringBuilder();
		 * 
		 * String emailToRecipients = "debal.india2014@gmail.com";
		 * 
		 * try { email.addTo(emailToRecipients);
		 * email.setSubject("AEM Content Expiration Notofication");
		 * email.setFrom("debal.india2016@gmail.com"); email.setMsg("*********" +
		 * sb.append("Notification Message details***********").append(payLoadPath)
		 * .append(" ").append(expiryDate));
		 * 
		 * messageGateway = messageGatewayService.getGateway(Email.class);
		 * 
		 * messageGateway.send((Email) email);
		 * 
		 * } catch (EmailException e) {
		 * 
		 * logger.error("SMTP issue", e.getMessage()); }
		 * 
		 * }
		 * 
		 * }
		 */