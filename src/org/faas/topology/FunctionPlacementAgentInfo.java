package org.faas.topology;

import java.util.List;

public class FunctionPlacementAgentInfo {

	private String className;
	// CWE-495, CWE-496 private -> public
	public List parameterValues;
	
	public FunctionPlacementAgentInfo() {}
	
	public FunctionPlacementAgentInfo(String className, List parameterValues) {
		this.className = className;
		this.parameterValues = parameterValues;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List parameterValues) {
		this.parameterValues = parameterValues;
	}

	
}

