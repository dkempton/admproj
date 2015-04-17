package classifier.interfaces;

public interface IClassifier {
		
	public void train(double [][] trainingData);
	public void evaluate(double [][] testingData);

}
