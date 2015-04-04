package wavelets.interfaces;

/**
 *
 * Wavelet interface
 * 
 * The wavelet base class supplies the common functions power2 and
 * log2 and defines the abstract methods for the derived classes.
 * 
 * @author Ian Kaplan
 * @see Wavelets Made Easy by Yves Nieverglt, Birkhauser, 1999
 */
public interface IWavelet {

	/**
	 *
	 Function for calculating a wavelet function.
	 * 
	 * @param values
	 *            Calculate the wavelet function from the values array.
	 */
	public double[] calcWavelet(double[] values);

}
