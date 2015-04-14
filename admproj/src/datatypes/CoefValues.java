package datatypes;

import datatypes.interfaces.ICoefValues;

public class CoefValues implements ICoefValues {

	int cls_label;
	double[] coefs;

	public CoefValues(int cls_label, double[] coefs) {
		if (coefs == null)
			throw new IllegalArgumentException(
					"double[] coefs cannot be null in CeofValues constructor.");
		this.cls_label = cls_label;
		this.coefs = coefs;
	}

	@Override
	public int size() {
		return this.coefs.length;
	}

	@Override
	public double getCoeff(int indx) {
		return coefs[indx];
	}

	@Override
	public int getClassLabel() {
		return this.cls_label;
	}

}
