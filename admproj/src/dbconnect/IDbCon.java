package dbconnect;

import java.util.concurrent.FutureTask;

import datatypes.IWindowSet;

public interface IDbCon {
	public FutureTask<IWindowSet>[] getWindows();
}
