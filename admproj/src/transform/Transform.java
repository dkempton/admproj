package transform;

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

		IWavelengthSet[] transformedSets = new IWavelengthSet[original.size()];
		int length = original.getWavelengthSet(0).size();
		for (int waveId = 0; waveId < original.size(); waveId++) {
			IWavelengthSet waveLengthSet = original.getWavelengthSet(waveId);
			IParamSet[] paramSetArray = new IParamSet[length];
			for (int paramIndex = 0; paramIndex < length; paramIndex++) {
				IParamSet param = waveLengthSet.getParamSet(paramIndex);
				double[][] convertedParamMatrix = this
						.convertParamSetToDoubleMatrix(param);
				double[][] transformedParamMatrix = this
						.transformConvertedParamStat(convertedParamMatrix);
				IParamSet transformedParam = convertDoubleMatrixToParamSet(
						transformedParamMatrix, param.getParamId());
				paramSetArray[paramIndex] = transformedParam;
			}
			transformedSets[waveId] = this.factory.getWaveSet(paramSetArray,
					waveLengthSet.getWaveId());
		}

		return this.factory.getWindowSet(transformedSets,
				original.memberOfClass(), original.getWindowId());
	}

	private double[][] convertParamSetToDoubleMatrix(IParamSet paramSet) {

		IStatSet indicatorStatSet = paramSet.getStatSet(0);
		int numberOfStats = indicatorStatSet.size();
		int lengthOfTrack = paramSet.size();
		double[][] convertedParamSet = new double[numberOfStats][lengthOfTrack];
		for (int statIndex = 0; statIndex < numberOfStats; statIndex++) {
			for (int index = 0; index < lengthOfTrack; index++) {
				convertedParamSet[statIndex][index] = paramSet
						.getStatSet(index).getStat(statIndex);
			}
		}
		return convertedParamSet;
	}

	private double[][] transformConvertedParamStat(double[][] convertedParamSet) {
		int numberOfStats = convertedParamSet.length;
		ArrayList<double[]> transformResults = new ArrayList<double[]>();
		for (int statIndex = 0; statIndex < numberOfStats; statIndex++) {
			double[] data = convertedParamSet[statIndex];
 			double[] transformedData = this.wavelet.calcWavelet(data);
			transformResults.add(transformedData);
		}

		double[][] transformedParamSet = new double[numberOfStats][transformResults
				.get(0).length];
		for (int index = 0; index < transformResults.size(); index++) {
			transformedParamSet[index] = transformResults.get(0);
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
