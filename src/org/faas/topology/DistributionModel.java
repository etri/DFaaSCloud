package org.faas.topology;

import java.util.ArrayList;
import java.util.List;

public class DistributionModel {

	private String className;
	// CWE-495, CWE-496 private->public
	public List<DistributionParameter> parameterList = new ArrayList<DistributionParameter>();
	
	public String getClassName() {
		return className;
	}
	
	public void initClassName(Class classObj) {
		setClassName(classObj.getName());
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	public List<DistributionParameter> getParameterList() {
		return parameterList;
	}
	public void setParameterList(List<DistributionParameter> parameterList) {
		this.parameterList = parameterList;
	}

	public void addParameter(DistributionParameter param) {
		parameterList.add(param);
	}
	
	public void clearParameters() {
		parameterList.clear();
	}
}
