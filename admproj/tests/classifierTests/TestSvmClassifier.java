package classifierTests;

import org.junit.Test;

import classifier.SVMClassifier;
import dataselection.BasicSelection;

public class TestSvmClassifier {
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnZeroLengthDataSet() {
		double[][] dataset = new double[0][10];
		new SVMClassifier(dataset, new BasicSelection(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnZeroWidthDataSet() {
		double[][] dataset = new double[10][0];
		new SVMClassifier(dataset, new BasicSelection(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullDataSet() {
		new SVMClassifier(null, new BasicSelection(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullSelector() {
		double[][] dataset = new double[10][10];
		new SVMClassifier(dataset, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnUnevenDataset() {
		double[][] dataset = { { 0.0, 5.0, 4.5, 5.0 }, {}, { 0.0, 5.0 },
				{ 0.0, 5.0, 4.5, 5.0 }, { 0.0, 5.0, 4.5, 5.0 } };
		new SVMClassifier(dataset, new BasicSelection(), null);
	}

	public void testConstructorForSuccessOnEvenDataset() {
		double[][] dataset = { { 0.0, 5.0, 4.5, 5.0 }, { 0.0, 5.0, 4.5, 5.0 },
				{ 0.0, 5.0, 4.5, 5.0 }, { 0.0, 5.0, 4.5, 5.0 },
				{ 0.0, 5.0, 4.5, 5.0 } };
	}

	public void testTrainingAndEvaluationPartOne() {
		double[][] dataset = { { 0, -1.0, -5.0, -4.5, -5.0 },
				{ 0, -3.0, -6.0, -4.8, -5.0 }, { 0, -1.0, -5.6, -4.5, -5.0 },
				{ 0, -6.0, -7.0, -8.5, -5.0 }, { 0, -9.0, -5.0, -4.5, -9.0 },
				{ 1, 1.0, 5.0, 4.5, 5.0 }, { 1, 3.0, 6.0, 4.8, 5.0 },
				{ 1, 1.0, 5.6, 4.5, 5.0 }, { 1, 6.0, 7.0, 8.5, 5.0 },
				{ 1, 9.0, 5.0, 4.5, 9.0 }, };
		SVMClassifier svm = new SVMClassifier(dataset, new BasicSelection(),
				null);
		svm.train();
		svm.evaluate();
	}

	public void testTrainingAndEvaluationPartTwo() {
		double[][] training = { { 0, -1.0, -5.6, -4.5, -5.0 },
				{ 0, -6.0, -7.0, -8.5, -5.0 }, { 0, -9.0, -5.0, -4.5, -9.0 },
				{ 1, 1.0, 5.0, 4.5, 5.0 }, { 1, 1.0, 5.6, 4.5, 5.0 },
				{ 1, 6.0, 7.0, 8.5, 5.0 }, { 1, 9.0, 5.0, 4.5, 9.0 }, };
		double[][] testing = { { 0, -1.0, -5.0, -4.5, -5.0 },
				{ 1, 3.0, 6.0, 4.8, 5.0 }, { 0, -3.0, -6.0, -4.8, -5.0 }, };
		SVMClassifier svm = new SVMClassifier();
		svm.train(training);
		svm.evaluate(testing);
	}

}
