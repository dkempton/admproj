/**
 * 
 */
package datatypes;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public class ParamSet implements IParamSet {
	private IStatSet[] paramStats;
	int paramId;

	public ParamSet(IStatSet[] paramStats, int paramId) {
		if (paramStats == null)
			throw new IllegalArgumentException(
					"IStatSet[] cannot be null in ParamSet constructor.");
		this.paramStats = paramStats;
		this.paramId = paramId;
	}

	@Override
	public int size() {

		return this.paramStats.length;
	}

	@Override
	public IStatSet[] getAllStatSets() {

		return this.paramStats;
	}

	@Override
	public IStatSet getStatSet(int idx) {
		return this.paramStats[idx];
	}

	@Override
	public int getParamId() {
		return this.paramId;
	}

}
