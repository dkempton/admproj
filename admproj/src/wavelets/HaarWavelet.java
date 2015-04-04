package wavelets;

import java.util.Vector;

import wavelets.interfaces.IWavelet;

/**
 *
 <p>
 * Class simple_haar
 * 
 * <p>
 * This object calcalculates the "ordered fast Haar wavelet transform". The
 * algorithm used is the a simple Haar wavelet algorithm that does <u>not</u>
 * calculate the wavelet transform in-place. The function works on Java double
 * values.
 * <p>
 * The wavelet_calc function is passed an <b>array</b> of doubles from which it
 * calculates the Haar wavelet transform. The transform is not calculated in
 * place. The result consists of a single value and a Vector of coefficients
 * arrays, ordered by increasing frequency. The number of data points in the
 * data used to calculate the wavelet must be a power of two.
 * <p>
 * The Haar wavelet transform is based on calculating the Haar step function and
 * the Haar wavelet from two adjacent values. For an array of values S0, S1, S2
 * .. Sn, the step function and wavelet are calculated as follows for two
 * adjacent points, S0 and S1:
 * 
 * <pre>
 *       <i>HaarStep</i> = (S0 + S1)/2  <i>// average of S0 and S1</i>
 *       <i>HaarWave</i> = (S0 - S1)/2  <i>// average difference of S0 and S1</i>
 * </pre>
 * 
 * <p>
 * This yields two vectors: <b>a</b>, which contains the <i>HaarStep</i> values
 * and <b>c</b>, which contains the <i>HaarWave</i> values.
 * 
 * <p>
 * The result of the <tt>wavelet_calc</tt> is the single Haar value and a set of
 * coefficients. There will be ceil( log<sub>2</sub>( values.length() ))
 * coefficients.
 * 
 * @author Ian Kaplan
 * @see <i>Wavelets Made Easy by Yves Nieverglt, Birkhauser, 1999</i>
 * 
 * 
 *      <h4>
 *      Copyright and Use</h4>
 * 
 *      <p>
 *      You may use this source code without limitation and without fee as long
 *      as you include:
 *      </p>
 *      <blockquote> This software was written and is copyrighted by Ian Kaplan,
 *      Bear Products International, www.bearcave.com, 2001. </blockquote>
 *      <p>
 *      This software is provided "as is", without any warrenty or claim as to
 *      its usefulness. Anyone who uses this source code uses it at their own
 *      risk. Nor is any support provided by Ian Kaplan and Bear Products
 *      International.
 *      <p>
 *      Please send any bug fixes or suggested source changes to:
 * 
 *      <pre>
 *      iank@bearcave.com
 * </pre>
 */

public class HaarWavelet implements IWavelet {
	private double haar_value;
	private Vector coefficient;
	private double [] data;
	private double haarValue;
	

	@Override
	public double[] calcWavelet(double[] values) {
		assert values != null;
		data = values;
		coefficient = new Vector();
		haar_value  = calcHaarValue(values);
		return reverseCoef();
	}
	
	/**
	 * 
	 Recursively calculate the Haar transform. The result of the Haar
	 * transform is a single integer value and a Vector of coefficients. The
	 * coefficients are calculated from the highest to the lowest frequency.
	 * <p>
	 * The number of elements in <tt>values</tt> must be a power of two.
	 */
	private double calcHaarValue(double[] values) {
		double retVal;

		double[] a = new double[values.length / 2];
		double[] c = new double[values.length / 2];

		for (int i = 0, j = 0; i < values.length; i += 2, j++) {
			a[j] = (values[i] + values[i + 1]) / 2;
			c[j] = (values[i] - values[i + 1]) / 2;
		}
		coefficient.addElement(c);

		if (a.length == 1)
			retVal = a[0];
		else
			retVal = calcHaarValue(a);

		return retVal;
	} // haar_calc
	
	
	/**
	 * The Haar transform coefficients are generated from the longest
	 * coefficient vector (highest frequency) to the shortest (lowest
	 * frequency). However, the reverse Haar transform and the display of the
	 * values uses the coefficients from the lowest to the highest frequency.
	 * This function reverses the coefficient order, so they will be ordered
	 * from lowest to highest frequency.
	 */
	private double [] reverseCoef() {
		int size = coefficient.size();
		Object tmp;

		for (int i = 0, j = size - 1; i < j; i++, j--) {
			tmp = coefficient.elementAt(i);
			coefficient.setElementAt(coefficient.elementAt(j), i);
			coefficient.setElementAt(tmp, j);
		} 
		double [] transformed = new double[coefficient.size()];
		for (int index = 0; index < coefficient.size(); index++){
			double value = (double) coefficient.get(index);
			transformed[index] = value;
		}
		return transformed;
	} 

}
