package admproj;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import admproj.interfaces.IProjectFactory;
import admproj.interfaces.ITransformWorkSupervisor;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;
import exceptions.DbConException;

public class TransformWorkSupervisor implements ITransformWorkSupervisor {
	ListeningExecutorService executor;
	IProjectFactory factory;
	IDbCon dbcon;

	LinkedList<FutureTask<IWindowSet>> windowSetList;
	LinkedList<FutureTask<IWindowSet>> transformSetList;
	LinkedList<FutureTask<Boolean>> saveTransformList;
	private static final int MAX_FETCH = 20;
	Lock lock;
	Condition notFull;
	Condition doneProcessing;
	boolean doneFetching = false;

	public TransformWorkSupervisor(ListeningExecutorService executor, IDbCon dbcon,
			IProjectFactory factory) {
		if (executor == null)
			throw new IllegalArgumentException(
					"ThreadPoolExecutor cannot be null in WorkSupervisor constructor.");
		if (dbcon == null)
			throw new IllegalArgumentException(
					"IDbCon cannot be null in WorkSupervisor constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in WorkSupervisor");

		this.executor = executor;
		this.factory = factory;
		this.dbcon = dbcon;

		this.windowSetList = new LinkedList<FutureTask<IWindowSet>>();
		this.transformSetList = new LinkedList<FutureTask<IWindowSet>>();
		this.saveTransformList = new LinkedList<FutureTask<Boolean>>();

		this.lock = new ReentrantLock();
		this.doneProcessing = this.lock.newCondition();
		this.notFull = this.lock.newCondition();
	}

	public void run() {

		// get the window result set from the database
		IDbWindowSetResults windResults = dbcon.getWindows();

		// while there are results to process, do so by throwing the
		// future task on the thread pool for execution
		while (windResults.hasNext()) {
			try {
				this.createRetrievalTask(windResults.getNextWindow());
			} catch (DbConException e) {
				e.printStackTrace();
			}
		}

		this.lock.lock();
		this.doneFetching = true;
		this.lock.unlock();

		// to do
		// next we will need to implement
		// fetch_transformed/calculate_f_stat/store_top_k

		// then we will need to implement
		// fetch_top_k_transformed/calculate_svm_results/store_results

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

	public void handleTransformDone(IWindowSet transformedSet) {
		this.lock.lock();
		try {
			Iterator<FutureTask<IWindowSet>> itr = this.transformSetList
					.iterator();
			while (itr.hasNext()) {
				FutureTask<IWindowSet> tsk = itr.next();
				if (tsk.isDone()) {
					itr.remove();
				}
			}

			ListenableFutureTask<Boolean> saveTask = (ListenableFutureTask<Boolean>) this.executor
					.submit(this.factory
							.getTransformSaveCallable(transformedSet));
			Futures.addCallback(saveTask,
					this.factory.getSavedTransfromCallBack(this), this.executor);
			this.saveTransformList.add(saveTask);
		} finally {
			this.lock.unlock();
		}
	}

	public void handleWindowRetrieval(IWindowSet wSet) {
		this.lock.lock();
		try {
			Iterator<FutureTask<IWindowSet>> itr = this.windowSetList
					.iterator();
			while (itr.hasNext()) {
				FutureTask<IWindowSet> tsk = itr.next();
				if (tsk.isDone()) {
					itr.remove();
					this.notFull.signal();
				}

			}
			ListenableFutureTask<IWindowSet> transformTask = (ListenableFutureTask<IWindowSet>) this.executor
					.submit(this.factory.getTransformWinSetCallable(wSet));
			Futures.addCallback(transformTask,
					this.factory.getTransformCallBack(this), this.executor);
			this.transformSetList.add(transformTask);

		} finally {
			this.lock.unlock();
		}
	}

	private void createRetrievalTask(Callable<IWindowSet> retrievalCallable) {
		this.lock.lock();
		try {
			while (this.windowSetList.size() == MAX_FETCH
					|| this.saveTransformList.size() >= MAX_FETCH) {
				notFull.await();
			}
			ListenableFutureTask<IWindowSet> retrievalTask = (ListenableFutureTask<IWindowSet>) this.executor
					.submit(retrievalCallable);
			Futures.addCallback(retrievalTask,
					this.factory.getWindowRetrievalCallBack(this),
					this.executor);
			this.windowSetList.add(retrievalTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void handleTransformSaved(Boolean done) {
		this.lock.lock();
		try {
			Iterator<FutureTask<Boolean>> itr = this.saveTransformList
					.iterator();
			while (itr.hasNext()) {
				FutureTask<Boolean> tsk = itr.next();
				if (tsk.isDone()) {
					itr.remove();
					this.notFull.signal();
				}
			}

		} finally {
			this.lock.unlock();
		}

		try {
			// if all task are done then signal that we are ready to move on
			this.lock.lock();
			if (this.doneFetching && this.windowSetList.size() == 0
					&& this.transformSetList.size() == 0
					&& this.saveTransformList.size() == 0) {
				this.doneProcessing.signal();
			}
		} finally {
			this.lock.unlock();
		}
	}

}
