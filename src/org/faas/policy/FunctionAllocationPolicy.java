package org.faas.policy;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class FunctionAllocationPolicy extends VmAllocationPolicy {
	private Host host;
	private double mips;
	
	public FunctionAllocationPolicy(List<? extends Host> list, double mips) {
		super(list);
		
		this.mips = mips;
		if(list.size()==1)
			this.host = list.get(0);
	}
	

	public double calcProcessTime(double requiredMips) {
		double time = requiredMips/mips;
		
		return time;
	}

	@Override
	public boolean allocateHostForVm(Vm vm) {
		Host host = this.host;
		return host.vmCreate(vm);
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		return host.vmCreate(vm);
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(
			List<? extends Vm> vmList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deallocateHostForVm(Vm vm) {
		if (this.host != null) {
			this.host.vmDestroy(vm);
		}
	}

	@Override
	public Host getHost(Vm vm) {
		return this.host;
	}

	@Override
	public Host getHost(int vmId, int userId) {
		return this.host;
	}
}
