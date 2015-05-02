package admproj;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import datatypes.interfaces.ICoefSet;
import exceptions.InvalidConfigException;

public class TestFStatCalcWorkSupervisor {

	private FStatCalcWorkSupervisor fStatCalcWorkSupervisor = null;
	private int[] wavelengthIds = new int[1];
	private int[] paramIds = new int[1];
	private int[] statIds = new int[1];
	private ListeningExecutorService executor;
	private IProjectFactory factory;

	@Before
	public void setUp() throws InvalidConfigException {
		wavelengthIds[0] = 100;
		paramIds[0] = 10;
		statIds[0] = 50;
		executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		factory = new ProjectFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFStatCalcWorkSupervisor_withExcecuterNull() throws InvalidConfigException {
		ListeningExecutorService executor = null;
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFStatCalcWorkSupervisor_withFactoryNull() throws InvalidConfigException {
		IProjectFactory factory = null;
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFStatCalcWorkSupervisor_withWavelengthIdsNull() throws InvalidConfigException {
		ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		int[] wavelengthIds = null;
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFStatCalcWorkSupervisor_withParamIdsNull() throws InvalidConfigException {
		int[] paramIds = null;
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFStatCalcWorkSupervisor_withStatIdsNull() throws InvalidConfigException {
		int[] statIds = null;
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	@Test
	public void testFStatCalcWorkSupervisor() throws InvalidConfigException {
		ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
	}

	/*
	 * @Test public void testRun() throws InvalidConfigException { int[]
	 * wavelengthIds = { 100 }; int[] paramIds = { 10 }; int[] statIds = { 50 };
	 * ListeningExecutorService executor =
	 * Mockito.mock(ListeningExecutorService.class); IProjectFactory factory =
	 * Mockito.mock(ProjectFactory.class);
	 * Mockito.when(factory.getCoefValuesSetCallable(wavelengthIds[0],
	 * paramIds[0], statIds[0])).thenReturn(Callable.class); ListenableFuture
	 * retrievalTask = Mockito.mock(ListenableFuture.class);
	 * Mockito.when(executor
	 * .submit(Mockito.any(Runnable.class))).thenReturn(retrievalTask);
	 * fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory,
	 * wavelengthIds, paramIds, statIds); fStatCalcWorkSupervisor.run(); }
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleCoefsArrFetched() throws InvalidConfigException {
		executor = Mockito.mock(ListeningExecutorService.class);
		factory = Mockito.mock(ProjectFactory.class);
		ICoefSet coefs = Mockito.mock(ICoefSet.class);
		Callable<Boolean> callable = Mockito.mock(Callable.class);
		Mockito.when(factory.getCalcFValsAndSaveCallable(coefs)).thenReturn(callable);
		FutureCallback<Boolean> futureCallback = Mockito.mock(FutureCallback.class);
		ListenableFutureTask<Boolean> saveTask = Mockito.mock(ListenableFutureTask.class);
		Mockito.when(executor.submit(callable)).thenReturn(saveTask);
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
		Mockito.when(factory.getSavedFStatValsCallBack(fStatCalcWorkSupervisor)).thenReturn(futureCallback);

		Assert.assertEquals(0, fStatCalcWorkSupervisor.saveFStatTaskList.size());
		fStatCalcWorkSupervisor.handleCoefsArrFetched(coefs);
		Assert.assertEquals(1, fStatCalcWorkSupervisor.saveFStatTaskList.size());
	}

	@Test
	public void testHandleFValsSaved_taskDone() throws InvalidConfigException {
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
		LinkedList<FutureTask<Boolean>> futureTasks = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		futureTasks.add(futureTask);
		fStatCalcWorkSupervisor.saveFStatTaskList = futureTasks;
		Assert.assertEquals(1, fStatCalcWorkSupervisor.saveFStatTaskList.size());
		fStatCalcWorkSupervisor.handleFValsSaved(true);
		Assert.assertEquals(0, fStatCalcWorkSupervisor.saveFStatTaskList.size());
	}

	@Test
	public void testHandleFValsSaved_taskNotDone() throws InvalidConfigException {
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
		LinkedList<FutureTask<Boolean>> futureTasks = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(false);
		futureTasks.add(futureTask);
		fStatCalcWorkSupervisor.saveFStatTaskList = futureTasks;
		Assert.assertEquals(1, fStatCalcWorkSupervisor.saveFStatTaskList.size());
		fStatCalcWorkSupervisor.handleFValsSaved(true);
		Assert.assertEquals(1, fStatCalcWorkSupervisor.saveFStatTaskList.size());
	}

	@Test
	public void testHandleFValsSaved_notDoneFetching() throws InvalidConfigException {
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
		LinkedList<FutureTask<Boolean>> futureTasks = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		futureTasks.add(futureTask);
		Condition doneProcessing = Mockito.mock(Condition.class);
		fStatCalcWorkSupervisor.saveFStatTaskList = futureTasks;
		fStatCalcWorkSupervisor.doneFetching = false;
		fStatCalcWorkSupervisor.doneProcessing = doneProcessing;
		fStatCalcWorkSupervisor.handleFValsSaved(true);
		Mockito.verify(fStatCalcWorkSupervisor.doneProcessing, Mockito.times(0)).signal();
	}

	@Test
	public void testHandleFValsSaved_doneFetching() throws InvalidConfigException {
		fStatCalcWorkSupervisor = new FStatCalcWorkSupervisor(executor, factory, wavelengthIds, paramIds, statIds);
		LinkedList<FutureTask<Boolean>> futureTasks = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		futureTasks.add(futureTask);
		Condition doneProcessing = Mockito.mock(Condition.class);
		fStatCalcWorkSupervisor.saveFStatTaskList = futureTasks;
		fStatCalcWorkSupervisor.doneFetching = true;
		fStatCalcWorkSupervisor.doneProcessing = doneProcessing;
		fStatCalcWorkSupervisor.handleFValsSaved(true);
		Mockito.verify(fStatCalcWorkSupervisor.doneProcessing, Mockito.times(1)).signal();
	}
}
