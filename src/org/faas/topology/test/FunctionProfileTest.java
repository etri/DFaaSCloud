package org.faas.topology.test;

import org.faas.DFaaSConstants;
import org.faas.topology.*;
import org.faas.utils.JsonUtil;
import org.faas.utils.Util;
import org.faas.utils.distribution.LocationDistribution;
import org.faas.utils.distribution.UniformDistribution;

public class FunctionProfileTest {

	public static FunctionProfile packFunctionProfile() {
		FunctionProfile fp = new FunctionProfile();
		
		fp.setFunctionProfileId(Util.getUUID());

		fp.setFunctionGrade(DFaaSConstants.CORE_NODE_GROUP);
		fp.setViolationUnitCost(10.9);
		fp.setMaximumCompletionDuration(130);
		
		// computingResourceRequirements
		
		ComputingResourceRequirements computingResourceRequirements = new ComputingResourceRequirements();
		
		computingResourceRequirements.setNumberOfCpuCores(3);
		computingResourceRequirements.setMemorySize(10);
		
		DistributionModel requiredMipsModel = new DistributionModel();
		
		requiredMipsModel.setClassName("org.fog.utils.distribution.NormalDistribution");
		requiredMipsModel.addParameter(new DistributionParameter("mean",4000.0));
		requiredMipsModel.addParameter(new DistributionParameter("stdDev",1000.0));
		
		computingResourceRequirements.setRequiredMipsModel(requiredMipsModel);
		
		fp.setComputingResourceRequirements(computingResourceRequirements);
		
		//function request arrival process model
		
		DistributionModel functionRequestArrivalProcessModel = new DistributionModel();
		
		functionRequestArrivalProcessModel.setClassName("org.fog.utils.distribution.NormalDistribution");
		functionRequestArrivalProcessModel.addParameter(new DistributionParameter("mean",50.0));
		functionRequestArrivalProcessModel.addParameter(new DistributionParameter("stdDev",10.0));
		
		fp.setFunctionRequestArrivalProcessModel(functionRequestArrivalProcessModel);
		
		
		// requestMessageSizeModel
		DistributionModel requestMessageSizeModel = new DistributionModel();
		
		requestMessageSizeModel.initClassName(UniformDistribution.class);
		requestMessageSizeModel.addParameter(new DistributionParameter("min",90.0));
		requestMessageSizeModel.addParameter(new DistributionParameter("max",100.0));
		
		fp.setRequestMessageSizeModel(requestMessageSizeModel);
		
		// responseMessageSizeModel
		DistributionModel responseMessageSizeModel = new DistributionModel();
		
		responseMessageSizeModel.initClassName(UniformDistribution.class);
		responseMessageSizeModel.addParameter(new DistributionParameter("min",40.0));
		responseMessageSizeModel.addParameter(new DistributionParameter("max",50.0));
		
		fp.setResponseMessageSizeModel(responseMessageSizeModel);
		
		// inputDataModel
		
		DataModel inputDataModel = new DataModel();
		DistributionModel locationDistributionModel = new DistributionModel();
		DistributionModel dataSizeDistributionModel = new DistributionModel();
		locationDistributionModel.initClassName(LocationDistribution.class);
		locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.4));
		locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.3));
		locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("userDist",0.15));

		dataSizeDistributionModel.initClassName(UniformDistribution.class);
		dataSizeDistributionModel.addParameter(new DistributionParameter("min",1.0));
		dataSizeDistributionModel.addParameter(new DistributionParameter("max",10.0));
		
		inputDataModel.addModel(locationDistributionModel, dataSizeDistributionModel);
		fp.setInputDataModel(inputDataModel);

		// outputDataModel
		
		DataModel outputDataModel = new DataModel();
		locationDistributionModel = new DistributionModel();
		dataSizeDistributionModel = new DistributionModel();
		locationDistributionModel.initClassName(LocationDistribution.class);
		locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.3));
		locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("userDist",0.4));

		dataSizeDistributionModel.initClassName(UniformDistribution.class);
		dataSizeDistributionModel.addParameter(new DistributionParameter("min",1.0));
		dataSizeDistributionModel.addParameter(new DistributionParameter("max",10.0));
		
		outputDataModel.addModel(locationDistributionModel, dataSizeDistributionModel);
		
		locationDistributionModel = new DistributionModel();
		dataSizeDistributionModel = new DistributionModel();
		locationDistributionModel.initClassName(LocationDistribution.class);
		locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.3));
		locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("userDist",0.4));

		dataSizeDistributionModel.initClassName(UniformDistribution.class);
		dataSizeDistributionModel.addParameter(new DistributionParameter("min",10.0));
		dataSizeDistributionModel.addParameter(new DistributionParameter("max",60.0));
		
		outputDataModel.addModel(locationDistributionModel, dataSizeDistributionModel);
		
		fp.setOutputDataModel(outputDataModel);

		//
		
		return fp;
	}
	
	public static void main(String []argv) {
		
		FunctionProfile fp = FunctionProfileTest.packFunctionProfile();
		
		//
		//
		//
		
		String fileName = "json/fp";
		
		JsonUtil.write(fp, fileName);
		
		FunctionProfile fp2 = (FunctionProfile)JsonUtil.read(FunctionProfile.class, fileName);
		
		System.out.println(JsonUtil.toPrettyJsonString(fp2));
	}
	
}
