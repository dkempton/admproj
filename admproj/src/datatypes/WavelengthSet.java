/**
 * 
 */
package datatypes;

import com.sun.istack.internal.NotNull;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IWavelengthSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public class WavelengthSet implements IWavelengthSet {
	IParamSet[] params;
	public WavelengthSet(@NotNull IParamSet[] params){
		this.params = params;
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

}
