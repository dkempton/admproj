package admproj;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import utils.interfaces.IFTestCalc;
import admproj.interfaces.IClassifierWorkSupervisor;
import admproj.interfaces.IFStatCalcWorkSupervisor;
import admproj.interfaces.ITransformWorkSupervisor;
import classifier.interfaces.IClassifier;

import com.google.common.util.concurrent.FutureCallback;

import datatypes.CoefValues;
import datatypes.ParamSet;
import datatypes.StatSet;
import datatypes.WavelengthSet;
import datatypes.interfaces.ICoefSet;
import datatypes.interfaces.ICoefValues;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbWindowSetResults;

public class TestProjectFactory {

	@Test
	public void testProjectFactory() throws Exception {
		ProjectFactory factory = new ProjectFactory();
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory.dbPoolSourc != null);
		Assert.assertTrue(factory.executor != null);
		Assert.assertTrue(factory.dbcon != null);
		Assert.assertTrue(factory.rangeMap != null);
		Assert.assertTrue(factory.lock != null);
	}

	@Test
	public void testFinalize() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		factory.finalize();
	}

	@Test
	public void testGetDbCon() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		Assert.assertTrue(factory.getDbCon() != null);
	}

	@Test
	public void testGetClassifier() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IClassifier classifier = factory.getClassifier(1, 1);
		Assert.assertTrue(classifier != null);
	}

	@Test
	public void testGetWindowResultSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IDbWindowSetResults result = factory.getWindowResultSet();
		Assert.assertTrue(result != null);
	}

	@Test
	public void testGetWinSetCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		Callable<IWindowSet> callable = factory.getWinSetCallable(1, 1);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetTransformSaveCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IWindowSet windowSet = Mockito.mock(IWindowSet.class);
		Callable<Boolean> callable = factory.getTransformSaveCallable(windowSet);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetCalcFValsAndSaveCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ICoefSet coefSet = Mockito.mock(ICoefSet.class);
		ICoefValues[] coefValues = { new CoefValues(1, new double[] { 1.11d }) };
		Mockito.when(coefSet.getCoefs()).thenReturn(coefValues);
		Callable<Boolean> callable = factory.getCalcFValsAndSaveCallable(coefSet);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetSVMTrainTestAndSaveCallabel() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds = new ArrayList<ArrayList<ArrayList<Integer>>>();
		int kCount = 1;
		int kernel = 1;
		Callable<Boolean> callable = factory.getSVMTrainTestAndSaveCallabel(seperatedIds, kCount, kernel);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetCoefValuesCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		Callable<ICoefValues> callable = factory.getCoefValuesCallable(1, 1, 1, 1, 1);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetCoefValuesSetCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		Callable<ICoefSet> callable = factory.getCoefValuesSetCallable(1, 1, 1);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetStatSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IStatSet statSet = factory.getStatSet(new double[] { 1.22 });
		Assert.assertTrue(statSet != null);
		Assert.assertEquals(1, statSet.getAllStats().length);
		Assert.assertEquals(new Double(1.22), new Double(statSet.getAllStats()[0]));
	}

	@Test
	public void testGetParamSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IStatSet[] stateSet = { new StatSet(new double[] { 1.22 }) };
		IParamSet paramSet = factory.getParamSet(stateSet, 1);
		Assert.assertTrue(paramSet != null);
		Assert.assertEquals(1, paramSet.getParamId());
		Assert.assertEquals(stateSet.length, paramSet.getAllStatSets().length);
		Assert.assertEquals(stateSet[0], paramSet.getAllStatSets()[0]);
	}

	@Test
	public void testGetWaveSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IStatSet[] stateSet = { new StatSet(new double[] { 1.22 }) };
		IParamSet[] paramSet = { new ParamSet(stateSet, 1) };
		IWavelengthSet wavelengthSet = factory.getWaveSet(paramSet, 1);
		Assert.assertTrue(wavelengthSet != null);
		Assert.assertEquals(paramSet.length, wavelengthSet.getAllParamSets().length);
		Assert.assertEquals(paramSet[0], wavelengthSet.getAllParamSets()[0]);
		Assert.assertEquals(1, wavelengthSet.getWaveId());
	}

	@Test
	public void testGetWindowSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IStatSet[] stateSet = { new StatSet(new double[] { 1.22 }) };
		IParamSet[] paramSet = { new ParamSet(stateSet, 1) };
		IWavelengthSet[] wavelengthSet = { new WavelengthSet(paramSet, 1) };
		IWindowSet windowSet = factory.getWindowSet(wavelengthSet, 1, 1);
		Assert.assertTrue(windowSet != null);
		Assert.assertEquals(1, windowSet.getWindowId());
		Assert.assertEquals(wavelengthSet.length, windowSet.getAllWavelengthSets().length);
		Assert.assertEquals(wavelengthSet[0], windowSet.getAllWavelengthSets()[0]);
		Assert.assertEquals(1, windowSet.getWindowId());
	}

	@Test
	public void testGetCoefVals() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ICoefValues coefVals = factory.getCoefVals(1, new double[] { 1.22 });
		Assert.assertTrue(coefVals != null);
		Assert.assertEquals(1, coefVals.size());
		Assert.assertEquals(new Double(1.22), new Double(coefVals.getCoeff(0)));
		Assert.assertEquals(1, coefVals.getClassLabel());
	}

	@Test
	public void testGetCoefSet() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ICoefValues[] coefVals = { new CoefValues(1, new double[] { 1.22 }) };
		ICoefSet coefSet = factory.getCoefSet(1, 2, 3, coefVals);
		Assert.assertTrue(coefSet != null);
		Assert.assertEquals(new Double(1.22), new Double(coefSet.getCoefs()[0].getCoeff(0)));
		Assert.assertEquals(1, coefSet.getCoefs()[0].getClassLabel());
		Assert.assertEquals(1, coefSet.getWavelengthId());
		Assert.assertEquals(2, coefSet.getParamId());
		Assert.assertEquals(3, coefSet.getStatId());
	}

	@Test
	public void testGetWindowRetrievalCallBack() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ITransformWorkSupervisor supervisor = Mockito.mock(ITransformWorkSupervisor.class);
		FutureCallback<IWindowSet> callback = factory.getWindowRetrievalCallBack(supervisor);
		Assert.assertTrue(callback != null);
	}

	@Test
	public void testGetTransformCallBack() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ITransformWorkSupervisor supervisor = Mockito.mock(ITransformWorkSupervisor.class);
		FutureCallback<IWindowSet> callback = factory.getTransformCallBack(supervisor);
		Assert.assertTrue(callback != null);
	}

	@Test
	public void testGetSavedTransfromCallBack() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		ITransformWorkSupervisor supervisor = Mockito.mock(ITransformWorkSupervisor.class);
		FutureCallback<Boolean> callback = factory.getSavedTransfromCallBack(supervisor);
		Assert.assertTrue(callback != null);
	}

	@Test
	public void testGetCoefValuesRetreivalCallBack() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IFStatCalcWorkSupervisor supervisor = Mockito.mock(IFStatCalcWorkSupervisor.class);
		FutureCallback<ICoefSet> callback = factory.getCoefValuesRetreivalCallBack(supervisor);
		Assert.assertTrue(callback != null);
	}

	@Test
	public void testGetSavedFStatValsCallBack() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IFStatCalcWorkSupervisor supervisor = Mockito.mock(IFStatCalcWorkSupervisor.class);
		FutureCallback<Boolean> callback = factory.getSavedFStatValsCallBack(supervisor);
		Assert.assertTrue(callback != null);
	}

	@Test
	public void testGetTransformWinSetCallable() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IWindowSet windowSet = Mockito.mock(IWindowSet.class);
		Callable<IWindowSet> callable = factory.getTransformWinSetCallable(windowSet);
		Assert.assertTrue(callable != null);
	}

	@Test
	public void testGetTransformSuper() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		TransformWorkSupervisor superviser = factory.getTransformSuper();
		Assert.assertTrue(superviser != null);
		Assert.assertEquals(factory.executor, superviser.executor);
		Assert.assertEquals(factory.dbcon, superviser.dbcon);
	}

	@Test
	public void testGetFStatCalcSuper() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IFStatCalcWorkSupervisor superviser = factory.getFStatCalcSuper();
		Assert.assertTrue(superviser != null);
	}

	@Test
	public void testGetClassifierSuper() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IClassifierWorkSupervisor superviser = factory.getClassifierSuper();
		Assert.assertTrue(superviser != null);
	}

	@Test
	public void testGetFValCalculator() throws Throwable {
		ProjectFactory factory = new ProjectFactory();
		IFTestCalc calc = factory.getFValCalculator();
		Assert.assertTrue(calc != null);
	}
}
