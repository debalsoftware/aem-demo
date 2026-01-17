package com.aem.demo.core.configurations;

import com.adobe.granite.jmx.annotation.Description;

/**
 * 
 * @author debal
 *
 */

@Description("Input WorkflowmodelId to read the workflow instances")
public interface WorkflowInfo {
	
	@Description("Enter the WorkflowmodelId")
	String getWorkflowInfo(String workflowModelId);

}
