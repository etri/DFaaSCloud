package org.faas.topology;

import java.util.ArrayList;
import java.util.List;

import org.faas.DFaaSConstants;
import org.faas.SimulationManager;
import org.faas.topology.test.FunctionProfileTest;
import org.faas.utils.JsonUtil;

public class FunctionProfileList {
	// CWE-495, CWE-496 private -> public
	public List<FunctionProfile> functionProfileList = new ArrayList<FunctionProfile>();

	public List<FunctionProfile> getFunctionProfileList() {
		return functionProfileList;
	}

	public void setFunctionProfileList(List<FunctionProfile> functionProfileList) {
		this.functionProfileList = functionProfileList;
	}
	
	public void addFunctionProfile(FunctionProfile profile) {
		functionProfileList.add(profile);
	}
	
	public void deleteFunctionProfile(FunctionProfile profile) {
		functionProfileList.remove(profile);
	}
	
	private static FunctionProfileList me;
	public static final String fileName = "fp_list.json";

	public static FunctionProfileList getInstance() {
		if (me == null) {
			me = (FunctionProfileList)JsonUtil.read(FunctionProfileList.class, SimulationManager.getConfigPath()+"/"+fileName);
		}
		return me;
	}
	
	public static void load() {
		me = (FunctionProfileList)JsonUtil.read(FunctionProfileList.class, SimulationManager.getConfigPath()+"/"+fileName);
	}
	
	public static boolean save() {
		return JsonUtil.write(me, SimulationManager.getConfigPath()+"/"+fileName);
	}
	
	public static void main(String[] args) {
		
		List<FunctionProfile> fpList = new ArrayList<FunctionProfile>();
		
		FunctionProfile fp = FunctionProfileTest.packFunctionProfile();

		fp.setFunctionGrade(DFaaSConstants.CORE_NODE_GROUP);
		fp.setName("function profile1");
		fpList.add(fp);
		
		fp = FunctionProfileTest.packFunctionProfile();
		
		fp.setFunctionGrade(DFaaSConstants.EDGE_NODE_GROUP);
		fp.setName("function profile2");
		fpList.add(fp);
		
		FunctionProfileList functionProfileList = new FunctionProfileList();
		functionProfileList.setFunctionProfileList(fpList);
		
		//
		//
		//
		
		
		JsonUtil.write(functionProfileList, SimulationManager.getConfigPath()+"/"+fileName);
		
//		FunctionProfile fp2 = (FunctionProfile)JsonUtil.read(FunctionProfile.class, fileName);
//		
//		System.out.println(JsonUtil.toPrettyJsonString(fp2));

	}
}
