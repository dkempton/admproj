package classifier;

import classifier.interfaces.IClassifier;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
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

	private svm_parameter params;
	private svm_model model;

	public SVMClassifier(svm_parameter params) {
		this.params = params;
	}

	public SVMClassifier(int kernelType, int numFeatures) {
		this.congifureParams();
		this.params.kernel_type = kernelType;
		this.params.gamma = 1.0 / numFeatures;
	}

	public SVMClassifier() {
		congifureParams();
	}

	private void congifureParams() {
		params = new svm_parameter();
		params.probability = 0;
		params.gamma = 0;
		params.nu = 0.5;
		params.C = 1;
		params.svm_type = svm_parameter.C_SVC;
		params.kernel_type = svm_parameter.LINEAR;
		params.cache_size = 200000;
		params.eps = .0001;
		params.p = 0.1;
		params.shrinking = 1;
		params.degree = 3;
		params.coef0 = 0;
		params.nr_weight = 0;
		params.weight_label = new int[0];
		params.weight = new double[0];

	}

	/**
	 * Performs exactly as the above method except it uses the training data
	 * given instead what has been stored in the class.
	 */
	public void train(double[][] trainingData) {
		this.testMatrix(trainingData);
		System.out.println("Training: ");
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
		String errmsg = svm.svm_check_parameter(problem, this.params);

		if (errmsg != null) {
			System.out.println("Error: " + errmsg);
			System.exit(1);
		}
		this.model = svm.svm_train(problem, params);
		System.out.println("\n\n");
	}

	/**
	 * For every element in the testing set get the predicted classification and
	 * adds to the correct or false count. Prints out the averages at the end
	 */
	public double[] evaluate(double[][] testingData) {
		this.testMatrix(testingData);
		System.out.println("Testing: ");
		double correctPredictions = 0, falsePredictions = 0;
		for (double[] features : testingData) {
			// Unpacking and repacking the data for testing
			svm_node[] nodes = new svm_node[features.length - 1];
			for (int i = 1; i < features.length; i++) {
				svm_node node = new svm_node();
				node.index = i;
				node.value = features[i];
				nodes[i - 1] = node;
			}
			double predicted = svm.svm_predict(this.model, nodes);
			// System.out.println("pred: " + predicted);
			correctPredictions += (features[0] == predicted) ? 1 : 0;
			falsePredictions += (features[0] != predicted) ? 1 : 0;
		}
		double avgCorrect = correctPredictions
				/ (correctPredictions + falsePredictions);
		double avgFalse = falsePredictions
				/ (correctPredictions + falsePredictions);
		System.out.println("Percentage Correct: " + avgCorrect);
		System.out.println("Percentage False: " + avgFalse);
		double[] returnValues = { avgCorrect, avgFalse };
		return returnValues;
	}

	/*
	 * Test to see if the input matrix contains enough data for the svm to
	 * function.
	 */
	private void testMatrix(double[][] matrix) throws IllegalArgumentException {
		if (matrix == null) {
			throw new IllegalArgumentException("Dataset cannot be null");
		}
		if (matrix.length < 1)
			throw new IllegalArgumentException("Dataset must contain data");
		int width = matrix[0].length;
		if (width <= 1)
			throw new IllegalArgumentException("Width must be greater than one");
		for (double[] featureVector : matrix) {
			if (featureVector.length != width)
				throw new IllegalArgumentException(
						"Feature vectors are not all of the same size");
		}
	}
}
