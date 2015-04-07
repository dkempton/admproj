package wavelets;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.transform.DctNormalization;
import org.apache.commons.math3.transform.FastCosineTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.analysis.function.*;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import wavelets.interfaces.IWavelet;

public class DCT implements IWavelet {
	FastCosineTransformer dct;
	SplineInterpolator interp;

	public DCT() {
		dct = new FastCosineTransformer(DctNormalization.ORTHOGONAL_DCT_I);
		interp = new SplineInterpolator();

	}

	@Override
	public double[] calcWavelet(double[] values) {
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
			int intNum = (int) num + 1;
			double[] preTrans = new double[intNum];

			// create an index of values for spline interpolation
			double[] xVals = new double[values.length];
			for (int i = 0; i < values.length; i++) {
				xVals[i] = i;
			}

			// find the spline interpolation for power of 2+1 values
			PolynomialSplineFunction spline = interp.interpolate(xVals, values);
			double stepSize = (values.length - 1) / (double) intNum;
			for (int i = 0; i < intNum; i++) {
				preTrans[i] = spline.value(stepSize * i);
			}

			// now we can actually calculate the transform
			double[] vals = dct.transform(preTrans, TransformType.FORWARD);
			return vals;
		} catch (MathIllegalArgumentException e) {
			System.out.println(e);
		}
		return new double[1];
	}

}
