package org.faas.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.faas.DFaaSConstants;
import org.faas.SimulationConfig;
import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.stats.DataDurationStats;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.stats.StatValue;
import org.faas.topology.FunctionProfile;
import org.faas.utils.Logger;

public class DatabaseLogger {

	private static DatabaseLogger me;
	
	static {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	                                                 
	    } catch (Exception ex) {
			Logger.error("DatabaseLogger:static","Exception: com.mysql.cj.jdbc.Driver-" + ex);
			// CWE-209
			//ex.printStackTrace();
	    }
	}

	public static DatabaseLogger getInstance() {
		if (me == null) {
			try {
				me = new DatabaseLogger();
			} catch (Exception e) {
				Logger.error("DatabaseLogger:getInstance","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}
		}
		
		return me;
	}

    private Connection connection = null;
    private PreparedStatement stmtMaster;
    private PreparedStatement stmtInstanceInfo;
    
    private PreparedStatement stmtFunctionFeature;
    private PreparedStatement stmtFunctionExecStats;
    private PreparedStatement stmtDataSizeStats;
    private PreparedStatement stmtDataDurationStats;
    
    private PreparedStatement stmtNodeGroupResourceUsageState;
    private PreparedStatement stmtNodeGroupResourceUsageStateDetail;

    private String masterId;
    
	private DatabaseLogger() throws Exception {
      
	}
	
	public void insertMaster(String id) {
		
		masterId = id;
		
		try {
			stmtMaster.setString(1, id);
			
			stmtMaster.executeUpdate();
		} catch (SQLException e) { // CWE-396 Exception -> SQLException
			Logger.error("DatabaseLogger:insertMaster","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
		
	}
	
	
	public void insert(DFaaSFunctionInstanceInfo info) {
		
		try {
			stmtInstanceInfo.setString(1, masterId);
			stmtInstanceInfo.setInt(2, info.getNodeGroupType());
			stmtInstanceInfo.setString(3,info.getFunctionProfileId());
			
			stmtInstanceInfo.setInt(4, info.getMaximumCompletionDuration());
			stmtInstanceInfo.setDouble(5, info.getFunctionRequestTime());
			stmtInstanceInfo.setDouble(6,  info.getFunctionCompletionTime());
			
			stmtInstanceInfo.setDouble(7, info.getFunctionStartingTime());
			stmtInstanceInfo.setDouble(8, info.getFunctionRunningDuration());
			stmtInstanceInfo.setDouble(9, info.getFunctionprocessingDuration());
			
			stmtInstanceInfo.setString(10, info.isDelayViolation() ? "Y" : "N");
			stmtInstanceInfo.setInt(11, info.getFunctionUseCPUCoreSize());
			stmtInstanceInfo.setInt(12, info.getFunctionUseMemorySize());
			
			stmtInstanceInfo.setInt(13, info.getFunctionUseProcessingMIsize());
			stmtInstanceInfo.setDouble(14, info.getFunctionRequestMessageSize());
			stmtInstanceInfo.setDouble(15, info.getFunctionResponseMessageSize());
			
			stmtInstanceInfo.setInt(16, info.getSensorId());
			stmtInstanceInfo.setInt(17, info.getActuatorId());
			stmtInstanceInfo.setDouble(18, info.getViolationUnitCost());

			stmtInstanceInfo.setLong(19, info.getSnapShotId());
			stmtInstanceInfo.setDouble(20, info.getTotalCost());
			stmtInstanceInfo.setDouble(21, info.getViolationCost());
			
			stmtInstanceInfo.setDouble(22, info.getComputingCost());
			stmtInstanceInfo.setDouble(23, info.getInputDataNetworkingCost());
			stmtInstanceInfo.setDouble(24, info.getOutputDataNetworkingCost());
			
			stmtInstanceInfo.setDouble(25, info.getInputDataReadingDuration());
			stmtInstanceInfo.setDouble(26, info.getOutputDataWritingDuration());
			stmtInstanceInfo.setString(27, info.getFunctionInstanceId());
			
			stmtInstanceInfo.executeUpdate();
			
			long id = -1;
	        try (ResultSet generatedKeys = stmtInstanceInfo.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	id = generatedKeys.getLong(1);
	            }
	            else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        }
			catch (SQLException e) { // CWE-754 add catch code
				Logger.error("getGeneratedKeys","Exception: " + e);
				throw new SQLException("Creating user failed, no ID obtained.");
			}
	        
		} catch (SQLException e) { // CWE-396 Exception -> SQLException
			Logger.error("DatabaseLogger:insert","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
        
	}
	
	private long nodeGroupResourceUsageStateId = 0;

	public void insert(List<ResourceStates> resourceStatesList) {
		
		for (int i=0;i<resourceStatesList.size();i++) {
			ResourceStates resourceStates = resourceStatesList.get(i);
			
			try {
				Map<Integer,org.faas.stats.ResourceStatsData> resourceStatMap = resourceStates.getResourcePoolModalityMap();
				Iterator<Integer> nodeGroupIdIte = resourceStatMap.keySet().iterator();

				while (nodeGroupIdIte.hasNext()) {
					int nodeGroupId = nodeGroupIdIte.next();
					org.faas.stats.ResourceStatsData resourceStatsData = resourceStatMap.get(nodeGroupId);

					stmtNodeGroupResourceUsageState.setString(1,masterId);
					stmtNodeGroupResourceUsageState.setLong(2, nodeGroupResourceUsageStateId++);
					stmtNodeGroupResourceUsageState.setInt(3, resourceStatsData.getNodeGroupType());
					stmtNodeGroupResourceUsageState.setString(4, resourceStates.getSnapShotTime()+"");
					stmtNodeGroupResourceUsageState.setInt(5, resourceStatsData.getTotalCpu());
					stmtNodeGroupResourceUsageState.setInt(6, resourceStatsData.getTotalMemory());
					
					stmtNodeGroupResourceUsageState.executeUpdate();
					
					// Queue Modality, queue에 대기중인 요소
					Iterator<org.faas.stats.QueueModality> iteQueueModality = resourceStatsData.getQueueModalityMap().values().iterator();
					while (iteQueueModality.hasNext()) {
						org.faas.stats.QueueModality state = iteQueueModality.next();
						String key = state.getExpectedFunctionRunningDuration()+"";
						int requests = state.getNumberOfFunctionRequests();
						int cores = state.getUsingNumberOfCore();
						
//						nodeGroupResourceUsageStateBuilder = nodeGroupResourceUsageStateBuilder.putFunctionWaitingState(key, requests);
//						nodeGroupResourceUsageStateBuilder = nodeGroupResourceUsageStateBuilder.putQueueUsageState(key, cores);
					
						stmtNodeGroupResourceUsageStateDetail.setString(1, masterId);
						stmtNodeGroupResourceUsageStateDetail.setLong(2, nodeGroupResourceUsageStateId);
						stmtNodeGroupResourceUsageStateDetail.setString(3, "FunctionWaitingState");
						stmtNodeGroupResourceUsageStateDetail.setString(4, key);
						stmtNodeGroupResourceUsageStateDetail.setDouble(5, requests);
						stmtNodeGroupResourceUsageStateDetail.executeUpdate();

						stmtNodeGroupResourceUsageStateDetail.setString(1, masterId);
						stmtNodeGroupResourceUsageStateDetail.setLong(2, nodeGroupResourceUsageStateId);
						stmtNodeGroupResourceUsageStateDetail.setString(3, "QueueUsageState");
						stmtNodeGroupResourceUsageStateDetail.setString(4, key);
						stmtNodeGroupResourceUsageStateDetail.setDouble(5, cores);
						stmtNodeGroupResourceUsageStateDetail.executeUpdate();

//		        		+ "master_id, resource_usage_state_id, "
//		        		+ "type, limit, value, "
					}

					// Resource pool modality, 실행중인 요소 
					Iterator<org.faas.stats.ResourcePoolModality> iteResourcePoolModality = resourceStatsData.getResourcePoolModalityMap().values().iterator();
					while (iteResourcePoolModality.hasNext()) {
						org.faas.stats.ResourcePoolModality state = iteResourcePoolModality.next();
						String key = state.getRemainingFunctionRunningDuration()+"";
						int requests = state.getNumberOfFunctionRequests();
						int cores = state.getUsingNumberOfCore();

//						nodeGroupResourceUsageStateBuilder = nodeGroupResourceUsageStateBuilder.putFunctionExecutionState(key, requests);
//						nodeGroupResourceUsageStateBuilder = nodeGroupResourceUsageStateBuilder.putCpuCoreUsageState(key, cores);

						stmtNodeGroupResourceUsageStateDetail.setString(1, masterId);
						stmtNodeGroupResourceUsageStateDetail.setLong(2, nodeGroupResourceUsageStateId);
						stmtNodeGroupResourceUsageStateDetail.setString(3, "FunctionExecutionState");
						stmtNodeGroupResourceUsageStateDetail.setString(4, key);
						stmtNodeGroupResourceUsageStateDetail.setDouble(5, requests);
						stmtNodeGroupResourceUsageStateDetail.executeUpdate();

						stmtNodeGroupResourceUsageStateDetail.setString(1, masterId);
						stmtNodeGroupResourceUsageStateDetail.setLong(2, nodeGroupResourceUsageStateId);
						stmtNodeGroupResourceUsageStateDetail.setString(3, "CpuCoreUsageState");
						stmtNodeGroupResourceUsageStateDetail.setString(4, key);
						stmtNodeGroupResourceUsageStateDetail.setDouble(5, cores);
						stmtNodeGroupResourceUsageStateDetail.executeUpdate();
					}
				}
			} catch (Exception e) {
				Logger.error("DatabaseLogger:insert","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}
		}
		
	}
	
	private int functionFeatureId = 0;
	private long functionExecStatId = 0;
	
	public void insert(List<FunctionStats> functionStatsList,FunctionProfile functionInfo) {
		
		// function_feature
		try {
			stmtFunctionFeature.setInt(1, functionFeatureId++);
			stmtFunctionFeature.setString(2, functionInfo.getFunctionProfileId());
			stmtFunctionFeature.setInt(3, functionInfo.getFunctionGrade());
			stmtFunctionFeature.setString(4, masterId);
			stmtFunctionFeature.setDouble(5, functionInfo.getMaximumCompletionDuration());
			stmtFunctionFeature.setDouble(6, functionInfo.getViolationUnitCost());
			stmtFunctionFeature.setInt(7, functionInfo.getComputingResourceRequirements().getNumberOfCpuCores());
			stmtFunctionFeature.setDouble(8, functionInfo.getComputingResourceRequirements().getMemorySize());
			
			stmtFunctionFeature.executeUpdate();
			
			for (int i=0;i<functionStatsList.size();i++ ) {
				FunctionStats functionStats = functionStatsList.get(i);

				//
				stmtFunctionExecStats.setLong(1, functionExecStatId++);
				stmtFunctionExecStats.setString(2, masterId);
				stmtFunctionExecStats.setInt(3, functionFeatureId);
				stmtFunctionExecStats.setInt(4, functionStats.getNodeGroupType());
				
				stmtFunctionExecStats.setDouble(5, functionStats.getFunctionRunningDurationStat().getMin());
				stmtFunctionExecStats.setDouble(6, functionStats.getFunctionRunningDurationStat().getMax());
				stmtFunctionExecStats.setDouble(7, functionStats.getFunctionRunningDurationStat().getMean());
				stmtFunctionExecStats.setDouble(8, functionStats.getFunctionRunningDurationStat().getMedian());
				stmtFunctionExecStats.setDouble(9, functionStats.getFunctionRunningDurationStat().getVariance());
				
				stmtFunctionExecStats.setDouble(10,  functionStats.getProcessingDurationStats().getMin());
				stmtFunctionExecStats.setDouble(11, functionStats.getProcessingDurationStats().getMax());
				stmtFunctionExecStats.setDouble(12, functionStats.getProcessingDurationStats().getMean());
				stmtFunctionExecStats.setDouble(13, functionStats.getProcessingDurationStats().getMedian());
				stmtFunctionExecStats.setDouble(14, functionStats.getProcessingDurationStats().getVariance());

				stmtFunctionExecStats.setDouble(15, functionStats.getCompletionDurationStats().getMin());
				stmtFunctionExecStats.setDouble(16, functionStats.getCompletionDurationStats().getMax());
				stmtFunctionExecStats.setDouble(17, functionStats.getCompletionDurationStats().getMean());
				stmtFunctionExecStats.setDouble(18, functionStats.getCompletionDurationStats().getMedian());
				stmtFunctionExecStats.setDouble(19, functionStats.getCompletionDurationStats().getVariance());

				stmtFunctionExecStats.setDouble(20, functionStats.getReqMessageSizeStat().getMin());
				stmtFunctionExecStats.setDouble(21, functionStats.getReqMessageSizeStat().getMax());
				stmtFunctionExecStats.setDouble(22, functionStats.getReqMessageSizeStat().getMean());
				stmtFunctionExecStats.setDouble(23, functionStats.getReqMessageSizeStat().getMedian());
				stmtFunctionExecStats.setDouble(24, functionStats.getReqMessageSizeStat().getVariance());

				stmtFunctionExecStats.setDouble(25, functionStats.getRspMessageSizeStat().getMin());
				stmtFunctionExecStats.setDouble(26, functionStats.getRspMessageSizeStat().getMax());
				stmtFunctionExecStats.setDouble(27, functionStats.getRspMessageSizeStat().getMean());
				stmtFunctionExecStats.setDouble(28, functionStats.getRspMessageSizeStat().getMedian());
				stmtFunctionExecStats.setDouble(29, functionStats.getRspMessageSizeStat().getVariance());

				stmtFunctionExecStats.executeUpdate();
				
				// input/output data size stats
				
				stmtDataSizeStats.setString(1, masterId);
				stmtDataSizeStats.setLong(2, functionExecStatId);
				
				StatValue dataSizeStatValue;
				Map<Integer,StatValue> inputDataSizeMap = functionStats.getInputDataSizeStats().getDataStatMap();

				dataSizeStatValue = inputDataSizeMap.get(DFaaSConstants.END_EDVICE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(3, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(4, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(5, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(6, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(7, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = inputDataSizeMap.get(DFaaSConstants.FOG_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(8, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(9, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(10, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(11, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(12, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = inputDataSizeMap.get(DFaaSConstants.EDGE_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(13, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(14, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(15, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(16, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(17, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = inputDataSizeMap.get(DFaaSConstants.CORE_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(18, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(19, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(20, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(21, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(22, dataSizeStatValue.getVariance());
				}

				Map<Integer,StatValue> outputDataSizeMap = functionStats.getOutputDataSizeStats().getDataStatMap();

				dataSizeStatValue = outputDataSizeMap.get(DFaaSConstants.END_EDVICE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(23, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(24, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(25, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(26, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(27, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = outputDataSizeMap.get(DFaaSConstants.FOG_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(28, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(29, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(30, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(31, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(32, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = outputDataSizeMap.get(DFaaSConstants.EDGE_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(33, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(34, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(35, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(36, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(37, dataSizeStatValue.getVariance());
				}

				dataSizeStatValue = outputDataSizeMap.get(DFaaSConstants.CORE_NODE_GROUP);
				if (dataSizeStatValue != null) {
					stmtDataSizeStats.setDouble(38, dataSizeStatValue.getMin());
					stmtDataSizeStats.setDouble(39, dataSizeStatValue.getMax());
					stmtDataSizeStats.setDouble(40, dataSizeStatValue.getMean());
					stmtDataSizeStats.setDouble(41, dataSizeStatValue.getMedian());
					stmtDataSizeStats.setDouble(42, dataSizeStatValue.getVariance());
				}
				
				stmtDataSizeStats.executeUpdate();
				
				// input/output data duration stats
				
				stmtDataDurationStats.setString(1, masterId);
				stmtDataDurationStats.setLong(2, functionExecStatId);

				DataDurationStats dataDurationStats = functionStats.getInputDataDurationStats();
				
				StatValue dataDurationStatValue = dataDurationStats.getDurationOfEDData();
				stmtDataDurationStats.setDouble(3, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(4, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(5, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(6, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(7, dataDurationStatValue.getVariance());
				
				dataDurationStatValue = dataDurationStats.getDurationOfFogData();
				stmtDataDurationStats.setDouble(8, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(9, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(10, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(11, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(12, dataDurationStatValue.getVariance());

				dataDurationStatValue = dataDurationStats.getDurationOfEdgeData();
				stmtDataDurationStats.setDouble(13, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(14, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(15, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(16, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(17, dataDurationStatValue.getVariance());

				dataDurationStatValue = dataDurationStats.getDurationOfCoreData();
				stmtDataDurationStats.setDouble(18, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(19, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(20, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(21, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(22, dataDurationStatValue.getVariance());

				dataDurationStats = functionStats.getOutputDataDurationStats();
				
				dataDurationStatValue = dataDurationStats.getDurationOfEDData();
				stmtDataDurationStats.setDouble(23, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(24, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(25, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(26, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(27, dataDurationStatValue.getVariance());
				
				dataDurationStatValue = dataDurationStats.getDurationOfFogData();
				stmtDataDurationStats.setDouble(28, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(29, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(30, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(31, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(32, dataDurationStatValue.getVariance());

				dataDurationStatValue = dataDurationStats.getDurationOfEdgeData();
				stmtDataDurationStats.setDouble(33, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(34, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(35, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(36, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(37, dataDurationStatValue.getVariance());

				dataDurationStatValue = dataDurationStats.getDurationOfCoreData();
				stmtDataDurationStats.setDouble(38, dataDurationStatValue.getMin());
				stmtDataDurationStats.setDouble(39, dataDurationStatValue.getMax());
				stmtDataDurationStats.setDouble(40, dataDurationStatValue.getMean());
				stmtDataDurationStats.setDouble(41, dataDurationStatValue.getMedian());
				stmtDataDurationStats.setDouble(42, dataDurationStatValue.getVariance());

				stmtDataDurationStats.executeUpdate();
			}
			
		} catch (SQLException e) { // CWE-396 Exception -> SQLException
			Logger.error("DatabaseLogger:insert","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
	
	public void open() {
		
		try {

			if (connection != null) {
				close();
			}
	        
			connection = DriverManager.getConnection("jdbc:mysql://"+SimulationConfig.getInstance().getDbHost()+":"+SimulationConfig.getInstance().getDbPort()+"/dfaassim?characterEncoding=UTF-8&serverTimezone=UTC" , "root", "root");
	        
	        stmtMaster = connection.prepareStatement("insert into dfaassim.master "
	        		+ "(id, create_date) "
	        		+ "values "
	        		+ "(?, sysdate()) ");
	        
	        // function instance info
	        
	        stmtInstanceInfo = connection.prepareStatement("insert into dfaassim.function_instance_info "
	        		+ "("
	        		+ "master_id, node_group_type, function_profile_id, "
	        		+ "maximum_completion_duration, function_request_time, function_completion_time, "
	        		+ "function_starting_time, function_running_duration, function_processing_duration, "
	        		+ "delay_violation, function_use_cpu_core_size, function_use_memory_size, "
	        		+ "function_use_processing_mi_size, function_request_message_size, function_response_message_size,"
	        		+ "sensor_id, actuator_id, violation_unit_cost, "
	        		+ "snapshot_id, total_cost, violation_cost, "
	        		+ "computing_cost, input_data_networking_cost, output_data_networking_cost, "
	        		+ "input_data_reading_duration, output_data_writing_duration, function_instance_id "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, ?,"
	        		+ " ?, ?, ?,"
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ?, "
	        		+ " ?, ?, ? "
	        		+ ")"
	        		, Statement.RETURN_GENERATED_KEYS);
	  
	        
	        // function feature
	        stmtFunctionFeature = connection.prepareStatement("insert into dfaassim.function_feature "
	        		+ "("
	        		+ "id, function_profile_id, grade, "
	        		+ "master_id, maximum_completion_duration, unit_slo_violation_cost, "
	        		+ "number_of_cpu_cores, size_of_memory"
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, ?,"
	        		+ " ?, ?, ?, "
	        		+ " ?, ?"
	        		+ ")"
	        		);
	        
	        // function exec stats
	        stmtFunctionExecStats = connection.prepareStatement("insert into dfaassim.function_exec_stats "
	        		+ "("
	        		+ "id, master_id, function_feature_id, execution_location, "
	        		+ "running_duration_min, running_duration_max, running_duration_mean, running_duration_median, running_duration_variance, "
	        		+ "processing_duration_min, processing_duration_max, processing_duration_mean, processing_duration_median, processing_duration_variance, "
	        		+ "completion_duration_min, completion_duration_max, completion_duration_mean, completion_duration_median, completion_duration_variance, "
	        		+ "function_request_min, function_request_max, function_request_mean, function_request_median, function_request_variance, "
	        		+ "function_response_min, function_response_max, function_response_mean, function_response_median, function_response_variance "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, ?, ?,"
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ? "
	        		+ ")"
	        		);
	        
	        // data size stats
	        stmtDataSizeStats = connection.prepareStatement("insert into dfaassim.data_size_stats "
	        		+ "("
	        		+ "master_id, function_exec_stats_id, "
	        		+ "ed_indata_min, ed_indata_max, ed_indata_mean, ed_indata_median, ed_indata_variance, "
	        		+ "fog_indata_min, fog_indata_max, fog_indata_mean, fog_indata_median, fog_indata_variance, "
	        		+ "edge_indata_min, edge_indata_max, edge_indata_mean, edge_indata_median, edge_indata_variance, "
	        		+ "core_indata_min, core_indata_max, core_indata_mean, core_indata_median, core_indata_variance, "
	        		+ "ed_outdata_min, ed_outdata_max, ed_outdata_mean, ed_outdata_median, ed_outdata_variance, "
	        		+ "fog_outdata_min, fog_outdata_max, fog_outdata_mean, fog_outdata_median, fog_outdata_variance, "
	        		+ "edge_outdata_min, edge_outdata_max, edge_outdata_mean, edge_outdata_median, edge_outdata_variance, "
	        		+ "core_outdata_min, core_outdata_max, core_outdata_mean, core_outdata_median, core_outdata_variance "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ? "
	        		+ ")"
	        		);
	        
	        // data duration stats
	        stmtDataDurationStats = connection.prepareStatement("insert into dfaassim.data_duration_stats "
	        		+ "("
	        		+ "master_id, function_exec_stats_id, "
	        		+ "ed_indata_min, ed_indata_max, ed_indata_mean, ed_indata_median, ed_indata_variance, "
	        		+ "fog_indata_min, fog_indata_max, fog_indata_mean, fog_indata_median, fog_indata_variance, "
	        		+ "edge_indata_min, edge_indata_max, edge_indata_mean, edge_indata_median, edge_indata_variance, "
	        		+ "core_indata_min, core_indata_max, core_indata_mean, core_indata_median, core_indata_variance, "
	        		+ "ed_outdata_min, ed_outdata_max, ed_outdata_mean, ed_outdata_median, ed_outdata_variance, "
	        		+ "fog_outdata_min, fog_outdata_max, fog_outdata_mean, fog_outdata_median, fog_outdata_variance, "
	        		+ "edge_outdata_min, edge_outdata_max, edge_outdata_mean, edge_outdata_median, edge_outdata_variance, "
	        		+ "core_outdata_min, core_outdata_max, core_outdata_mean, core_outdata_median, core_outdata_variance "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ?, "
	        		+ " ?, ?, ?, ?, ? "
	        		+ ")"
	        		);
	        
	        
	        //
	        stmtNodeGroupResourceUsageState = connection.prepareStatement("insert into dfaassim.node_group_resource_usage_state "
	        		+ "("
	        		+ "master_id, id, "
	        		+ "node_group_type, time_stamp, total_cpu, total_memory "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, "
	        		+ " ?, ?, ?, ? "
	        		+ ")"
	        		);
	        
	        stmtNodeGroupResourceUsageStateDetail = connection.prepareStatement("insert into dfaassim.node_group_resource_usage_state_detail "
	        		+ "("
	        		+ "master_id, resource_usage_state_id, "
	        		+ "type, boundary, value "
	        		+ ") "
	        		+ " values "
	        		+ "(?, ?, "
	        		+ " ?, ?, ? "
	        		+ ")"
	        		);
	        
		} catch (Exception e) {
			Logger.error("DatabaseLogger:open","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			connection.close();
		} catch(SQLException e) { // CWE-396 Exception -> SQLException
			Logger.error("DatabaseLogger:close","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
}

