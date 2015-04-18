package classifier.interfaces;

public interface IClassifier {
		
	public void train(double [][] trainingData);
	public double[] evaluate(double [][] testingData);

}
