package dbconnect.interfaces;

import java.util.concurrent.Callable;


import datatypes.interfaces.IWindowSet;
import exceptions.DbConException;

public interface IDbWindowSetResults {
	public boolean hasNext();

	public Callable<IWindowSet> getNextWindow()
			throws DbConException;
}
