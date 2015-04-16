package dataselection;

import java.util.Collections;
import java.util.Vector;

import dataselection.interfaces.IDataSelection;

public class BasicSelection implements IDataSelection {
	private double[][] training;
	private double[][] testing;

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

	@Override
	public void addDataSet(double[][] dataset) {
		double[][] shuffledData = shuffleData(dataset);
		int cutoff = (int) (shuffledData.length * .67);
		training = new double[cutoff][];
		for (int i = 0; i < cutoff; i++) {
			training[i] = shuffledData[i];
		}
		testing = new double[shuffledData.length - cutoff][];
		for (int i = cutoff; i < shuffledData.length; i++) {
			testing[i] = shuffledData[i];
		}

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

	private void loadTrainingAndTesting(double[][] shuffledData) {

	}

}
