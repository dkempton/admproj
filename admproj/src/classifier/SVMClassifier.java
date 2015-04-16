package classifier;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import classifier.interfaces.IClassifier;
import dataselection.BasicSelection;
import dataselection.interfaces.IDataSelection;
/**
 * 
 * @author thaddeus
 * This class 
 */
public class SVMClassifier implements IClassifier {

	private double[][] trainingData;
	private double[][] testingData;
	private svm_parameter params;
	private svm_model model;

	public SVMClassifier(double[][] dataset, IDataSelection selector,
			svm_parameter params) {
		assert dataset.length > 0 && dataset != null : "Dataset must contain data";
		int width1 = dataset[0].length;
		assert width1 > 0 : "Width must be greater than zero";
		for (double[] featureVector : dataset) {
			assert featureVector.length == width1 : "Feature vectors are not all of the same size";
		}
		if (params == null) {
			congifureParams();
		}
		trainingData = selector.getTrainingData()[0];
		testingData = selector.getTestingData()[0];
		this.params = params;
	}

	public SVMClassifier(double[][] dataset, IDataSelection selector) {
		this(dataset, selector, null);
	}

	public SVMClassifier(double[][] dataset, svm_parameter params,
			int totalNumberOfClasses) {
		this(dataset, new BasicSelection(), params);
	}

	public SVMClassifier(double[][] dataset) {
		this(dataset, new BasicSelection(), null);
	}

	private void congifureParams() {
		params = new svm_parameter();
		params.probability = 1;
		params.gamma = .5;
		params.nu = .5;
		params.C = 1;
		params.svm_type = svm_parameter.C_SVC;
		params.kernel_type = svm_parameter.LINEAR;
		params.cache_size = 200000;
		params.eps = .002;
	}

	@Override
	public void train() {
		// Training portion - unpacks the data and reloads it into
		// the format needed for svm to work
		svm_problem problem = new svm_problem();
		int dataCount = trainingData.length;
		problem.y = new double[dataCount];
		problem.l = dataCount;
		problem.x = new svm_node[dataCount][];
		for (int i = 0; i < dataCount; i++) {
			double[] features = trainingData[i];
			problem.x[i] = new svm_node[features.length - 1];
			for (int j = 1; j < features.length; j++) {
				svm_node node = new svm_node();
				node.index = j;
				node.value = features[j];
				problem.x[i][j - 1] = node;
			}
			problem.y[i] = features[0];
		}
		model = svm.svm_train(problem, params);
	}

	public void evaluate() {
		double correctPredictions = 0, falsePredictions = 0;

		for (double[] features : testingData) {
			svm_node[] nodes = new svm_node[features.length - 1];
			for (int i = 1; i < features.length; i++) {
				svm_node node = new svm_node();
				node.index = 1;
				node.value = features[i];
				nodes[i - 1] = node;
				double predicted = svm.svm_predict(model, nodes);
				correctPredictions += (features[0] == predicted) ? 1 : 0;
				falsePredictions += (features[0] != predicted) ? 1 : 0;
			}
		}
		double avgCorrect = correctPredictions
				/ (correctPredictions + falsePredictions);
		double avgFalse = falsePredictions
				/ (correctPredictions + falsePredictions);
		System.out.println("Percentage Correct: " + avgCorrect);
		System.out.println("Percentage False: " + avgFalse);
	}

}
