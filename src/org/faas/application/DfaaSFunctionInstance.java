package org.faas.application;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerVm;
import org.faas.AnalyticsEngine;
import org.faas.network.HIINetwork;
import org.faas.policy.FunctionAllocationPolicy;
import org.faas.topology.FunctionProfile;
import org.faas.topology.NetworkTopologyHelper;

public class DfaaSFunctionInstance extends PowerVm {

	private DFaaSFunctionInstanceInfo functionInfo;
	private FunctionProfile funtionProfile;
	
	/**
	 * 
	 * @param functionInfo
	 * @param id
	 * @param userId
	 * @param mips
	 * @param ram
	 * @param bw
	 * @param size 		storage size
	 * @param vmm 		virtual machine monitor.
	 * @param cloudletScheduler
	 */
	public DfaaSFunctionInstance(
			FunctionProfile functionProfile,
			DFaaSFunctionInstanceInfo functionInfo,
			int id,
			int userId,
			double mips,
			int ram,
			long bw,
			long size,
			String vmm,
			CloudletScheduler cloudletScheduler) {
		super(id, userId, mips, 1, ram, bw, size, 1, vmm, cloudletScheduler, 300);
		setId(id);
		setUserId(userId);
		setUid(getUid(userId, id));
		setMips(mips);
		setNumberOfPes(functionProfile.getComputingResourceRequirements().getNumberOfCpuCores()); // issue #36 두번째.
		setRam(ram);
		setBw(bw);
		setSize(size);
		setVmm(vmm);
		setCloudletScheduler(cloudletScheduler);
		setInMigration(false);
		setBeingInstantiated(true);
		setCurrentAllocatedBw(0);
		setCurrentAllocatedMips(null);
		setCurrentAllocatedRam(0);
		setCurrentAllocatedSize(0);

		this.funtionProfile = functionProfile;
		this.functionInfo = functionInfo;
	}
	
	public DFaaSFunctionInstanceInfo getFunctionInfo() {
		return functionInfo;
	}
	
	public void calcFunctionRunningDuration(FunctionAllocationPolicy allocationPolicy, int id) {
		functionInfo.setFunctionStartingTime(CloudSim.clock());
		
		double functionProcessingDuration= calcFunctionProcessingDuration(allocationPolicy);
		double inputDataReadingDuration = calcInputDataReadingDuration(id);
		double outputDataWritingDuration= calcOutputDataWritingDuration(id);
		
		functionInfo.setFunctionRunningDuration(
				functionProcessingDuration + inputDataReadingDuration + outputDataWritingDuration);
		
	}
	
	
	private double calcFunctionProcessingDuration(FunctionAllocationPolicy allocationPolicy) {
		// ppt page 17
		int functionMips = functionInfo.getFunctionUseProcessingMIsize();

		double time = allocationPolicy.calcProcessTime(functionMips) * 1000;
		functionInfo.setFunctionProcessingDuration(time);
		return time;
	}
	
	private double calcInputDataReadingDuration(int id) {
		List<DataLocationAndSize> inputDataList = functionInfo.getInputDataList();
		double totalDuration = 0;
		
		AnalyticsEngine analyticsEngine = AnalyticsEngine.getInstance();
		for (int i=0;i<inputDataList.size();i++) {
			DataLocationAndSize data = inputDataList.get(i);
			double size = data.getSize();
			double duration = HIINetwork.getInstance().calcNetworkDelay2(size, id, data.getNodeGroupId());
			analyticsEngine.updateInputDataReadingDurationStats(functionInfo, data.getType(), duration);
			
			totalDuration += duration;
		}
		functionInfo.setInputDataReadingDuration(totalDuration);

		return totalDuration;
	}
	
	private double calcOutputDataWritingDuration(int id) {
		List<DataLocationAndSize> outputDataList = functionInfo.getOutputDataList();
		double totalDuration = 0;
		
		AnalyticsEngine analyticsEngine = AnalyticsEngine.getInstance();
		for (int i=0;i<outputDataList.size();i++) {
			DataLocationAndSize data = outputDataList.get(i);
			double size = data.getSize();
			double duration = HIINetwork.getInstance().calcNetworkDelay2(size, id, data.getNodeGroupId());
			analyticsEngine.updateOutputDataReadingDurationStats(functionInfo, data.getType(), duration);
			
			totalDuration += duration;
		}
		functionInfo.setOutputDataWritingDuration(totalDuration);
		
		return totalDuration;
	}

	public void calFunctionRunningCost(int sourceNodeGroupId,DFaaSFunctionReqMsg reqMsg,DFaaSFunctionRspMsg rspMsg,double cpuCost) {
		calcComputingCost(cpuCost);
		calcInputDataNetworkingCost(reqMsg,sourceNodeGroupId);
		calcOutputDataNetworkingCost(rspMsg,sourceNodeGroupId);
	}
	
	/**
	 * ppt page 17 
	 * 
	 * computing cost = running duration * No. of core * function 실행 노드 그룹의 단위 CPU cost
	 * 
	 * @param cpuCost
	 */
	private void calcComputingCost(double cpuCost) {
		int cpuCores = NetworkTopologyHelper.getInstance().getFunctionProfile(functionInfo.getFunctionProfileId()).getComputingResourceRequirements().getNumberOfCpuCores();
		double duration = functionInfo.getFunctionRunningDuration() * cpuCores * cpuCost;
		
		functionInfo.setComputingCost(duration);
	}
	
	private void calcInputDataNetworkingCost(DFaaSFunctionReqMsg reqMsg,int id) {
		List<DataLocationAndSize> inputDataList = functionInfo.getInputDataList();
		double cost = 0;
		HIINetwork network = HIINetwork.getInstance();
		cost += network.calcNetworkCost(reqMsg.getMessageSize(),reqMsg.getEventSource().getId(),reqMsg.getEventSource().getParentId());
		for (int i=0;i<inputDataList.size();i++) {

			DataLocationAndSize data = inputDataList.get(i);
			cost += network.calcNetworkCost(data.getSize(),id, data.getNodeGroupId());
		}

		functionInfo.setInputDataNetworkingCost(cost);
	}
	
	private void calcOutputDataNetworkingCost(DFaaSFunctionRspMsg rspMsg,int id) {
		List<DataLocationAndSize> outputDataList = functionInfo.getOutputDataList();
		double cost = 0;
		HIINetwork network = HIINetwork.getInstance();
		cost += network.calcNetworkCost(rspMsg.getMessageSize(),rspMsg.getEventSink().getParentId(),rspMsg.getEventSink().getId());
		for (int i=0;i<outputDataList.size();i++) {
			
			DataLocationAndSize data = outputDataList.get(i);
			cost += network.calcNetworkCost(data.getSize(),id, data.getNodeGroupId());
		}

		functionInfo.setOutputDataNetworkingCost(cost);

	}
	
	/**
	 * Gets the current requested mips.
	 * 
	 * @return the current requested mips
	 */
	@Override
	public List<Double> getCurrentRequestedMips() {
		// function profile 에서 필요로 하는 mips를 pe 만큼 return.
		List<Double> mipsList = new ArrayList<Double>();
		
		for (int i=0;i<funtionProfile.getComputingResourceRequirements().getNumberOfCpuCores();i++) {
			//mipsList.add(0d);
			mipsList.add((double)functionInfo.getFunctionUseProcessingMIsize());
		}
		
		return mipsList;
	}
}
