/**
 * 
 */
package admproj.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import utils.interfaces.IFTestCalc;
import admproj.TransformWorkSupervisor;
import classifier.interfaces.IClassifier;

import com.google.common.util.concurrent.FutureCallback;

import datatypes.interfaces.ICoefSet;
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

	public IClassifier getClassifier( int kernelId);

	public IDbWindowSetResults getWindowResultSet() throws SQLException,
			InterruptedException;

	public IStatSet getStatSet(double[] stats);

	public IParamSet getParamSet(IStatSet[] statSets, int paramId);

	public IWavelengthSet getWaveSet(IParamSet[] paramSets, int waveId);

	public IWindowSet getWindowSet(IWavelengthSet[] waveSets, int classId,
			int windowId);

	public ICoefValues getCoefVals(int clslabel, double[] coefs);

	public ICoefSet getCoefSet(int wavelenghtId, int paramId, int statId,
			ICoefValues[] coefs);

	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId);

	public Callable<Boolean> getTransformSaveCallable(IWindowSet transformedSet);

	public Callable<Boolean> getCalcFValsAndSaveCallable(ICoefSet coefSet);

	public Callable<Boolean> getSVMTrainTestAndSaveCallabel(
			ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds, int kCount,
			int kernel);

	public Callable<IWindowSet> getTransformWinSetCallable(IWindowSet inputSet);

	public Callable<ICoefValues> getCoefValuesCallable(int windowId,
			int wavelengthId, int paramId, int statId, int classId);

	public Callable<ICoefSet> getCoefValuesSetCallable(int wavelengthId,
			int paramId, int statId) throws InterruptedException;

	public FutureCallback<IWindowSet> getWindowRetrievalCallBack(
			ITransformWorkSupervisor supervisor);

	public FutureCallback<IWindowSet> getTransformCallBack(
			ITransformWorkSupervisor supervisor);

	public FutureCallback<Boolean> getSavedTransfromCallBack(
			ITransformWorkSupervisor supervisor);

	public FutureCallback<ICoefSet> getCoefValuesRetreivalCallBack(
			IFStatCalcWorkSupervisor supervisor);

	public FutureCallback<Boolean> getSavedFStatValsCallBack(
			IFStatCalcWorkSupervisor supervisor);

	public TransformWorkSupervisor getTransformSuper();

	public IFStatCalcWorkSupervisor getFStatCalcSuper();
	
	public IClassifierWorkSupervisor getClassifierSuper();

	public IFTestCalc getFValCalculator();
}
