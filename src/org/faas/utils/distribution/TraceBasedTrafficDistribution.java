package org.faas.utils.distribution;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.faas.utils.Logger;

public class TraceBasedTrafficDistribution extends Distribution {

	private Iterator<Double> samples;
	
	/**
	 * 
	 * @param seed
	 * @param fileName
	 * @param column starts from 1
	 * @param separator column separator
	 */
	public TraceBasedTrafficDistribution(long seed,String fileName,int column,String separator) {
		BufferedReader br = null;
		FileReader fr = null;

		try {
			File f = new File(fileName);
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line;

			double prevValue=0;
			List<Double> sampleList = new ArrayList<Double>();
			int i=0;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line,separator);
				String columnStr = null;

				try {
					for (int c=0;c<column;c++) {
						columnStr = st.nextToken();
					}

					double currentValue = Double.parseDouble(columnStr);
					if (i>0) {
						sampleList.add(currentValue - prevValue);
					}
					
					prevValue = currentValue;
					i++;
				} catch (NoSuchElementException ex) {
				    // CWE-390 code added
                    Logger.error("TraceBasedTrafficDistribution:","NoSuchElementException: " + ex);
				} catch (NumberFormatException ex) {
                    // CWE-390 code added
                    Logger.error("TraceBasedTrafficDistribution:","NumberFormatException: " + ex);
				}
			}
			
			samples = sampleList.iterator();
		} catch (FileNotFoundException fne) { // CWE-754 FileNotFoundException, IOException added
            Logger.error("TraceBasedTrafficDistribution:","FileNotFoundException: " + fne);
        } catch (IOException e) {
            Logger.error("TraceBasedTrafficDistribution:","IOException: " + e);
        } catch (Exception e) {
            // CWE-209 add code
            Logger.error("TraceBasedTrafficDistribution:","Exception: " + e);
			//e.printStackTrace();
		} finally {
			// CWE-404 close() code added
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				Logger.error("TraceBasedTrafficDistribution:","Exception: " + e);
			}

			try {
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				Logger.error("TraceBasedTrafficDistribution:","Exception: " + e);
			}
		}
	}
	
	@Override
	public double sample() {
		
		if (samples == null) {
			return Double.MAX_VALUE;
		}
		
		if (samples.hasNext()) {
			return samples.next();
		}
		
		return Double.MAX_VALUE;
	}

	@Override
	public int getDistributionType() {
		return Distribution.TRACEBASEDTRAFFIC;
	}
}
