package transform;

<<<<<<< Updated upstream
import admproj.interfaces.ITransformWorkSupervisor;
=======
import java.util.concurrent.FutureTask;

import admproj.interfaces.IProjectFactory;
>>>>>>> Stashed changes

import com.google.common.util.concurrent.FutureCallback;

import datatypes.interfaces.IWindowSet;

public class TransformCallback implements FutureCallback<IWindowSet> {

	ITransformWorkSupervisor supervisor;

	public TransformCallback(ITransformWorkSupervisor supervisor) {
		if (supervisor == null)
			throw new IllegalArgumentException(
					"IWorkSupervisor cannot be null in TransformCallBack constructor.");
		this.supervisor = supervisor;
	}

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Failed Transform.");
	}

	@Override
	public void onSuccess(IWindowSet transformedSet) {
		this.supervisor.handleTransformDone(transformedSet);
	}

}
