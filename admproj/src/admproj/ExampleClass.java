package admproj;
/**
 * @author Dustin Kempton
 * @version 1
 *
 */

public class ExampleClass implements IExampleInterface {

	@Override
	public void firstMethod(String input) {
		System.out.println("Hello From Example Class");
		System.out.println("Input: "+input);
	}
	
}
