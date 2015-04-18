package dbconnect;

import admproj.interfaces.IFStatCalcWorkSupervisor;

import com.google.common.util.concurrent.FutureCallback;

import datatypes.interfaces.ICoefSet;

public class CoefsSetFetchCallback implements FutureCallback<ICoefSet> {
	IFStatCalcWorkSupervisor supervisor;

	public CoefsSetFetchCallback(IFStatCalcWorkSupervisor supervisor) {
		if (supervisor == null)
			throw new IllegalArgumentException(
					"IStatCalcWorkSupervisor cannot be null in CeofsArrFetchCallBack constructor.");
		this.supervisor = supervisor;
	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed on CoefsArrRetrieveal");
		System.out.println(arg0.getMessage());
	}

	@Override
	public void onSuccess(ICoefSet coefs) {
		this.supervisor.handleCoefsArrFetched(coefs);
	}

}
