/**
 * 
 */
package admproj;

import dbconnect.IDbCon;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public interface IProjectFactory {
	public IDbCon getDbCon();
}
