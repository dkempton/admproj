package admproj;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import admproj.interfaces.IFStatCalcWorkSupervisor;
import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import datatypes.interfaces.ICoefSet;

public class FStatCalcWorkSupervisor implements IFStatCalcWorkSupervisor {

	ListeningExecutorService executor;
	IProjectFactory factory;

	LinkedList<FutureTask<ICoefSet>> fetchTaskList;
	LinkedList<FutureTask<Boolean>> saveFStatTaskList;

	private static final int MAX_FETCH = 10;
	Lock lock;
	Condition notFull;
	Condition doneProcessing;
	boolean doneFetching = false;

	int[] wavelengthIds;
	int[] paramIds;
	int[] statIds;

	FStatCalcWorkSupervisor(ListeningExecutorService executor,
			IProjectFactory factory, int[] wavelengthIds, int[] paramIds,
			int[] statIds) {
		if (executor == null)
			throw new IllegalArgumentException(
					"ThreadPoolExecutor cannot be null in FStatCalcWorkSupervisor constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in FStatCalcWorkSupervisor");
		if (wavelengthIds == null)
			throw new IllegalArgumentException(
					"int[] wavelengthIds cannot be null in FStatCalcWorkSupervisor constructor.");
		if (paramIds == null)
			throw new IllegalArgumentException(
					"int[] paramIds cannot be null in FStatCalcWorkSupervisor constructor.");
		if (statIds == null)
			throw new IllegalArgumentException(
					"int[] statIds cannot be null in FStatCalcWorkSupervisor constructor.");

		this.executor = executor;
		this.factory = factory;

		this.wavelengthIds = wavelengthIds;
		this.paramIds = paramIds;
		this.statIds = statIds;

		this.fetchTaskList = new LinkedList<FutureTask<ICoefSet>>();
		this.saveFStatTaskList = new LinkedList<FutureTask<Boolean>>();

		this.lock = new ReentrantLock();
		this.doneProcessing = this.lock.newCondition();
		this.notFull = this.lock.newCondition();
	}

	@Override
	public void run() {

		for (int i = 0; i < this.wavelengthIds.length; i++) {
			for (int j = 0; j < this.paramIds.length; j++) {
				for (int k = 0; k < this.statIds.length; k++) {
					this.createRetrievalTask(this.wavelengthIds[i],
							this.paramIds[j], this.statIds[k]);
				}
			}
		}

		this.lock.lock();
		this.doneFetching = true;
		this.lock.unlock();

		this.lock.lock();
		try {
			this.doneProcessing.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void handleCoefsArrFetched(ICoefSet coefs) {
		this.lock.lock();
		try {
			Iterator<FutureTask<ICoefSet>> itr = this.fetchTaskList.iterator();
			while (itr.hasNext()) {
				FutureTask<ICoefSet> tsk = itr.next();
				if (tsk.isDone()) {
					itr.remove();
					this.notFull.signal();
				}

			}
			ListenableFutureTask<Boolean> saveTask = (ListenableFutureTask<Boolean>) this.executor
					.submit(this.factory.getCalcFValsAndSaveCallable(coefs));
			Futures.addCallback(saveTask,
					this.factory.getSavedFStatValsCallBack(this), this.executor);
			this.saveFStatTaskList.add(saveTask);

		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void handleFValsSaved(Boolean done) {
		this.lock.lock();
		try {
			Iterator<FutureTask<Boolean>> itr = this.saveFStatTaskList
					.iterator();
			while (itr.hasNext()) {
				FutureTask<Boolean> tsk = itr.next();
				if (tsk.isDone()) {
					itr.remove();
				}
			}

		} finally {
			this.lock.unlock();
		}

		try {
			// if all task are done then signal that we are ready to move on
			this.lock.lock();
			if (this.doneFetching && this.fetchTaskList.size() == 0
					&& this.saveFStatTaskList.size() == 0) {
				this.doneProcessing.signal();
			}
		} finally {
			this.lock.unlock();
		}

	}

	private void createRetrievalTask(int wavelengthId, int paramId, int statId) {
		this.lock.lock();
		try {
			while (this.fetchTaskList.size() >= MAX_FETCH
					|| this.saveFStatTaskList.size() >= MAX_FETCH) {
				notFull.await();
			}

			ListenableFutureTask<ICoefSet> retrievalTask = (ListenableFutureTask<ICoefSet>) this.executor
					.submit(this.factory.getCoefValuesSetCallable(wavelengthId,
							paramId, statId));
			Futures.addCallback(retrievalTask,
					this.factory.getCoefValuesRetreivalCallBack(this),
					this.executor);
			this.fetchTaskList.add(retrievalTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}
