/**
 * 
 */
package admproj.interfaces;

import java.util.concurrent.Callable;

import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public interface IProjectFactory {
	public IDbCon getDbCon();
	public IDbWindowSetResults getWindowResultSet();
	public Callable<IWindowSet> getWinSetCallable(int windowId);
}
