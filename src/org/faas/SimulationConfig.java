package org.faas;

import java.io.File;

import org.faas.utils.JsonUtil;

public class SimulationConfig {

	//private static final String file = SimulationManager.CONFIG_PATH+"/simulation_config.json";
	
	private static SimulationConfig me;

	private boolean infiniteRunning = false;
	
	private double simulationRunningDuration=1000;
	private double inpControllerDecisionTime=5;
	private double infraManagerMonitoringInterval=100;
	private int resourceModalitySize;
	private double resourceModalityGap;
	
	private double funcProfileStatsDisplayInterval = 0.5;
	
	private long initialSeed=0;
	//private String workingDir="json"; // 꼭 필요시 global_settings.json 에서 처리.
	
	private String agentHost="localhost";
	private int agentPort=50051;
	
	private String dbHost="localhost";
	private int dbPort=3306;
	
	private String recentNetworkTopologyFile;
	
	private boolean dbLogging = false;
	
	static {
		me = (SimulationConfig)JsonUtil.read(SimulationConfig.class, SimulationManager.getConfigPath()+"/simulation_config.json");

		if (me == null)
			me = new SimulationConfig();
	}
	
	public static void load(String configPath) {
		File newConfigFile = new File (configPath,"simulation_config.json");
		SimulationManager.setConfigPath(configPath);
		if (newConfigFile.exists() == false) {
			me.save();
		} else {
			me = (SimulationConfig)JsonUtil.read(SimulationConfig.class, configPath+"/simulation_config.json");
		}
		me.setConfigPath(configPath);
	}
	
	public static SimulationConfig getInstance() {
		return me;
	}
	
	public void save() {
		JsonUtil.write(this, SimulationManager.getConfigPath()+"/simulation_config.json");
	}
	
	public double getSimulationRunningDuration() {
		return simulationRunningDuration;
	}

	public void setSimulationRunningDuration(double simulationRunningDuration) {
		this.simulationRunningDuration = simulationRunningDuration;
	}

	public double getInpControllerDecisionTime() {
		return inpControllerDecisionTime;
	}

	public void setInpControllerDecisionTime(double inpControllerDecisionTime) {
		this.inpControllerDecisionTime = inpControllerDecisionTime;
	}

	public double getInfraManagerMonitoringInterval() {
		return infraManagerMonitoringInterval;
	}

	public void setInfraManagerMonitoringInterval(double infraManagerMonitoringInterval) {
		this.infraManagerMonitoringInterval = infraManagerMonitoringInterval;
	}

	public int getResourceModalitySize() {
		return resourceModalitySize;
	}

	public void setResourceModalitySize(int resourceModalitySize) {
		this.resourceModalitySize = resourceModalitySize;
	}
	
	public double getResourceModalityGap() {
		return resourceModalityGap;
	}

	public void setResourceModalityGap(double resourceModalityGap) {
		this.resourceModalityGap = resourceModalityGap;
	}

	public long getInitialSeed() {
		return initialSeed;
	}

	public void setInitialSeed(long initialSeed) {
		this.initialSeed = initialSeed;
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public int getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(int agentPort) {
		this.agentPort = agentPort;
	}

	public String getRecentNetworkTopologyFile() {
		return recentNetworkTopologyFile;
	}

	public void setRecentNetworkTopologyFile(String recentNetworkTopologyFile) {
		this.recentNetworkTopologyFile = recentNetworkTopologyFile;
		save();
	}

	public boolean isInfiniteRunning() {
		return infiniteRunning;
	}

	public void setInfiniteRunning(boolean infiniteRunning) {
		this.infiniteRunning = infiniteRunning;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}
	
	public boolean getDbLogging() {
		return dbLogging;
	}
	
	public void setDbLogging(boolean dbLogging) {
		this.dbLogging = dbLogging;
	}

	public double getFuncProfileStatsDisplayInterval() {
		return funcProfileStatsDisplayInterval;
	}

	public void setFuncProfileStatsDisplayInterval(double funcProfileStatsDisplayInterval) {
		this.funcProfileStatsDisplayInterval = funcProfileStatsDisplayInterval;
	}

	/**
	 * 기존 json파일 호환성 문제로 함수만 남겨둠.
	 * @param configPath
	 */
	@Deprecated
	public void setConfigPath(String configPath) {
	}

//	public String getWorkingDir() {
//		return workingDir;
//	}

	@Deprecated
	public void setWorkingDir(String workingDir) {
		//this.workingDir = workingDir;
	}
	
}
