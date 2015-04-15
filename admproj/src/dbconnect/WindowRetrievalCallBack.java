package dbconnect;

import admproj.interfaces.ITransformWorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

import datatypes.interfaces.IWindowSet;

public class WindowRetrievalCallBack implements FutureCallback<IWindowSet> {
	ITransformWorkSupervisor supervisor;

	public WindowRetrievalCallBack(ITransformWorkSupervisor supervisor) {
		if (supervisor == null)
			throw new IllegalArgumentException(
					"IWorkSupervisor cannot be null in WindowRetrievalCallBack constructor.");
		this.supervisor = supervisor;
	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed on WinRetrieveal");
		System.out.println(arg0.getMessage());
	}

	@Override
	public void onSuccess(IWindowSet windowSet) {
		this.supervisor.handleWindowRetrieval(windowSet);
	}

}