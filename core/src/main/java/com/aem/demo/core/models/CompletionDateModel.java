package com.aem.demo.core.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CompletionDateModel {

	@ValueMapValue
	private String noofdays;

	@ValueMapValue
	private Date startdate;

	@ValueMapValue
	private Date calculateddate;
	
	@RequestAttribute(name="fname")
	private String firstName;

	@SlingObject
	SlingHttpServletRequest slingHttpServletRequest;

	@PostConstruct
	protected void init() {
        if (Objects.nonNull(noofdays)) {
        	long userEnteredDate = Long.parseLong(noofdays);

    		LocalDate localStartDate = startdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    		LocalDate plusDays = localStartDate.plusDays(userEnteredDate);
    		calculateddate = Date.from(plusDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(calculateddate);

    		Node currentnode = slingHttpServletRequest.getResource().adaptTo(Node.class);

    		Session session = slingHttpServletRequest.getResourceResolver().adaptTo(Session.class);
    		try {
    			currentnode.setProperty("calculateddate", calendar);
    			session.save();
    		} catch (ValueFormatException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (VersionException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (LockException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ConstraintViolationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (RepositoryException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
			
		}
		

	}

	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the noofdays
	 */
	public String getNoofdays() {
		return noofdays;
	}

	/**
	 * @return the startdate
	 */
	public Date getStartdate() {
		return startdate;
	}

}
