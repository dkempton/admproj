package admproj;

import java.util.LinkedList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.ListeningExecutorService;

import dbconnect.interfaces.IDbCon;

public class TestSVMClassifierWorkSupervisor {

	private ListeningExecutorService executor;
	private IDbCon dbCon;
	private IProjectFactory factory;

	@Before
	public void setUp() {
		executor = Mockito.mock(ListeningExecutorService.class);
		dbCon = Mockito.mock(IDbCon.class);
		factory = Mockito.mock(IProjectFactory.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSVMClassifierWorkSupervisor_nullExecutor() {
		ListeningExecutorService executor = null;
		new SVMClassifierWorkSupervisor(executor, dbCon, factory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSVMClassifierWorkSupervisor_nullDbCon() {
		IDbCon dbCon = null;
		new SVMClassifierWorkSupervisor(executor, dbCon, factory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSVMClassifierWorkSupervisor_nullFactory() {
		IProjectFactory factory = null;
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		Assert.assertTrue(supervisor != null);
	}

	@Test
	public void testSVMClassifierWorkSupervisor() {
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		Assert.assertTrue(supervisor != null);
	}

	@Test
	public void testOnFailure() {
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		supervisor.notFull = Mockito.mock(Condition.class);
		LinkedList<FutureTask<Boolean>> classifyTaskList = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		classifyTaskList.add(futureTask);
		supervisor.classifyTaskList = classifyTaskList;
		supervisor.onFailure(new Throwable("some error"));
		Mockito.verify(supervisor.notFull, Mockito.times(1)).signal();
	}

	@Test
	public void testOnSuccess() {
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		LinkedList<FutureTask<Boolean>> classifyTaskList = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		supervisor.notFull = Mockito.mock(Condition.class);
		classifyTaskList.add(futureTask);
		supervisor.classifyTaskList = classifyTaskList;
		supervisor.onSuccess(true);
		Mockito.verify(supervisor.notFull, Mockito.times(1)).signal();
	}

	@Test
	public void testHandleClassificationTaskFinished() {
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		LinkedList<FutureTask<Boolean>> classifyTaskList = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		supervisor.notFull = Mockito.mock(Condition.class);
		supervisor.doneProcessing = Mockito.mock(Condition.class);
		classifyTaskList.add(futureTask);
		supervisor.classifyTaskList = classifyTaskList;
		supervisor.doneFetching = false;
		supervisor.handleClassificationTaskFinished(true);
		Mockito.verify(supervisor.notFull, Mockito.times(1)).signal();
		Mockito.verify(supervisor.doneProcessing, Mockito.times(0)).signal();
	}

	@Test
	public void testHandleClassificationTaskFinished_doneFetching() {
		SVMClassifierWorkSupervisor supervisor = new SVMClassifierWorkSupervisor(executor, dbCon, factory);
		LinkedList<FutureTask<Boolean>> classifyTaskList = new LinkedList<FutureTask<Boolean>>();
		@SuppressWarnings("unchecked")
		FutureTask<Boolean> futureTask = Mockito.mock(FutureTask.class);
		Mockito.when(futureTask.isDone()).thenReturn(true);
		supervisor.notFull = Mockito.mock(Condition.class);
		supervisor.doneProcessing = Mockito.mock(Condition.class);
		classifyTaskList.add(futureTask);
		supervisor.classifyTaskList = classifyTaskList;
		supervisor.doneFetching = true;
		supervisor.handleClassificationTaskFinished(true);
		Mockito.verify(supervisor.notFull, Mockito.times(1)).signal();
		Mockito.verify(supervisor.doneProcessing, Mockito.times(1)).signal();
	}

}
