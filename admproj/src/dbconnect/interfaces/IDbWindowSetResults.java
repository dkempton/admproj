package dbconnect.interfaces;

import java.util.concurrent.FutureTask;

import datatypes.interfaces.IWindowSet;
import exceptions.DbConException;

public interface IDbWindowSetResults {
	public boolean hasNext();
	public FutureTask<IWindowSet> getNextWindow() throws DbConException;
}
