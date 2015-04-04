package wavelets;

import org.apache.commons.math3.transform.DctNormalization;
import org.apache.commons.math3.transform.FastCosineTransformer;
import org.apache.commons.math3.transform.TransformType;

import wavelets.interfaces.IWavelet;

public class DCT implements IWavelet {

	@Override
	public double[] calcWavelet(double[] values) {
		FastCosineTransformer dct = new FastCosineTransformer(DctNormalization.ORTHOGONAL_DCT_I);
		return dct.transform(values, TransformType.FORWARD);
	}
	
}
