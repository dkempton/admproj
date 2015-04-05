/**
 * 
 */
package datatypes;

import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public class WindowSet implements IWindowSet {
	IWavelengthSet[] waveSets;
	int classMembership;
	int windowId;

	public WindowSet(IWavelengthSet[] waveSets, int classMembership,
			int windowId) {
		if (waveSets == null)
			throw new IllegalArgumentException(
					"IWavelenghtSet[] waveSets cannot be null in WindowSet constructor");
		this.waveSets = waveSets;
		this.classMembership = classMembership;
		this.windowId = windowId;
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

	@Override
	public int memberOfClass() {
		return this.classMembership;
	}

	@Override
	public int getWindowId() {
		return this.windowId;
	}
}
