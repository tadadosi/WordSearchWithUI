package application.grid.utils;

import java.util.ArrayList;
import java.util.List;

public class BinPacking {

	// return bin id for the first weight. -1 - weight doesn't fit any bin
	public static int fit(List<Integer> weight, int[] bin) {
		// create array for storing which weight should go into which bin
		int[] binIndex = new int[weight.size()];
		int[] binTemp = new int[bin.length];
		int j = 0;
		for (int binas : bin) {
		    binTemp[j] = binas;
		    j++;
		}
//		List<Integer> weightOriginal = new ArrayList<Integer>(weight);
		
		// create temporary value for returning bin number for the first weight.
		if (weight.size() > 0) {

		} else {
			return -1;
		}
		int loopIndex = 0;
		// full bins packing algorithm
		for (int i = 0; i < weight.size(); i++) {
		    loopIndex++;
			if (loopIndex > 20000000)
			    return -1;
//				System.out.println(loopIndex);;
			// check into which bin the weight fits
			while (binIndex[i] < binTemp.length && binTemp[binIndex[i]] < weight.get(i))
				binIndex[i]++;

			// if weight fits into the bin
			if (binIndex[i] < binTemp.length)
				binTemp[binIndex[i]] -= weight.get(i);
			// if weight doesn't fit into the bin
			else {
				binIndex[i] = 0;
				i--;
				if (i > -1) {
					binTemp[binIndex[i]] += weight.get(i);
					binIndex[i]++;
				}
				i--;
			}

			// if impossible combination of fitting weights into the bins
			if (i == -2) {
				return -1;
			}
		}

		return binIndex[0];
	}
}

