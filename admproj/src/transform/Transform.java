package transform;

/**
 * This class transforms 
 */

import java.util.ArrayList;
import java.util.concurrent.Callable;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;
import wavelets.interfaces.IWavelet;

public class Transform implements Callable<IWindowSet> {
	private IProjectFactory factory;
	private IWindowSet original;
	IWavelet wavelet;

	public Transform(IProjectFactory factory, IWindowSet original,
			IWavelet wavelet) {
		if (original == null)
			throw new IllegalArgumentException(
					"IWindowSet cannot to be transformed cannot be null");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in Transform constructor.");
		this.original = original;
		this.wavelet = wavelet;
		this.factory = factory;
	}

	@Override
	public IWindowSet call() throws Exception {

		// create a wavelength set for all of the wavelength sets in the
		// original window
		IWavelengthSet[] transformedSets = new IWavelengthSet[original.size()];

		// get the number of parameters in the wavelength sets as the number in
		// the first
		// wavelength set
		int length = original.getWavelengthSet(0).size();

		// process all of the parameters as one big set by first fetching them
		// from all of
		// the wavelenght sets.
		for (int waveId = 0; waveId < original.size(); waveId++) {

			// get the param sets from the current wavelength set.
			IWavelengthSet waveLengthSet = original.getWavelengthSet(waveId);

			// create a new array to place the transformed param set into
			IParamSet[] paramSetArray = new IParamSet[length];

			// process param sets
			for (int paramIndex = 0; paramIndex < length; paramIndex++) {

				// get the current param set form the set of param sets
				IParamSet param = waveLengthSet.getParamSet(paramIndex);

				// convert the param set into a matrix
				double[][] convertedParamMatrix = this
						.convertParamSetToDoubleMatrix(param);

				// do transform on the matrix
				double[][] transformedParamMatrix = this
						.transformConvertedParamStat(convertedParamMatrix);

				// convert it back to a param set
				IParamSet transformedParam = convertDoubleMatrixToParamSet(
						transformedParamMatrix, param.getParamId());

				// place in the array for return
				paramSetArray[paramIndex] = transformedParam;
			}
			transformedSets[waveId] = this.factory.getWaveSet(paramSetArray,
					waveLengthSet.getWaveId());
		}

		return this.factory.getWindowSet(transformedSets,
				original.memberOfClass(), original.getWindowId());
	}

	private double[][] convertParamSetToDoubleMatrix(IParamSet paramSet) {

		// take the first stat set from the param set and use it to determine
		// how long each of the stat sets will be in this param set.
		IStatSet indicatorStatSet = paramSet.getStatSet(0);
		int numberOfStats = indicatorStatSet.size();

		// determine the length of the window of stat values
		int lengthOfTrack = paramSet.size();

		// create a matrix to hold the stats in this window
		double[][] convertedParamSet = new double[numberOfStats][lengthOfTrack];

		// put the stats into a matrix of stat num x window length
		for (int statIndex = 0; statIndex < numberOfStats; statIndex++) {
			for (int index = 0; index < lengthOfTrack; index++) {
				convertedParamSet[statIndex][index] = paramSet
						.getStatSet(index).getStat(statIndex);
			}
		}
		return convertedParamSet;
	}

	private double[][] transformConvertedParamStat(double[][] convertedParamSet) {

		// get the number of stats to convert
		int numberOfStats = convertedParamSet.length;
		ArrayList<double[]> transformResults = new ArrayList<double[]>();

		// process each window of stats
		for (int statIndex = 0; statIndex < numberOfStats; statIndex++) {
			double[] data = convertedParamSet[statIndex];
			double[] transformedData = this.wavelet.calcWavelet(data);
			transformResults.add(transformedData);
		}

		// figure out the size of the return matrix by looking at the first
		// transformed
		// stat window
		double[][] transformedParamSet = new double[numberOfStats][transformResults
				.get(0).length];

		// take each of the transformed windows and place them into the return
		// array.
		for (int index = 0; index < transformResults.size(); index++) {
			transformedParamSet[index] = transformResults.get(index);
		}
		return transformedParamSet;
	}

	private IParamSet convertDoubleMatrixToParamSet(
			double[][] convertedParamSet, int paramId) {
		int numberOfStats = convertedParamSet.length;
		int length = convertedParamSet[0].length;
		IStatSet[] statSets = new IStatSet[length];
		for (int index = 0; index < length; index++) {
			double[] stats = new double[numberOfStats];
			for (int statIndex = 0; statIndex < numberOfStats; statIndex++) {
				stats[statIndex] = convertedParamSet[statIndex][index];
			}
			statSets[index] = this.factory.getStatSet(stats);
		}
		return this.factory.getParamSet(statSets, paramId);
	}
}
