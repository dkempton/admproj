package datatypes;

import datatypes.interfaces.ICoefSet;
import datatypes.interfaces.ICoefValues;

public class CoefSet implements ICoefSet {

	int wavelengthId;
	int paramId;
	int statId;
	ICoefValues[] coefs;

	public CoefSet(int wavelengthId, int paramId, int statId,
			ICoefValues[] coefs) {
		if (coefs == null)
			throw new IllegalArgumentException(
					"ICoefValues[] coefs cannot be null in CoefSet constructor.");
		this.wavelengthId = wavelengthId;
		this.paramId = paramId;
		this.statId = statId;
		this.coefs = coefs;
	}

	@Override
	public ICoefValues[] getCoefs() {
		return this.coefs;
	}

	@Override
	public int getWavelengthId() {
		return this.wavelengthId;
	}

	@Override
	public int getParamId() {
		return this.paramId;
	}

	@Override
	public int getStatId() {
		return this.statId;
	}

}
