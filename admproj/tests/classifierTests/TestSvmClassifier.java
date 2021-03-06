package classifierTests;

import org.junit.Test;

import classifier.SVMClassifier;
import dataselection.BasicSelection;

public class TestSvmClassifier {
	@Test(expected = IllegalArgumentException.class)
	public void testTrainThrowsOnZeroLengthDataSet() {
		double[][] dataset = new double[0][10];
		new SVMClassifier().train(dataset);;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTrainThrowsOnZeroWidthDataSet() {
		double[][] dataset = new double[10][0];
		new SVMClassifier().train(dataset);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTrainThrowsOnNullDataSet() {
		new SVMClassifier().train(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testTrainThrowsOnUnevenDataset() {
		double[][] dataset = { { 0.0, 5.0, 4.5, 5.0 }, {}, { 0.0, 5.0 },
				{ 0.0, 5.0, 4.5, 5.0 }, { 0.0, 5.0, 4.5, 5.0 } };
		new SVMClassifier().train(dataset);;
	}

	@Test
	public void testTrainingAndEvaluation() {
		double[][] training = { { 0, -1.0, -5.6, -4.5, -5.0 },
				{ 0, -6.0, -7.0, -8.5, -5.0 }, { 0, -9.0, -5.0, -4.5, -9.0 },
				{ 1, 1.0, 5.0, 4.5, 5.0 }, { 1, 1.0, 5.6, 4.5, 5.0 },
				{ 1, 6.0, 7.0, 8.5, 5.0 }, { 1, 9.0, 5.0, 4.5, 9.0 }};
		double[][] testing = { { 0, -1.0, -5.0, -4.5, -5.0 },
				{ 1, 3.0, 6.0, 4.8, 5.0 }, { 0, -3.0, -6.0, -4.8, -5.0 }};
		SVMClassifier svm = new SVMClassifier();
		svm.train(training);
		svm.evaluate(testing);
	}

}
