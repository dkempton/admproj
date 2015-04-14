package admproj;

import java.util.*;

import org.apache.commons.math3.stat.inference.OneWayAnova;

import datatypes.interfaces.ICoefValues;

public class FTestCalc {
	OneWayAnova ow = new OneWayAnova();

	public double[] fcalc(ICoefValues[] coefs) {


		int j = 0;

		ArrayList<Double> fvalues = new ArrayList<Double>();

		// loop through coefficients
		while (j < coefs[0].size()) {

			Map<Integer, ArrayList<Double>> clsMap = new HashMap<Integer, ArrayList<Double>>();

			// loop through each ICoefValues entry passed in as part of the
			// collection
			for (int i = 0; i < coefs.length; i++) {
				ICoefValues coefVals = coefs[i];
				int clsNum = coefVals.getClassLabel();

				Object clsListObj = clsMap.get(clsNum);
				if (clsListObj == null) {
					ArrayList<Double> clsArrayList = new ArrayList<Double>();
					clsArrayList.add(coefVals.getCoeff(j));
					clsMap.put(clsNum, clsArrayList);
				} else {
					@SuppressWarnings("unchecked")
					ArrayList<Double> clsArrayList = (ArrayList<Double>) clsListObj;
					clsArrayList.add(coefVals.getCoeff(j));
				}
			}
			ArrayList<double[]> clsColl = new ArrayList<double[]>();

			Collection<ArrayList<Double>> clsMapCollection = clsMap.values();
			for (Iterator<ArrayList<Double>> itr = clsMapCollection.iterator(); itr
					.hasNext();) {
				ArrayList<Double> clsArrayList = itr.next();
				double[] clsArr = new double[clsArrayList.size()];
				for (int k = 0; k < clsArr.length; k++) {
					clsArr[k] = clsArrayList.get(k);
				}
				clsColl.add(clsArr);
			}
			double fval1 = ow.anovaFValue(clsColl);
			fvalues.add(fval1);

			j++;
		}

		double[] retVals = new double[fvalues.size()];
		for (int k = 0; k < retVals.length; k++) {
			retVals[k] = fvalues.get(k);
		}
		return retVals;
	}
}
