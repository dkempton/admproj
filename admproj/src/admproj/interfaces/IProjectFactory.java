/**
 * 
 */
package admproj.interfaces;

import java.util.concurrent.Callable;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public interface IProjectFactory {
	public IDbCon getDbCon();

	public IDbWindowSetResults getWindowResultSet();
	
	public IStatSet getStatSet(double[] stats);
	public IParamSet getParamSet(IStatSet[] statSets);
	public IWavelengthSet getWaveSet(IParamSet[] paramSets);
	public IWindowSet getWindowSet(IWavelengthSet[] waveSets);
	

	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId);
}
