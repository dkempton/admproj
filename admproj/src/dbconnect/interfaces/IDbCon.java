package dbconnect.interfaces;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import datatypes.interfaces.IWindowSet;

public interface IDbCon {
	public IDbWindowSetResults getWindows();

	
}
