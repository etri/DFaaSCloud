package org.faas.application;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.faas.stats.collector.MonitoringDataCollector;
import org.faas.topology.DataModel;
import org.faas.topology.DistributionModel;
import org.faas.topology.FunctionProfile;
import org.faas.topology.NetworkTopologyHelper;

public class DFaaSFunctionRequester {
	private String functionProfileId;

	private ContinuousDistribution functionTrafficSementDist;
	private List<ContinuousDistribution> functionTrafficSementDists = new ArrayList<ContinuousDistribution>();

	private ContinuousDistribution functionRequestArrivalProcessDist;
	private List<ContinuousDistribution> functionRequestArrivalProcessDists = new ArrayList<ContinuousDistribution>();

	private ContinuousDistribution requestMessageSizeDist;
	private ContinuousDistribution responseMessageSizeDist;
	private FunctionProfile functionProfile;
	private List<DataDistPair> inputDataDistList = new ArrayList<DataDistPair>();
	private List<DataDistPair> outputDataDistList = new ArrayList<DataDistPair>();
	
	private ContinuousDistribution requiredMIPSDist;
	
	class DataDistPair {
		ContinuousDistribution locationDist;
		ContinuousDistribution sizeDist;
		
		DataDistPair(ContinuousDistribution locationDist, ContinuousDistribution sizeDist) {
			this.locationDist = locationDist;
			this.sizeDist = sizeDist;
		}
	}
	
	public DFaaSFunctionRequester(FunctionProfile functionProfile) {
		this.functionProfileId = functionProfile.getFunctionProfileId();
		this.functionProfile = functionProfile;
		
		for (int i=0;i<functionProfile.getFunctionTrafficSegmentModels().size();i++) {
			functionTrafficSementDists.add(NetworkTopologyHelper.instantiateDist(functionProfile.getFunctionTrafficSegmentModels().get(i)));
		}
		functionTrafficSementDist = functionTrafficSementDists.get(0);
		
		for (int i=0;i<functionProfile.getFunctionRequestArrivalProcessModels().size();i++) {
			functionRequestArrivalProcessDists.add(NetworkTopologyHelper.instantiateDist(functionProfile.getFunctionRequestArrivalProcessModels().get(i)));
		}
		functionRequestArrivalProcessDist = functionRequestArrivalProcessDists.get(0);//NetworkTopologyHelper.instantiateDist(functionProfile.getFunctionRequestArrivalProcessModel());
		
		requestMessageSizeDist = NetworkTopologyHelper.instantiateDist(functionProfile.getRequestMessageSizeModel());
		
		responseMessageSizeDist = NetworkTopologyHelper.instantiateDist(functionProfile.getResponseMessageSizeModel());

		DataModel inputDataModel = functionProfile.getInputDataModel();
		packDataDistPair(inputDataModel,inputDataDistList);
		
		DataModel outputDataModel = functionProfile.getOutputDataModel();
		packDataDistPair(outputDataModel,outputDataDistList);
		
		requiredMIPSDist = NetworkTopologyHelper.instantiateDist(functionProfile.getComputingResourceRequirements().getRequiredMipsModel());
	}
	
	private void packDataDistPair(DataModel dataModel,List<DataDistPair> dataDistList) {
		List<DistributionModel> locationDistributionList = dataModel.getLocationDistributionList();
		List<DistributionModel> dataSizeDistributionList = dataModel.getDataSizeDistributionList();
		
		for (int i=0;i<locationDistributionList.size();i++) {
			DistributionModel locationDistModel = locationDistributionList.get(i);
			DistributionModel dataSizeDistModel = dataSizeDistributionList.get(i);
			
			dataDistList.add(new DataDistPair(
				NetworkTopologyHelper.instantiateDist(locationDistModel),
				NetworkTopologyHelper.instantiateDist(dataSizeDistModel)
			));
		}
	}
	
	public double getDelayOfReqMsg() {
		// ppt page 4. function request arrival process model.
		return functionRequestArrivalProcessDist.sample();
	}
	
	private int currentTrafficSegment=0;
	public double getCurrentTrafficSegmentLength() {
		functionRequestArrivalProcessDist = functionRequestArrivalProcessDists.get(currentTrafficSegment);
		functionTrafficSementDist = functionTrafficSementDists.get(currentTrafficSegment);
		
		currentTrafficSegment++;
		if (currentTrafficSegment == this.functionRequestArrivalProcessDists.size()) {
			currentTrafficSegment=0;
		}
		
		return functionTrafficSementDist.sample() * 60 * 1000;
	}
	
	public DFaaSFunctionReqMsg getReqMsg() {

		DFaaSFunctionInstanceInfo functionInfo = new DFaaSFunctionInstanceInfo(functionProfile);
		
		functionInfo.setFunctionRequestTime(CloudSim.clock());
		functionInfo.setFunctionUseProcessingMIsize((int)requiredMIPSDist.sample());
		
		if (inputDataDistList.size() > 0) {
			List<DataLocationAndSize> list = new ArrayList<DataLocationAndSize>();
			for (int i=0;i<inputDataDistList.size();i++) {
				DataDistPair dataDistPair = inputDataDistList.get(i);
				double size = dataDistPair.sizeDist.sample();
				list.add(new DataLocationAndSize((int)dataDistPair.locationDist.sample(), size));
			}
			functionInfo.setInputDataList(list);
		}
		
		if (outputDataDistList.size() > 0) {
			List<DataLocationAndSize> list = new ArrayList<DataLocationAndSize>();
			for (int i=0;i<outputDataDistList.size();i++) {
				DataDistPair dataDistPair = outputDataDistList.get(i);
				double size = dataDistPair.sizeDist.sample();
				list.add(new DataLocationAndSize((int)dataDistPair.locationDist.sample(), size));
			}
			functionInfo.setOutputDataList(list);
		}
		
		double reqMessageSize = requestMessageSizeDist.sample();
		functionInfo.setFunctionRequestMessageSize(reqMessageSize);
		
		double rspMessageSize = responseMessageSizeDist.sample();
		functionInfo.setFunctionResponseMessageSize(rspMessageSize);
		
		DFaaSFunctionReqMsg msg = new DFaaSFunctionReqMsg(functionProfileId, reqMessageSize,functionInfo);
		
		MonitoringDataCollector.getInstance().increaseRequestedFunctionCount(CloudSim.clock());

		return msg;
	}
	
	public FunctionProfile getFunctionProfile() {
		return functionProfile;
	}
}
