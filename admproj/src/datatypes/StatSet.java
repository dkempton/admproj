/**
 * 
 */
package datatypes;

import datatypes.interfaces.IStatSet;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public class StatSet implements IStatSet {
	private double[] stats;

	public StatSet(double[] stats) {
		assert stats != null : "stats cannot be null in StatSet constructor.";
		this.stats = stats;
	}

	@Override
	public int size() {
		return this.stats.length;
	}

	/**
	 * @return double array of stat values from
	 */
	@Override
	public double[] getAllStats() {

		return this.stats;
	}

	@Override
	public double getStat(int idx) {
		return this.stats[idx];
	}
}
