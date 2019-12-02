package org.faas.topology;

import java.util.ArrayList;
import java.util.List;

public class DataModel {

//	private int dataCount;
	// CWE-495(2), CWE-496(2) private -> public
	public List<DistributionModel> locationDistributionList = new ArrayList<DistributionModel>();
	public List<DistributionModel> dataSizeDistributionList = new ArrayList<DistributionModel>();
	
	public int dataCount() {
		//return dataCount;
		return locationDistributionList.size();
	}
//	public void setDataCount(int dataCount) {
//		this.dataCount = dataCount;
//	}
	public List<DistributionModel> getLocationDistributionList() {
		return locationDistributionList;
	}
	public void setLocationDistributionList(List<DistributionModel> locationDistributionList) {
		this.locationDistributionList = locationDistributionList;
	}
	public List<DistributionModel> getDataSizeDistributionList() {
		return dataSizeDistributionList;
	}
	public void setDataSizeDistributionList(List<DistributionModel> dataSizeDistributionList) {
		this.dataSizeDistributionList = dataSizeDistributionList;
	}

	public void addModel(DistributionModel locationDistributionModel, DistributionModel dataSizeDistributionModel) {
		locationDistributionList.add(locationDistributionModel);
		dataSizeDistributionList.add(dataSizeDistributionModel);
	}
	
	public void removeModel(DistributionModel locationDistributionModel, DistributionModel dataSizeDistributionModel) {
		locationDistributionList.remove(locationDistributionModel);
		dataSizeDistributionList.remove(dataSizeDistributionModel);
	}

	
}
