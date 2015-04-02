/**
 * 
 */
package datatypes;

import com.sun.istack.internal.NotNull;

import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public class WindowSet implements IWindowSet {
	IWavelengthSet[] waveSets;

	public WindowSet(@NotNull IWavelengthSet[] waveSets) {
		this.waveSets = waveSets;
	}

	@Override
	public int size() {
		return this.waveSets.length;
	}

	@Override
	public IWavelengthSet[] getAllWavelengthSets() {
		return this.waveSets;
	}

	@Override
	public IWavelengthSet getWavlengthSet(int waveId) {
		return this.waveSets[waveId];
	}
}
