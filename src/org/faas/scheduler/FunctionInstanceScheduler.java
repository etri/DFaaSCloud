package org.faas.scheduler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.lists.PeList;

public class FunctionInstanceScheduler extends VmScheduler {

	//private Map<Integer,Pe> peMap = new HashMap<Integer,Pe>();
	
	public FunctionInstanceScheduler(List<? extends Pe> pelist) {
		super(pelist);
		
//		for (int i=0;i<super.getPeList().size();i++) {
//			Pe pe = super.getPeList().get(i);
//			peMap.put(pe.getId(), pe);
//		}
	}
	
	/**
	 * Allocates PEs for a VM.
	 * 
	 * @param vm the vm
	 * @param mipsShare the mips share
	 * @return $true if this policy allows a new VM in the host, $false otherwise
	 * @pre $none
	 * @post $none
	 */
	public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {

		// count state FREE PEs
		if (mipsShare.size() > countFreePe()) {
			return false;
		}

		for (int i=0;i<mipsShare.size();i++) {
			Pe pe = PeList.getFreePe(super.getPeList());
			pe.setStatusBusy(); // issue #40
			this.addPeForVm(vm, pe);
		}
		
		return true;
	}
	
	/**
	 * Releases PEs allocated to a VM.
	 * 
	 * @param vm the vm
	 * @pre $none
	 * @post $none
	 */
	public void deallocatePesForVm(Vm vm) {
		//List<Pe> vmPeList = super.getPesAllocatedForVM(vm);
		Map<String, List<Pe>> peMap = getPeMap();
		List<Pe> vmPeList = peMap.get(vm.getUid());
		
		for (int i=0;i<vmPeList.size();i++) {
			//peMap.get(vmPeList.get(i).getId()).setStatusFree();
			vmPeList.get(i).setStatusFree();
		}
		
		getPeMap().get(vm.getUid()).clear(); // issue #40
	}
	
	protected int countFreePe() {
		int count = 0;
		List<Pe> peList = super.getPeList();
		for (int i=0;i<peList.size();i++) {
			if (peList.get(i).getStatus() == Pe.FREE) {
				count++;
			}
		}
		return count;
	}
	
	protected void addPeForVm(Vm vm,Pe pe) {
		
		Map<String, List<Pe>> peMap = getPeMap();
		
		List<Pe> peList = peMap.get(vm.getUid());
		if (peList == null) {
			peMap.put(vm.getUid(), peList = new LinkedList<Pe>());
		}
		
		peList.add(pe);
	}

}
