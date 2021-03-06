/**
 * 
 */
package datatypes.interfaces;

/**
 * @author dkempton1
 *
 */
public interface IParamSet {
	public int size();

	public IStatSet[] getAllStatSets();

	public IStatSet getStatSet(int idx);

	public int getParamId();
}
