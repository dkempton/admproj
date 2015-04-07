package dbconnect;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

import datatypes.interfaces.IWindowSet;

public class WindowRetrievalCallBack implements FutureCallback<IWindowSet> {
	ListeningExecutorService executor;
	IProjectFactory factory;

	public WindowRetrievalCallBack(ListeningExecutorService executor,
			IProjectFactory factory) {
		if (executor == null)
			throw new IllegalArgumentException(
					"ThreadPoolExecutor cannot be null in WindowRetrievalCallBack constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in WindowRetrievalCallBack.");

		this.executor = executor;
		this.factory = factory;

	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed on WinRetrieveal");
	}

	@Override
	public void onSuccess(IWindowSet windowSet) {
		ListenableFutureTask<IWindowSet> transfromTask = (ListenableFutureTask<IWindowSet>) this.executor
				.submit(this.factory.getTransformWinSetCallable(windowSet));
		Futures.addCallback(transfromTask, this.factory.getTransformCallBack(),
				this.executor);
	}

}