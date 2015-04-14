package dbconnect;

import admproj.interfaces.IWorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

public class TransformSavedCallback implements FutureCallback<Boolean> {
	IWorkSupervisor supervisor;

	public TransformSavedCallback(IWorkSupervisor supervisor) {
		if (supervisor == null)
			throw new IllegalArgumentException(
					"IWorkSupervisor cannot be null inTransformSavedCallBack constructor.");
		this.supervisor = supervisor;
	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed on transform save");
	}

	@Override
	public void onSuccess(Boolean saved) {
		this.supervisor.handleTransformSaved(saved);
	}

}
