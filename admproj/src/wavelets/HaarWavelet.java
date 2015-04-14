package wavelets;

import java.util.Vector;

import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

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
	// private double haar_value;
	@SuppressWarnings("rawtypes")
	private Vector coefficient;
	// private double[] data;
	// private double haarValue;

	SplineInterpolator interp;

	public HaarWavelet() {
		interp = new SplineInterpolator();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public double[] calcWavelet(double[] values) {
		if (values == null)
			throw new IllegalArgumentException(
					"doublep[] values cannot be null in call to calcWavelet in HaarWavelet.");

		// data = values;
		coefficient = new Vector();

		try {
			// find the log base 2 size of the input array
			Log lg = new Log();
			double lgVal = lg.value(values.length) / lg.value(2.0);

			// get the ceiling value of that
			// Ceil cl = new Ceil();
			// double clVal = cl.value(lgVal);

			// actually lets down sample by taking the floor
			Floor fl = new Floor();
			double clVal = fl.value(lgVal);

			// get the power of two value of the ceiling
			Pow pw = new Pow();
			double num = pw.value(2, clVal);

			// add one because the stupid transform requires power of 2 plus 1
			// values
			int intNum = (int) num;
			double[] preTrans = new double[intNum];

			// create an index of values for spline interpolation
			double[] xVals = new double[values.length];
			for (int i = 0; i < values.length; i++) {
				xVals[i] = i;
			}

			// find the spline interpolation for power of 2 values
			PolynomialSplineFunction spline = interp.interpolate(xVals, values);
			double stepSize = (values.length - 1) / (double) intNum;
			for (int i = 0; i < intNum; i++) {
				preTrans[i] = spline.value(stepSize * i);
			}

			// now we can actually calculate the transform
			calcHaarValue(preTrans);
			return reverseCoef();

		} catch (MathIllegalArgumentException e) {
			System.out.println(e);
		}
		return new double[1];
	}

	/**
	 * 
	 Recursively calculate the Haar transform. The result of the Haar
	 * transform is a single integer value and a Vector of coefficients. The
	 * coefficients are calculated from the highest to the lowest frequency.
	 * <p>
	 * The number of elements in <tt>values</tt> must be a power of two.
	 */
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	private double[] reverseCoef() {
		int size = coefficient.size();
		Object tmp;

		for (int i = 0, j = size - 1; i < j; i++, j--) {
			tmp = coefficient.elementAt(i);
			coefficient.setElementAt(coefficient.elementAt(j), i);
			coefficient.setElementAt(tmp, j);
		}
		try {
			int count = 0;
			for (int index = 0; index < coefficient.size(); index++) {
				double[] value = (double[]) coefficient.get(index);
				count += value.length;
			}
			double[] transformed = new double[count];

			int index = 0;
			for (int i = 0; i < coefficient.size(); i++) {
				double[] value = (double[]) coefficient.get(i);
				for (int j = 0; j < value.length; j++) {
					transformed[index++] = value[j];
				}
			}

			return transformed;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return new double[1];
	}

}
