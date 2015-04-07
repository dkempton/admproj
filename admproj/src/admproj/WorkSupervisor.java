package admproj;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;
import exceptions.DbConException;

public class WorkSupervisor {
	ListeningExecutorService executor;
	IProjectFactory factory;
	IDbCon dbcon;

	public WorkSupervisor(ListeningExecutorService executor, IDbCon dbcon,
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
	}

	public void run() {

		// get the window result set from the database
		IDbWindowSetResults windResults = dbcon.getWindows();

		// while there are results to process, do so by throwing the
		// future task on the thread pool for execution
		boolean moreToGo = windResults.hasNext();
		while (moreToGo) {
			int num = 10;
			int count = 0;

			@SuppressWarnings("unchecked")
			FutureTask<IWindowSet>[] tsks = new FutureTask[num];
			while (moreToGo && num - count > 0) {
				try {
					ListenableFutureTask<IWindowSet> retrievalTask = (ListenableFutureTask<IWindowSet>) this.executor
							.submit(windResults.getNextWindow());
					Futures.addCallback(retrievalTask,
							this.factory.getWindowRetrievalCallBack(),
							this.executor);
					// this.executor.execute(retrievalTask);
					tsks[count++] = retrievalTask;

					moreToGo = windResults.hasNext();
				} catch (DbConException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (int i = 0; i < count; i++) {
				try {
					tsks[i].get();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// await all fetch/transform/store_transformed tasks to finish.
		try {
			this.executor.awaitTermination(0, null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// to do
		// next we will need to implement
		// fetch_transformed/calculate_f_stat/store_top_k

		// then we will need to implement
		// fetch_top_k_transformed/calculate_svm_results/store_results
	}

}
