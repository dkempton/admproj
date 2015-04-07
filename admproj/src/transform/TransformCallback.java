package transform;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningExecutorService;

import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;

public class TransformCallback implements FutureCallback<IWindowSet> {

	ListeningExecutorService executor;
	IProjectFactory factory;
	IDbCon dbcon;

	public TransformCallback(ListeningExecutorService executor, IDbCon dbcon,
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

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed Transform.");
	}

	@Override
	public void onSuccess(IWindowSet transformedSet) {
		FutureTask<Boolean> saveTask = this.dbcon
				.saveTransformToDb(transformedSet);
		this.executor.execute(saveTask);
		/*
		 * boolean saved = false; for (int i = 0; i < 3; i++) { try { saved =
		 * saveTask.get(); } catch (InterruptedException | ExecutionException e)
		 * { // Do we even want to worry about this? } if (saved) break; }
		 */
	}

}
