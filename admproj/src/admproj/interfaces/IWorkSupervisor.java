package admproj.interfaces;

import datatypes.interfaces.IWindowSet;

public interface IWorkSupervisor {
	public void run();
	public void handleWindowRetrieval(IWindowSet wSet);
	public void handleTransformDone(IWindowSet transformSet);
	public void handleTransformSaved(Boolean done);
}
