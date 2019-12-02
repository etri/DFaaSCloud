package org.faas.utils.distribution;

import java.util.HashMap;
import java.util.Random;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.faas.DFaaSConstants;

public class LocationDistribution extends Distribution {

    private HashMap<Integer, Double> distribution = new HashMap<>();;
    private double distSum;

    private Random numGen;
    
    public LocationDistribution() {
    	numGen = new Random(10);
    }
    
    public LocationDistribution(double distCore,double distEdge,double distFog,double distEnd) {
    	this(10,distCore,distEdge,distFog,distEnd);
    }
    public LocationDistribution(long seed, double distCore,double distEdge,double distFog,double distEnd) {
    	addNumber(DFaaSConstants.CORE_NODE_GROUP,distCore);
    	addNumber(DFaaSConstants.EDGE_NODE_GROUP,distEdge);
    	addNumber(DFaaSConstants.FOG_NODE_GROUP,distFog);
    	addNumber(DFaaSConstants.END_EDVICE_GROUP,distEnd);
    	
    	numGen = new Random(seed);
    }
    
    /**
     * 
     * @param value
     * @param distribution
     */
    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    private int getDistributedRandomNumber() {
        //double rand = Math.random();
    	double rand = numGen.nextDouble();
    	
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }
	
	@Override
	public double sample() {
		return getDistributedRandomNumber();
	}

//	@Override
//	public int getDistributionType() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public double getMeanInterTransmitTime() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

    @Override
    public int getDistributionType() {
        return Distribution.LOCATION;
    }
	
}
