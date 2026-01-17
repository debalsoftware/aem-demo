package com.aem.demo.core.models;

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
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleModel {

	
	@ValueMapValue
	private String articleId;

	@ValueMapValue (name = "cq:panelTitle")
	private String panel;

	
	@SlingObject
	SlingHttpServletRequest slingHttpServletRequest;
	
	@PostConstruct
	protected void init() {
        if (Objects.nonNull(articleId)) {
        	
        	panel = articleId;
    		Node currentnode = slingHttpServletRequest.getResource().adaptTo(Node.class);

    		Session session = slingHttpServletRequest.getResourceResolver().adaptTo(Session.class);
    		try {
    			currentnode.setProperty("cq:panelTitle", panel);
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

	/**
	 * @return the articleId
	 */
	public String getArticleId() {
		return articleId;
	}

	/**
	 * @return the panel
	 */
	public String getPanel() {
		return panel;
	}

}
