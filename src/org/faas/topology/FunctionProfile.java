package org.faas.topology;

import java.util.ArrayList;
import java.util.List;

import org.faas.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.faas.utils.distribution.Distribution;


public class FunctionProfile {

	private String name;
	private String functionProfileId;
	private int functionGrade;
	private int maximumCompletionDuration; // in milli-secs
	private double violationUnitCost;
	private ComputingResourceRequirements computingResourceRequirements;

	@JsonIgnore
	private DistributionModel functionTrafficSegmentModel;
	// CWE-495, CWE-496 private -> public
	public List<DistributionModel> functionTrafficSegmentModels = new ArrayList<DistributionModel>();
	@JsonIgnore
	private DistributionModel functionRequestArrivalProcessModel;
	// CWE-495, CWE-496 private -> public
	public List<DistributionModel> functionRequestArrivalProcessModels = new ArrayList<DistributionModel>();
	
	private DistributionModel requestMessageSizeModel;
	private DistributionModel responseMessageSizeModel;
	private DataModel inputDataModel;
	private DataModel outputDataModel;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunctionProfileId() {
		return functionProfileId;
	}
	public void setFunctionProfileId(String functionProfileId) {
		this.functionProfileId = functionProfileId;
	}
	public int getFunctionGrade() {
		return functionGrade;
	}
	public void setFunctionGrade(int functionGrade) {
		this.functionGrade = functionGrade;
	}
	public int getMaximumCompletionDuration() {
		return maximumCompletionDuration;
	}
	public void setMaximumCompletionDuration(int maximumCompletionDuration) {
		this.maximumCompletionDuration = maximumCompletionDuration;
	}
	public void setViolationUnitCost(double violationUnitCost) {
		this.violationUnitCost = violationUnitCost;
	}
	public double getViolationUnitCost() {
		return violationUnitCost;
	}
	public ComputingResourceRequirements getComputingResourceRequirements() {
		return computingResourceRequirements;
	}
	public void setComputingResourceRequirements(ComputingResourceRequirements computingResourceRequirements) {
		this.computingResourceRequirements = computingResourceRequirements;
	}
	public List<DistributionModel> getFunctionRequestArrivalProcessModels() {
		return functionRequestArrivalProcessModels;
	}
	public void setFunctionRequestArrivalProcessModels(List<DistributionModel> functionRequestArrivalProcessModels) {
		this.functionRequestArrivalProcessModels = functionRequestArrivalProcessModels;
	}
	public DistributionModel getFunctionTrafficSegmentModel() {
		return functionTrafficSegmentModel;
	}
	public void setFunctionTrafficSegmentModel(DistributionModel functionTrafficSegmentModel) {
		this.functionTrafficSegmentModel = functionTrafficSegmentModel;
	}

	public List<DistributionModel> getFunctionTrafficSegmentModels() {
		return functionTrafficSegmentModels;
	}
	public void setFunctionTrafficSegmentModels(List<DistributionModel> functionTrafficSegmentModels) {
		this.functionTrafficSegmentModels = functionTrafficSegmentModels;
	}
	public void addFunctionTrafficSegmentModels(DistributionModel functionTrafficSegmentModels) {
		this.functionTrafficSegmentModels.add(functionTrafficSegmentModels);
	}
	public DistributionModel getRequestMessageSizeModel() {
		return requestMessageSizeModel;
	}
	public void setRequestMessageSizeModel(DistributionModel requestMessageSizeModel) {
		this.requestMessageSizeModel = requestMessageSizeModel;
	}
	public DistributionModel getResponseMessageSizeModel() {
		return responseMessageSizeModel;
	}
	public void setResponseMessageSizeModel(DistributionModel responseMessageSizeModel) {
		this.responseMessageSizeModel = responseMessageSizeModel;
	}
	public DataModel getInputDataModel() {
		return inputDataModel;
	}
	public void setInputDataModel(DataModel inputDataModel) {
		this.inputDataModel = inputDataModel;
	}
	public DataModel getOutputDataModel() {
		return outputDataModel;
	}
	public void setOutputDataModel(DataModel outputDataModel) {
		this.outputDataModel = outputDataModel;
	}
	
	public String toString() {
		if (name == null) return this.functionProfileId;
		return name;
	}
	
	public static FunctionProfile read(String fileName) {
		return (FunctionProfile)JsonUtil.read(FunctionProfile.class, fileName);
	}
	
	public static boolean save(FunctionProfile fp,String fileName) {
		return JsonUtil.write(fp, fileName);
	}
	public DistributionModel getFunctionRequestArrivalProcessModel() {
		return functionRequestArrivalProcessModel;
	}
	public void setFunctionRequestArrivalProcessModel(DistributionModel functionRequestArrivalProcessModel) {
		this.functionRequestArrivalProcessModel = functionRequestArrivalProcessModel;
	}
	public void addFunctionRequestArrivalProcessModel(DistributionModel functionRequestArrivalProcessModel) {
		this.functionRequestArrivalProcessModels.add(functionRequestArrivalProcessModel);
	}

	public FunctionProfile copy() {
		FunctionProfile newFunctionProfile = new FunctionProfile();
		newFunctionProfile.setName(this.name);
		newFunctionProfile.setFunctionProfileId(this.functionProfileId);
		newFunctionProfile.setFunctionGrade(this.functionGrade);
		newFunctionProfile.setMaximumCompletionDuration(this.maximumCompletionDuration);
		newFunctionProfile.setViolationUnitCost(this.violationUnitCost);

		// computingResourceRequirements
		if(this.computingResourceRequirements != null) {
			ComputingResourceRequirements tmpComputingResourceRequirements = new ComputingResourceRequirements();
			tmpComputingResourceRequirements.setNumberOfCpuCores(this.computingResourceRequirements.getNumberOfCpuCores());
			tmpComputingResourceRequirements.setMemorySize(this.computingResourceRequirements.getMemorySize());
			DistributionModel tmpRequiredMipsModel = new DistributionModel();
			tmpRequiredMipsModel.setClassName(this.computingResourceRequirements.getRequiredMipsModel().getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = this.computingResourceRequirements.getRequiredMipsModel().getParameterList();
			for (int i = 0; i < Parameters.size(); i++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(i).getName(), Parameters.get(i).getValue());
				tmpParameters.add(param);
			}
			tmpRequiredMipsModel.setParameterList(tmpParameters);
			tmpComputingResourceRequirements.setRequiredMipsModel(tmpRequiredMipsModel);
			newFunctionProfile.setComputingResourceRequirements(tmpComputingResourceRequirements);
		}

		// functionTrafficSegmentModels
		newFunctionProfile.getFunctionTrafficSegmentModels().clear();
		for (int i = 0; i < this.functionTrafficSegmentModels.size(); i++) {
			DistributionModel model = this.functionTrafficSegmentModels.get(i);
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(model.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = model.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			newFunctionProfile.getFunctionTrafficSegmentModels().add(tmpModel);
		}

		// functionRequestArrivalProcessModels
		newFunctionProfile.getFunctionRequestArrivalProcessModels().clear();
		for (int i = 0; i < this.functionRequestArrivalProcessModels.size(); i++) {
			DistributionModel model = this.functionRequestArrivalProcessModels.get(i);
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(model.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = model.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			newFunctionProfile.getFunctionRequestArrivalProcessModels().add(tmpModel);
		}
		// requestMessageSizeModel
		if(this.requestMessageSizeModel != null) {
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(this.requestMessageSizeModel.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = this.requestMessageSizeModel.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			newFunctionProfile.setRequestMessageSizeModel(tmpModel);
		}
		// responseMessageSizeModel
		if(this.responseMessageSizeModel != null) {
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(this.responseMessageSizeModel.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = this.responseMessageSizeModel.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			newFunctionProfile.setResponseMessageSizeModel(tmpModel);
		}

		// inputDataModel
		if(this.inputDataModel != null) {
			DataModel tmpDataModel = new DataModel();

			// locationDistributionModel, dataSizeDistributionModel must be same size;
			List<DistributionModel> locationDistributionModels = this.inputDataModel.getLocationDistributionList();
			List<DistributionModel> dataSizeDistributionModels = this.inputDataModel.getDataSizeDistributionList();
			for (int modelIdx = 0; modelIdx < locationDistributionModels.size(); modelIdx++) {
				DistributionModel locationModel = locationDistributionModels.get(modelIdx);
				DistributionModel tmpLocationModel = new DistributionModel();
				if(locationModel != null)
				{
					tmpLocationModel.setClassName(locationModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = locationModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpLocationModel.setParameterList(tmpParameters);
				}
				else
					tmpLocationModel = null;

				DistributionModel dataSizeModel = dataSizeDistributionModels.get(modelIdx);
				DistributionModel tmpDataSizeModel = new DistributionModel();
				if(dataSizeModel != null)
				{
					tmpDataSizeModel.setClassName(dataSizeModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = dataSizeModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpDataSizeModel.setParameterList(tmpParameters);
				}
				else
					tmpDataSizeModel = null;

				tmpDataModel.addModel(tmpLocationModel, tmpDataSizeModel);

			}
			newFunctionProfile.setInputDataModel(tmpDataModel);
		}

		// outputDataModel
		if(this.outputDataModel != null) {
			DataModel tmpDataModel = new DataModel();

			// locationDistributionModel, dataSizeDistributionModel must be same size;
			List<DistributionModel> locationDistributionModels = this.outputDataModel.getLocationDistributionList();
			List<DistributionModel> dataSizeDistributionModels = this.outputDataModel.getDataSizeDistributionList();
			for (int modelIdx = 0; modelIdx < locationDistributionModels.size(); modelIdx++) {
				DistributionModel locationModel = locationDistributionModels.get(modelIdx);
				DistributionModel tmpLocationModel = new DistributionModel();
				if(locationModel != null)
				{
					tmpLocationModel.setClassName(locationModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = locationModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpLocationModel.setParameterList(tmpParameters);
				}
				else
					tmpLocationModel = null;

				DistributionModel dataSizeModel = dataSizeDistributionModels.get(modelIdx);
				DistributionModel tmpDataSizeModel = new DistributionModel();
				if(dataSizeModel != null)
				{
					tmpDataSizeModel.setClassName(dataSizeModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = dataSizeModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpDataSizeModel.setParameterList(tmpParameters);
				}
				else
					tmpDataSizeModel = null;

				tmpDataModel.addModel(tmpLocationModel, tmpDataSizeModel);

			}
			newFunctionProfile.setOutputDataModel(tmpDataModel);
		}
		return newFunctionProfile;
	}

	public void from(FunctionProfile source) {
		this.name = source.getName();
		this.functionProfileId = source.getFunctionProfileId();
		this.functionGrade = source.getFunctionGrade();
		this.maximumCompletionDuration = source.getMaximumCompletionDuration();
		this.violationUnitCost = source.getViolationUnitCost();

		// computingResourceRequirements
		if(source.getComputingResourceRequirements() != null) {
			this.computingResourceRequirements = new ComputingResourceRequirements();
			this.computingResourceRequirements.setNumberOfCpuCores(source.getComputingResourceRequirements().getNumberOfCpuCores());
			this.computingResourceRequirements.setMemorySize(source.getComputingResourceRequirements().getMemorySize());
			DistributionModel tmpRequiredMipsModel = new DistributionModel();
			tmpRequiredMipsModel.setClassName(source.getComputingResourceRequirements().getRequiredMipsModel().getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = source.getComputingResourceRequirements().getRequiredMipsModel().getParameterList();
			for (int i = 0; i < Parameters.size(); i++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(i).getName(), Parameters.get(i).getValue());
				tmpParameters.add(param);
			}
			tmpRequiredMipsModel.setParameterList(tmpParameters);
			this.computingResourceRequirements.setRequiredMipsModel(tmpRequiredMipsModel);
		}

		// functionTrafficSegmentModels
		this.functionTrafficSegmentModels.clear();
		for (int i = 0; i < source.getFunctionTrafficSegmentModels().size(); i++) {
			DistributionModel model = source.getFunctionTrafficSegmentModels().get(i);
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(model.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = model.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);

			this.functionTrafficSegmentModels.add(tmpModel);
		}

		// functionRequestArrivalProcessModels
		this.functionRequestArrivalProcessModels.clear();
		for (int i = 0; i < source.getFunctionRequestArrivalProcessModels().size(); i++) {
			DistributionModel model = source.getFunctionRequestArrivalProcessModels().get(i);
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(model.getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = model.getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			this.functionRequestArrivalProcessModels.add(tmpModel);
		}
		// requestMessageSizeModel
		if(source.getRequestMessageSizeModel() != null) {
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(source.getRequestMessageSizeModel().getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = source.getRequestMessageSizeModel().getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			this.requestMessageSizeModel = tmpModel;
		}
		// responseMessageSizeModel
		if(source.getResponseMessageSizeModel() != null) {
			DistributionModel tmpModel = new DistributionModel();
			tmpModel.setClassName(source.getResponseMessageSizeModel().getClassName());
			List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
			List<DistributionParameter> Parameters = source.getResponseMessageSizeModel().getParameterList();
			for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
				DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
				tmpParameters.add(param);
			}
			tmpModel.setParameterList(tmpParameters);
			this.responseMessageSizeModel = tmpModel;
		}

		// inputDataModel
		if(source.getInputDataModel() != null) {
			DataModel tmpDataModel = new DataModel();

			// locationDistributionModel, dataSizeDistributionModel must be same size;
			List<DistributionModel> locationDistributionModels = source.getInputDataModel().getLocationDistributionList();
			List<DistributionModel> dataSizeDistributionModels = source.getInputDataModel().getDataSizeDistributionList();
			for (int modelIdx = 0; modelIdx < locationDistributionModels.size(); modelIdx++) {
				DistributionModel locationModel = locationDistributionModels.get(modelIdx);
				DistributionModel tmpLocationModel = new DistributionModel();
				if(locationModel != null)
				{
					tmpLocationModel.setClassName(locationModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = locationModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpLocationModel.setParameterList(tmpParameters);
				}
				else
					tmpLocationModel = null;

				DistributionModel dataSizeModel = dataSizeDistributionModels.get(modelIdx);
				DistributionModel tmpDataSizeModel = new DistributionModel();
				if(dataSizeModel != null)
				{
					tmpDataSizeModel.setClassName(dataSizeModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = dataSizeModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpDataSizeModel.setParameterList(tmpParameters);
				}
				else
					tmpDataSizeModel = null;

				tmpDataModel.addModel(tmpLocationModel, tmpDataSizeModel);

			}
			this.inputDataModel =  tmpDataModel;
		}

		// outputDataModel
		if(source.getOutputDataModel() != null) {
			DataModel tmpDataModel = new DataModel();

			// locationDistributionModel, dataSizeDistributionModel must be same size;
			List<DistributionModel> locationDistributionModels = source.getOutputDataModel().getLocationDistributionList();
			List<DistributionModel> dataSizeDistributionModels = source.getOutputDataModel().getDataSizeDistributionList();
			for (int modelIdx = 0; modelIdx < locationDistributionModels.size(); modelIdx++) {
				DistributionModel locationModel = locationDistributionModels.get(modelIdx);
				DistributionModel tmpLocationModel = new DistributionModel();
				if(locationModel != null)
				{
					tmpLocationModel.setClassName(locationModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = locationModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpLocationModel.setParameterList(tmpParameters);
				}
				else
					tmpLocationModel = null;

				DistributionModel dataSizeModel = dataSizeDistributionModels.get(modelIdx);
				DistributionModel tmpDataSizeModel = new DistributionModel();
				if(dataSizeModel != null)
				{
					tmpDataSizeModel.setClassName(dataSizeModel.getClassName());
					List<DistributionParameter> tmpParameters = new ArrayList<DistributionParameter>();
					List<DistributionParameter> Parameters = dataSizeModel.getParameterList();
					for (int paramIdx = 0; paramIdx < Parameters.size(); paramIdx++) {
						DistributionParameter param = new DistributionParameter(Parameters.get(paramIdx).getName(), Parameters.get(paramIdx).getValue());
						tmpParameters.add(param);
					}
					tmpDataSizeModel.setParameterList(tmpParameters);
				}
				else
					tmpDataSizeModel = null;

				tmpDataModel.addModel(tmpLocationModel, tmpDataSizeModel);

			}
			this.outputDataModel = tmpDataModel;
		}
	}
}
