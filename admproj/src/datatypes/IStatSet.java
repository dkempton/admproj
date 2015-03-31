/**
 * 
 */
package datatypes;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public interface IStatSet {
	public int size();
	public double[] getAllStats();
	public double getStat(int idx);
}
