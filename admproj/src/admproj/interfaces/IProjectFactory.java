/**
 * 
 */
package admproj.interfaces;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import admproj.WorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

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

	public IDbWindowSetResults getWindowResultSet() throws SQLException,
			InterruptedException;

	public IStatSet getStatSet(double[] stats);

	public IParamSet getParamSet(IStatSet[] statSets, int paramId);

	public IWavelengthSet getWaveSet(IParamSet[] paramSets, int waveId);

	public IWindowSet getWindowSet(IWavelengthSet[] waveSets, int classId,
			int windowId);

	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId);

	public Callable<Boolean> getTransformSaveCallable(IWindowSet transformedSet);

	public FutureCallback<IWindowSet> getWindowRetrievalCallBack();

	public FutureCallback<IWindowSet> getTransformCallBack();

	public Callable<IWindowSet> getTransformWinSetCallable(IWindowSet inputSet);

	public WorkSupervisor getSuper();
}
