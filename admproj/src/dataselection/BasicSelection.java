package dataselection;

import java.util.Collections;
import java.util.Vector;

import dataselection.interfaces.IDataSelection;

public class BasicSelection implements IDataSelection {
	private double[][] training;
	private double[][] testing;

	public BasicSelection(double[][] dataset) {
		if (dataset == null) {
			throw new IllegalArgumentException("Dataset cannot be null");
		}
		double[][] shuffledData = shuffleData(dataset);
		int cutoff = (int) Math.round(shuffledData.length * .67);
		System.out.println("CUTOFF: " + cutoff);
		training = new double[cutoff][];
		int index = 0;
		for (int i = 0; i < cutoff; i++, index++) {
			training[i] = shuffledData[index];
		}
		testing = new double[shuffledData.length - cutoff][];
		for (int i = 0; i < testing.length; i++, index++) {
			testing[i] = shuffledData[index];
		}
	}

	@Override
	public double[][][] getTrainingData() {
		double[][][] threeD = { training };
		return threeD;
	}

	@Override
	public double[][][] getTestingData() {
		double[][][] threeD = { testing };
		return threeD;
	}

	private double[][] shuffleData(double[][] dataset) {
		Vector<double[]> vect = new Vector<double[]>();
		int width = 0;
		for (double[] array : dataset) {
			vect.add(array);
			width = array.length;
		}
		Collections.shuffle(vect);
		double[][] shuffledData = new double[vect.size()][width];
		for (int i = 0; i < vect.size(); i++) {
			shuffledData[i] = (double[]) vect.get(i);
		}
		return shuffledData;
	}

}
