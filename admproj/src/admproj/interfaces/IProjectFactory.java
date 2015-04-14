/**
 * 
 */
package admproj.interfaces;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import admproj.WorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

import datatypes.interfaces.ICoefValues;
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

	public ICoefValues getCoefVals(int clslabel, double[] coefs);

	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId);

	public Callable<Boolean> getTransformSaveCallable(IWindowSet transformedSet);

	public Callable<IWindowSet> getTransformWinSetCallable(IWindowSet inputSet);

	public Callable<ICoefValues> getCoefValuesCallable(int windowId,
			int wavelengthId, int paramId, int statId, int classId);

	public Callable<ICoefValues[]> getCoefValuesArrCallable(int wavelengthId,
			int paramId, int statId) throws InterruptedException;

	public FutureCallback<IWindowSet> getWindowRetrievalCallBack(
			IWorkSupervisor supervisor);

	public FutureCallback<IWindowSet> getTransformCallBack(
			IWorkSupervisor supervisor);

	public FutureCallback<Boolean> getSavedTransfromCallBack(
			IWorkSupervisor supervisor);

	public WorkSupervisor getSuper();
}
