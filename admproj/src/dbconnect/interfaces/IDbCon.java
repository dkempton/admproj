package dbconnect.interfaces;

import java.util.concurrent.FutureTask;

import datatypes.interfaces.IWindowSet;

public interface IDbCon {
	public IDbWindowSetResults getWindows();

	public FutureTask<Boolean> saveTransformToDb(IWindowSet transformedSet);
}
