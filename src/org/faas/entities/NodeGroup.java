package org.faas.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.faas.DFaaSConstants;
import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.application.DFaaSFunctionReqMsg;
import org.faas.application.DFaaSFunctionRspMsg;
import org.faas.application.DfaaSFunctionInstance;
import org.faas.network.HIINetwork;
import org.faas.policy.FunctionAllocationPolicy;
import org.faas.stats.collector.MonitoringDataCollector;
import org.faas.topology.FunctionProfile;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.utils.DFaaSEvents;
import org.faas.utils.DFaaSUtils;

public class NodeGroup extends PowerDatacenter {

	private DFaaSFunctionScheduler dfaasFunctionScheduler;
	private int type; // CORE, EDGE, FOG

	private int tpId; // id on NetworkTopology
	private double computingUnitCost;
	private int mips;

	private int totalRam;
	private int totalCpu;

	private int queueSize;

	private NodeGroupController nodeGroupController = new NodeGroupController();

	public NodeGroup(int id, int type, double computingUnitCost, int mips, int queueSize,
					 DatacenterCharacteristics characteristics,
					 VmAllocationPolicy vmAllocationPolicy,
					 List<Storage> storageList,
					 double schedulingInterval,
					 double uplinkBandwidth, double downlinkBandwidth, double uplinkLatency, double ratePerMips) throws Exception {

		super(DFaaSConstants.getEntityName(type)+"-"+id, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
		//super(DFaaSConstants.NODE_NAMES[type]+"-"+id,characteristics,vmAllocationPolicy,storageList,schedulingInterval
		//		,uplinkBandwidth,downlinkBandwidth,uplinkLatency,ratePerMips);

		this.type = type;
		this.tpId = id;
		this.computingUnitCost = computingUnitCost;
		this.mips = mips;
		this.queueSize = queueSize;

		List<Host> hostList = super.getHostList();
		for (int i=0;i<hostList.size();i++) {
			Host host = hostList.get(i);
			totalRam += host.getRam();
			totalCpu += host.getNumberOfPes();
		}

	}

	public int getTpId() {
		return tpId;
	}

	public int getType() {
		return type;
	}

	public void setINPController(DFaaSFunctionScheduler controller) {
		this.dfaasFunctionScheduler = controller;
	}

	public DFaaSFunctionScheduler getINPController() {
		return dfaasFunctionScheduler;
	}

	public ResourceUtilization getResourceUtilization() {

		double totalCores = 0;
		double usedCores = 0;

		double totalRam = 0;
		double usedRam = 0;

		List<Host> hostList= super.getHostList();
		for (int i=0;i<hostList.size();i++) {
			Host host = hostList.get(i);
			List<Pe>peList = host.getVmScheduler().getPeList();
			//List<Pe>peList = host.getPeList();

			// Issue #73
			totalCores += peList.size();
			usedCores += PeList.getNumberOfBusyPes(peList);

			RamProvisioner ramProvisioner = host.getRamProvisioner();
			totalRam += ramProvisioner.getRam();
			usedRam += ramProvisioner.getUsedRam();
		}

		return new ResourceUtilization(totalCores, usedCores, totalRam, usedRam);
	}

	public static class ResourceUtilization {
		double totalCores = 0;
		double usedCores = 0;

		double totalRam = 0;
		double usedRam = 0;


		private ResourceUtilization(double totalCores, double usedCores, double totalRam, double usedRam) {
			this.totalCores = totalCores;
			this.usedCores = usedCores;
			this.totalRam = totalRam;
			this.usedRam = usedRam;
		}

		public double getTotalCores() {
			return totalCores;
		}

		public double getUsedCores() {
			return usedCores;
		}

		public double getTotalRam() {
			return totalRam;
		}

		public double getUsedRam() {
			return usedRam;
		}

	}

	@Override
	protected void processOtherEvent(SimEvent ev) {

		//
		switch(ev.getTag()){
			case DFaaSEvents.NG_FUNCTION_REQ_ARRIVAL:
				processNgFunctionReqArrival(ev);
				break;
			case DFaaSEvents.FUNCTION_COMPLETE:
				processFunctionComplete(ev);
				break;
			// CWE-478: add default code
			default:
				break;
		}

	}

	private void processNgFunctionReqArrival(SimEvent ev) {
		nodeGroupController.pushEvent(ev);

		processEventInReadyQueue();
	}

	private void processEventInReadyQueue() {
		SimEvent event = nodeGroupController.getFirstEvent();

		if (event == null) {
			return;
		}

		DFaaSFunctionReqMsg reqMsg = (DFaaSFunctionReqMsg)event.getData();

		boolean result = processFunctionCreate(event,reqMsg);
		if (result) {
			nodeGroupController.popEvent();
		}

	}

	private boolean processFunctionCreate(SimEvent simEvent,DFaaSFunctionReqMsg reqMsg) {

		if (this.queueSize > 0) {
			if (this.nodeGroupController.waitingQueue.size() >= queueSize) {
				return false;
			}
		}

		DFaaSFunctionInstanceInfo functionInfo = reqMsg.getFunctionInfo();

		// TODO : set below according to the functionInfo
		int mips = this.mips;//functionInfo.getFunctionUseProcessingMIsize(); // issue #36 첫번째.
		long size = 1; // storage size. obsolete
		long bw = 1000; // obsolete
		String vmm = "Xen";
		int ram= functionInfo.getFunctionUseMemorySize();
		int userId = 100;

		FunctionProfile functionProfile = NetworkTopologyHelper.getInstance().getFunctionProfile(reqMsg.getFunctionProfileId());
		//DfaaSFunctionInstance functionInstance = new DfaaSFunctionInstance(
		//		functionProfile,
		//		functionInfo,DFaaSUtils.generateEntityId(), userId,
		//		mips, ram, bw, size, vmm, new TupleScheduler(mips, 1));
		DfaaSFunctionInstance functionInstance = new DfaaSFunctionInstance(
				functionProfile,
				functionInfo, DFaaSUtils.generateEntityId(), userId,
				mips, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

		boolean result = getVmAllocationPolicy().allocateHostForVm(functionInstance);

//		if (ack) {
//			int[] data = new int[3];
//			data[0] = getId();
//			data[1] = vm.getId();
//
//			if (result) {
//				data[2] = CloudSimTags.TRUE;
//			} else {
//				data[2] = CloudSimTags.FALSE;
//			}
//			send(vm.getUserId(), CloudSim.getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, data);
//		}

		if (result) {
			reqMsg.setFunctionInstance(functionInstance);
			reqMsg.setSimEvent(simEvent);

			getVmList().add(functionInstance);

			if (functionInstance.isBeingInstantiated()) {
				functionInstance.setBeingInstantiated(false);
			}
			functionInstance.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(functionInstance).getVmScheduler()
					.getAllocatedMipsForVm(functionInstance));

			// calcXXX and update functionInfo
			functionInstance.calcFunctionRunningDuration((FunctionAllocationPolicy)getVmAllocationPolicy(),getId());
			double functionRunningDuration = functionInfo.getFunctionRunningDuration();

			double delay = functionRunningDuration;
			//System.out.println(this.getClass().getName()+".processFunctionCreate: FUNCTION_COMPLETE delay="+delay);
			send(getId(),delay,DFaaSEvents.FUNCTION_COMPLETE,reqMsg);

		}

		return result;
	}

	private void processFunctionComplete(SimEvent ev) {

		DFaaSFunctionReqMsg reqMsg = (DFaaSFunctionReqMsg)ev.getData();
		DFaaSFunctionInstanceInfo functionInfo = reqMsg.getFunctionInfo();

		DFaaSFunctionRspMsg rspMsg = new DFaaSFunctionRspMsg(functionInfo.getFunctionProfileId()
				,functionInfo.getFunctionResponseMessageSize()
				,functionInfo);

		EventSink eventSink = HIINetwork.getInstance().getActuatorPairOfSensor(reqMsg.getEventSource().getId());
		rspMsg.setEventSink(eventSink);

		double delay = calcNetworkDelay(reqMsg);

		//System.out.println(this.getClass().getName()+".processFunctionCreate: ACTUATOR_FUNCTION_RSP_ARRIVAL delay="+delay);

		send(functionInfo.getActuatorId(), delay, DFaaSEvents.ACTUATOR_FUNCTION_RSP_ARRIVAL,rspMsg);

		getVmAllocationPolicy().deallocateHostForVm(reqMsg.getFunctionInstance());

		DfaaSFunctionInstance functionInstance = reqMsg.getFunctionInstance();
		functionInstance.calFunctionRunningCost(getId(), reqMsg, rspMsg, computingUnitCost);

		// [
		//getVmList().remove(reqMsg.getFunctionInstance());
		sendNow(getId(), CloudSimTags.VM_DESTROY, reqMsg.getFunctionInstance());
		// ]
		nodeGroupController.doneEvent(reqMsg.getSimEvent());

		MonitoringDataCollector.getInstance().updateFinishedQueueSum(getId(), functionInfo.getFunctionGrade());

		processEventInReadyQueue();
	}

	/**
	 *
	 * @param reqMsg
	 * @return milli-seconds
	 */
	private double calcNetworkDelay(DFaaSFunctionReqMsg reqMsg) {
		int sourceId = getId();
		int destId = reqMsg.getEventSource().getActuatorId();

		double delay = HIINetwork.getInstance().calcNetworkDelay2(reqMsg.getMessageSize(), sourceId, destId);

		return delay;
	}

	public List<DFaaSFunctionInstanceInfo> getFunctionInstancesInWaitingQueue() {

		List<DFaaSFunctionInstanceInfo> list = new ArrayList<DFaaSFunctionInstanceInfo>();

		// 대기중인 queue에 있는 항목들 add 필요한지 확인.
		for (int i=0;i<nodeGroupController.waitingQueue.size();i++) {
			DFaaSFunctionReqMsg reqMsg = (DFaaSFunctionReqMsg)nodeGroupController.waitingQueue.get(i).getData();
			list.add(reqMsg.getFunctionInfo());
		}
		return list;

	}

	public List<DFaaSFunctionInstanceInfo> getFunctionInstancesInRunningQueue() {

		List<DFaaSFunctionInstanceInfo> list = new ArrayList<DFaaSFunctionInstanceInfo>();

		// 실행중인 function instance queue
		for (int i=0;i<nodeGroupController.runningQueue.size();i++) {
			DFaaSFunctionReqMsg reqMsg = (DFaaSFunctionReqMsg)nodeGroupController.runningQueue.get(i).getData();
			list.add(reqMsg.getFunctionInfo());
		}

		return list;

	}

	public int getTotalCpu() {
		return totalCpu;
	}

	public int getTotalMemory() {
		return totalRam;
	}

	class NodeGroupController {
		// 실행 대기 queue
		private final List<SimEvent> waitingQueue = new LinkedList<SimEvent>();

		// 실행중 queue
		private final List<SimEvent> runningQueue = new LinkedList<SimEvent>();

		void pushEvent(SimEvent ev) {
			waitingQueue.add(ev);
		}

		SimEvent getFirstEvent() {
			if (waitingQueue.size() == 0) return null;
			return waitingQueue.get(0);
		}

		// move the first event in waiting queue to running queue
		SimEvent popEvent() {
			if (waitingQueue.size() == 0) return null;

			SimEvent ev = waitingQueue.remove(0);
			runningQueue.add(ev);
			return ev;
		}

		void doneEvent(SimEvent ev) {
			runningQueue.remove(ev);
		}
	}
}
