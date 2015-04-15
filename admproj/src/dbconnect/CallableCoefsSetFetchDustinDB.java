package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.ICoefSet;
import datatypes.interfaces.ICoefValues;
import exceptions.DbConException;

public class CallableCoefsSetFetchDustinDB implements Callable<ICoefSet>,
		FutureCallback<ICoefValues> {
	private DataSource dsourc;
	private IProjectFactory factory;
	private ListeningExecutorService executor;
	private int[][] pageOfWindowIds;

	private int pageSize;
	private int currentPageSize;
	private AtomicInteger currentIdx;
	private int offset;
	private long locTimeout;
	private Lock lock;
	private String idQury;

	private int wavelengthId;
	private int paramId;
	private int statId;

	private static final int MAX_FETCH = 10;
	private Condition notFull;
	private Condition doneProcessing;
	private LinkedList<FutureTask<ICoefValues>> coefSetList;
	private ArrayList<ICoefValues> coefVals;
	boolean doneFetching;

	public CallableCoefsSetFetchDustinDB(DataSource dsourc,
			IProjectFactory factory, ListeningExecutorService executor,
			String table, int pageSize, long locTimeout, int wavelengthId,
			int paramId, int statId) throws InterruptedException {

		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableCoefsArrFetch constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableCoefsArrFetch constructor.");
		if (executor == null)
			throw new IllegalArgumentException(
					"ListeningExecutorService cannot be null in CallableCoefsArrFetch constructor.");

		this.idQury = "SELECT window_id, class_id FROM dmdata." + table
				+ "_transform_coefs GROUP BY window_id LIMIT ?,?;";

		this.dsourc = dsourc;
		this.factory = factory;
		this.executor = executor;

		this.wavelengthId = wavelengthId;
		this.paramId = paramId;
		this.statId = statId;

		this.lock = new ReentrantLock();
		this.notFull = this.lock.newCondition();
		this.doneProcessing = this.lock.newCondition();

		this.pageSize = pageSize;
		this.locTimeout = locTimeout;
		this.currentIdx = new AtomicInteger();
		this.offset = 0;
		this.pageOfWindowIds = new int[this.pageSize][2];
		this.getNewPageOfIds();
	}

	@Override
	public ICoefSet call() throws Exception {
		coefVals = new ArrayList<ICoefValues>();
		this.doneFetching = false;

		// continue while we have more windows to fetch
		while (this.hasNext()) {
			this.createRetrievalTask();
		}

		// indicate we are done creating fetch tasks
		this.lock.lock();
		this.doneFetching = true;
		this.lock.unlock();

		// wait for to be done processing
		this.doneProcessing.await();

		// put all of them into the array for retrieval.
		ICoefValues[] coefValsArr = new ICoefValues[this.coefVals.size()];
		this.coefVals.toArray(coefValsArr);

		return this.factory.getCoefSet(this.wavelengthId, this.paramId,
				this.statId, coefValsArr);
	}

	@Override
	public void onFailure(Throwable arg0) {
		System.out.println(arg0.getMessage());
		cleanUp();
	}

	@Override
	public void onSuccess(ICoefValues vals) {
		this.lock.lock();
		try {
			this.coefVals.add(vals);
		} finally {
			this.lock.unlock();
		}
		this.cleanUp();
	}

	private void cleanUp() {
		this.lock.lock();

		// take all of the task from the list that are done.
		try {
			Iterator<FutureTask<ICoefValues>> itr = this.coefSetList.iterator();
			while (itr.hasNext()) {
				FutureTask<ICoefValues> tsk = itr.next();
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
			if (this.doneFetching && this.coefSetList.size() == 0) {
				this.doneProcessing.signal();
			}
		} finally {
			this.lock.unlock();
		}
	}

	private void createRetrievalTask() throws DbConException {
		if (this.hasNext()) {
			this.lock.lock();
			try {
				while (this.coefSetList.size() == MAX_FETCH) {
					notFull.await();
				}

				// get the id of the window to return a future task to
				int idxVal = this.currentIdx.getAndIncrement();
				int winId = this.pageOfWindowIds[idxVal][0];
				int clsId = this.pageOfWindowIds[idxVal][1];

				// now return a future task for this window id.
				Callable<ICoefValues> clble = this.factory
						.getCoefValuesCallable(winId, this.wavelengthId,
								this.paramId, this.statId, clsId);

				// schedule the task to run and set this as its callback when
				// finished
				ListenableFutureTask<ICoefValues> retrievalTask = (ListenableFutureTask<ICoefValues>) this.executor
						.submit(clble);
				Futures.addCallback(retrievalTask, this, this.executor);
				this.coefSetList.add(retrievalTask);

			} catch (InterruptedException e) {
				throw new DbConException(e.getMessage());
			} finally {
				this.lock.unlock();
			}
		}
	}

	private boolean hasNext() {

		if ((this.currentIdx.intValue() < this.currentPageSize)
				&& this.currentPageSize > 0) {
			return true;
		} else {
			try {
				return this.getNewPageOfIds();
			} catch (InterruptedException e) {
				return false;
			}
		}
	}

	private boolean getNewPageOfIds() throws InterruptedException {
		Connection con = null;
		if (this.lock.tryLock(this.locTimeout, TimeUnit.SECONDS)) {
			try {

				// get a connection from the db connection pool and
				// prepare the statement.
				con = this.dsourc.getConnection();
				con.setAutoCommit(true);

				PreparedStatement prep = con.prepareStatement(this.idQury);

				prep.setInt(1, this.offset);
				prep.setInt(2, this.pageSize);

				// execute query and get the page of results.
				ResultSet rs = prep.executeQuery();
				int rowCount = 0;
				while (rs.next()) {

					// just make sure we don't go out of bounds because
					// more were returned than we expect.
					if (rowCount < this.pageOfWindowIds.length) {
						this.pageOfWindowIds[rowCount][0] = rs.getInt(1);
						this.pageOfWindowIds[rowCount][1] = rs.getInt(2);
						rowCount++;
					}
				}

				// update the index of the current windowId to the first one
				// since we are getting a new page.
				this.currentIdx.set(0);
				this.currentPageSize = rowCount;

				con.close();
				if (rowCount > 0) {
					this.offset += this.pageSize;
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return false;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e1) {

					}
				}
				this.lock.unlock();
			}
		} else {
			return false;
		}
	}

}
