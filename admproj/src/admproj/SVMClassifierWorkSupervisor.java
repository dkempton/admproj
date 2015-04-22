package admproj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import dbconnect.interfaces.IDbCon;
import admproj.interfaces.IClassifierWorkSupervisor;
import admproj.interfaces.IProjectFactory;

public class SVMClassifierWorkSupervisor implements IClassifierWorkSupervisor,
		FutureCallback<Boolean> {

	ListeningExecutorService executor;
	IProjectFactory factory;
	IDbCon dbCon;

	private static final int MAX_FETCH = 7;
	Lock lock;
	Condition notFull;
	Condition doneProcessing;
	boolean doneFetching = false;

	LinkedList<FutureTask<Boolean>> classifyTaskList;

	public SVMClassifierWorkSupervisor(ListeningExecutorService executor,
			IDbCon dbCon, IProjectFactory factory) {

		if (executor == null)
			throw new IllegalArgumentException(
					"ThreadPoolExecutor cannot be null in ClassifierWorkSupervisor constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in ClassifierWorkSupervisor constructor.");
		if (dbCon == null)
			throw new IllegalArgumentException(
					"IDbCon cannot be null in ClassifierWorkSupervisor constructor.");

		this.executor = executor;
		this.factory = factory;
		this.dbCon = dbCon;

		this.classifyTaskList = new LinkedList<FutureTask<Boolean>>();

		this.lock = new ReentrantLock();
		this.doneProcessing = this.lock.newCondition();
		this.notFull = this.lock.newCondition();
	}

	@Override
	public void run() {
		ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds = this.getIds();
		for (int i = 10; i < 10000; i += 5) {
			for (int j = 1; j < 4; j++) {
				this.createClassifierTask(seperatedIds, i, j);
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
	public void handleClassificationTaskFinished(Boolean done) {
		this.lock.lock();
		try {
			Iterator<FutureTask<Boolean>> itr = this.classifyTaskList
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
			if (this.doneFetching && this.classifyTaskList.size() == 0) {
				this.doneProcessing.signal();
			}
		} finally {
			this.lock.unlock();
		}

	}

	private void createClassifierTask(
			ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds, int kCount,
			int kernel) {
		this.lock.lock();
		try {
			while (this.classifyTaskList.size() >= MAX_FETCH) {
				this.notFull.await();
			}

			ListenableFutureTask<Boolean> calassifyTask = (ListenableFutureTask<Boolean>) this.executor
					.submit(this.factory.getSVMTrainTestAndSaveCallabel(
							seperatedIds, kCount, kernel));
			Futures.addCallback(calassifyTask, this, this.executor);
			this.classifyTaskList.add(calassifyTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.lock.unlock();
		}
	}

	private ArrayList<ArrayList<ArrayList<Integer>>> getIds() {
		int[][] ids = this.dbCon.getTransformWinIdsWithClassLabels();

		Map<Integer, LinkedList<Integer>> clsMap = new HashMap<Integer, LinkedList<Integer>>();

		for (int i = 0; i < ids.length; i++) {
			int[] pair = ids[i];

			Object clsListObj = clsMap.get(pair[1]);
			if (clsListObj == null) {
				LinkedList<Integer> clsArrayList = new LinkedList<Integer>();
				clsArrayList.add(pair[0]);
				clsMap.put(pair[1], clsArrayList);
			} else {
				@SuppressWarnings("unchecked")
				LinkedList<Integer> clsArrayList = (LinkedList<Integer>) clsListObj;
				clsArrayList.add(pair[0]);
			}
		}

		Collection<LinkedList<Integer>> classes = clsMap.values();
		Iterator<LinkedList<Integer>> itr = classes.iterator();

		Random rnd = new Random(10);
		ArrayList<ArrayList<ArrayList<Integer>>> returnVals = new ArrayList<ArrayList<ArrayList<Integer>>>();
		while (itr.hasNext()) {
			LinkedList<Integer> valList = itr.next();
			int numForTrain = (valList.size() / 3) * 2;
			ArrayList<Integer> trainingSet = new ArrayList<Integer>();
			for (int j = 0; j < numForTrain; j++) {
				int idx = rnd.nextInt(valList.size());
				Integer numVal = valList.get(idx);
				if (numVal != null) {
					trainingSet.add(numVal);
				} else {
					System.out.println("Null 1");
				}
				valList.remove(idx);
			}

			ArrayList<Integer> testSet = new ArrayList<Integer>();
			for (int k = 0; k < valList.size(); k++) {
				Integer intVal = valList.get(k);
				if (intVal != null) {
					testSet.add(intVal);
				} else {
					System.out.println("Null");
				}
			}

			ArrayList<ArrayList<Integer>> set = new ArrayList<ArrayList<Integer>>(
					2);
			set.add(trainingSet);
			set.add(testSet);
			returnVals.add(set);
		}
		return returnVals;
	}

	@Override
	public void onFailure(Throwable arg0) {
		System.out.println("Failed to do SVM task.");
		arg0.printStackTrace();
		this.handleClassificationTaskFinished(false);
	}

	@Override
	public void onSuccess(Boolean done) {
		this.handleClassificationTaskFinished(done);
	}
}
