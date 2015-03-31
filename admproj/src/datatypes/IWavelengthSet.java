/**
 * 
 */
package datatypes;


/**
 * @author dkempton1
 *
 */
public interface IWavelengthSet {
	public int size();
	public IParamSet[] getAllParamSets();
	public IParamSet getParamSet(int idx);
}
