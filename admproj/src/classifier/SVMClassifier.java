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
 * 
 *         This class expects that the first index in the dataset or training
 *         and testing sets is the class label.
 * 
 */
public class SVMClassifier implements IClassifier {

	private double[][] trainingData;
	private double[][] testingData;
	private svm_parameter params;
	private svm_model model;
	private int width = -1;

	public SVMClassifier(double[][] dataset, IDataSelection selector,
			svm_parameter params) {
		assert dataset.length > 0 && dataset != null : "Dataset must contain data";
		int width = dataset[0].length;
		assert width > 1 : "Width must be greater than one";
		for (double[] featureVector : dataset) {
			assert featureVector.length == width : "Feature vectors are not all of the same size";
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

	public SVMClassifier(double[][] dataset, svm_parameter params) {
		this(dataset, new BasicSelection(), params);
	}

	public SVMClassifier(double[][] dataset) {
		this(dataset, new BasicSelection(), null);
	}
	
	
	public SVMClassifier(svm_parameter params) {
		this.params = params;
	}

	public SVMClassifier() {
		congifureParams();
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
		// Training portion - unpacking the training data and
		// reloads it into the format needed for svm to work
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
		// Training the svm
		model = svm.svm_train(problem, params);
	}

	/**
	 * For every element in the testing set get the predicted classification and
	 * adds to the correct or false count. Prints out the averages at the end
	 */
	public void evaluate() {
		double correctPredictions = 0, falsePredictions = 0;
		for (double[] features : testingData) {
			// Unpacking and repacking the data for testing
			svm_node[] nodes = new svm_node[features.length - 1];
			for (int i = 1; i < features.length; i++) {
				svm_node node = new svm_node();
				node.index = 1;
				node.value = features[i];
				nodes[i - 1] = node;
			}
			double predicted = svm.svm_predict(model, nodes);
			correctPredictions += (features[0] == predicted) ? 1 : 0;
			falsePredictions += (features[0] != predicted) ? 1 : 0;
		}
		double avgCorrect = correctPredictions
				/ (correctPredictions + falsePredictions);
		double avgFalse = falsePredictions
				/ (correctPredictions + falsePredictions);
		System.out.println("Percentage Correct: " + avgCorrect);
		System.out.println("Percentage False: " + avgFalse);
	}

	public void addTrainingData(double[][] trainingData) {
		assert trainingData.length > 0 && trainingData != null : "Dataset must contain data";
		if (width == -1){
			width = trainingData[0].length;
			assert width > 1 : "Width must be greater than one";
		}
		for (double[] featureVector : trainingData) {
			assert featureVector.length == width : "Feature vectors are not all of the same size";
		}
		this.trainingData = trainingData;
	}

	public void addTestingData(double[][] testingData) {
		assert trainingData.length > 0 && trainingData != null : "Dataset must contain data";
		if (width == -1){
			width = trainingData[0].length;
			assert width > 1 : "Width must be greater than one";
		}
		for (double[] featureVector : trainingData) {
			assert featureVector.length == width : "Feature vectors are not all of the same size";
		}
		this.testingData = testingData;
	}
}
