/**
 * 
 */
package datatypes;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IWavelengthSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public class WavelengthSet implements IWavelengthSet {
	IParamSet[] params;
	int waveId;

	public WavelengthSet(IParamSet[] params, int waveId) {
		if (params == null)
			throw new IllegalArgumentException(
					"IParamSet[] params cannot be null in WavelenghtSet constructor.");
		this.params = params;
		this.waveId = waveId;
	}

	@Override
	public int size() {
		return this.params.length;
	}

	@Override
	public IParamSet[] getAllParamSets() {
		return this.params;
	}

	@Override
	public IParamSet getParamSet(int idx) {
		return this.params[idx];
	}

	@Override
	public int getWaveId() {
		return this.waveId;
	}
}
