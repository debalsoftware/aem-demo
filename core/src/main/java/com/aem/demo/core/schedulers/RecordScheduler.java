
package com.aem.demo.core.schedulers;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = RecordScheduler.class, configurationPolicy = ConfigurationPolicy.REQUIRE)

@Designate(ocd = RecordScheduler.Config.class)
public class RecordScheduler implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecordScheduler.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private Scheduler scheduler;
	private int schedulerID;
	private boolean serviceEnabled;
	private String schedulerExpression;
	private String baseDamPath;
	private String xmlFolderPath;
	private String sftpDomainName;
	private int sftpPortNumber;
	private String sftpUserName;
	private String sftpPassword;

	private static final String RECORD_SCHEDULER_NAME = "RecordXMLScheduler";

	@ObjectClassDefinition(name = "Record Scheduler Configuration")
	public @interface Config {

		@AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
		boolean serviceEnabled() default true;

		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run on every Saturday 2:00 AM.", type = AttributeType.STRING)
		String schedulerExpression() default "0 0 2 1/1 * ? *";

		@AttributeDefinition(name = "Base Dam Path", description = "An XML will be generated for all the ginRecords in this path", type = AttributeType.STRING)
		String baseDamPath() default "/content/dam/cms-commons/sigmaaldrich/product/documents";

		@AttributeDefinition(name = "XML Folder Path", description = "An XML will be generated for all the ginRecords in this path", type = AttributeType.STRING)
		String xmlFolderPath() default "/ora_gpr/gpr/import_gprp/xml/";

		@AttributeDefinition(name = "SFTP Domain Name", type = AttributeType.STRING)
		String sftpDomainName() default "deda1x2746.merckgroup.com";

		@AttributeDefinition(name = "SFTP Port Number", type = AttributeType.INTEGER)
		int sftpPortNumber() default 22;

		@AttributeDefinition(name = "SFTP User name", type = AttributeType.STRING)
		String sftpUserName() default "gpr_admin";

		@AttributeDefinition(name = "SFTP Password", type = AttributeType.PASSWORD)
		String sftpPassword() default "gpr_2017";
	}

	@Activate
	protected void activate(Config config) {
		schedulerID = RECORD_SCHEDULER_NAME.hashCode();
		addScheduler(config);
	}

	@Modified
	protected void modified(Config config) {
		removeScheduler();
		schedulerID = RECORD_SCHEDULER_NAME.hashCode(); // update schedulerID
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(Config config) {
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */

	private void removeScheduler() {
		LOGGER.info("Removing Scheduler Job: {}", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(Config config) {
		this.serviceEnabled = config.serviceEnabled();
		if (this.serviceEnabled) {
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			this.schedulerExpression = config.schedulerExpression();
			this.baseDamPath = config.baseDamPath();
			this.xmlFolderPath = config.xmlFolderPath();
			this.sftpDomainName = config.sftpDomainName();
			this.sftpUserName = config.sftpUserName();
			this.sftpPassword = config.sftpPassword();
			this.sftpPortNumber = config.sftpPortNumber();
			LOGGER.info("Scheduler added successfully");
		} else {
			LOGGER.error("");
		}
	}

	@Override
	public void run() {
		LOGGER.info("*** Folder Path *** {}", xmlFolderPath);
		LOGGER.info("*** Running ***");
	}
}
