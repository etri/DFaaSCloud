package org.faas.utils.distribution.test;

import java.util.HashMap;

import org.faas.utils.distribution.LocationDistribution;

public class LocationDistributionTest {

	public void test1() {
		
	}
	
	public void test2() {
		
	}
	
	public static void main(String []argv) {
		LocationDistribution drng = new LocationDistribution();
        drng.addNumber(1, 0.1);
        drng.addNumber(2, 0.2);
        drng.addNumber(3, 0.3);
        drng.addNumber(4, 0.4);

        //int testCount = 1000000;
        int testCount = 1000000;

        HashMap<Integer, Double> test = new HashMap<>();

        for (int i = 0; i < testCount; i++) {
            int random = (int)drng.sample();
            //System.out.println(random);
            //int intRandom = (int)random;
            test.put(random, (test.get(random) == null) ? (1d / testCount) : test.get(random) + 1d / testCount);
        }

        System.out.println(test.toString());
	}
}
