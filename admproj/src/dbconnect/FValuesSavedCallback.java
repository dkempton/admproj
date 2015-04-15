package dbconnect;

import admproj.interfaces.IFStatCalcWorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

public class FValuesSavedCallback implements FutureCallback<Boolean> {

	IFStatCalcWorkSupervisor supervisor;

	public FValuesSavedCallback(IFStatCalcWorkSupervisor supervisor) {
		if (supervisor == null)
			throw new IllegalArgumentException(
					"IWorkSupervisor cannot be null in FValuesSavedCallBack constructor.");
		this.supervisor = supervisor;
	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed on FValuesSaved");
		System.out.println(arg0.getMessage());
	}

	@Override
	public void onSuccess(Boolean done) {
		this.supervisor.handleFValsSaved(done);
	}

}
