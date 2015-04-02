/**
 * 
 */
package dbconnect;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.sql.*;

import javax.sql.DataSource;

import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbWindowSetResults;
import exceptions.DbConException;
import admproj.interfaces.IProjectFactory;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
public class DbWindowSetResults implements IDbWindowSetResults {
	private DataSource dsourc;
	IProjectFactory factory;
	private int[] pageOfWindowIds;
	private int pageSize;
	private int currentPageSize;
	private AtomicInteger currentIdx;
	private int offset;
	private long locTimeout;
	private Lock loc;

	public DbWindowSetResults(DataSource dsourc, IProjectFactory factory,
			int pageSize, long locTimeout) throws SQLException,
			InterruptedException {
		this.dsourc = dsourc;
		this.factory = factory;
		this.pageSize = pageSize;
		this.locTimeout = locTimeout;
		this.loc = new ReentrantLock();
		this.currentIdx = new AtomicInteger();
		this.offset = 0;
		this.pageOfWindowIds = new int[this.pageSize];
		this.getNewPageOfIds();
	}

	@Override
	public boolean hasNext() {

		if ((this.currentIdx.intValue() < this.currentPageSize)
				&& this.currentPageSize > 0) {
			return true;
		} else {
			try {
				return this.getNewPageOfIds();
			} catch (InterruptedException e) {
				return false;
			}
		}

	}

	@Override
	public FutureTask<IWindowSet> getNextWindow() throws DbConException {
		if (this.hasNext()) {
			try {
				if (this.loc.tryLock(this.locTimeout, TimeUnit.SECONDS)) {
					// get the id of the window to return a future task to
					int winId = this.pageOfWindowIds[this.currentIdx
							.getAndIncrement()];
					this.loc.unlock();
					// now return a future task for this window id.
					return new FutureTask<IWindowSet>(
							this.factory.getWinSetCallable(winId));
				}
				return this.getNextWindow();
			} catch (InterruptedException e) {
				throw new DbConException(e.getMessage());
			}
		} else {
			throw new DbConException(
					"DbWindowSetResult::getNexWindow() called when no next Window.");
		}
	}

	private boolean getNewPageOfIds() throws InterruptedException {
		Connection con = null;
		if (this.loc.tryLock(this.locTimeout, TimeUnit.SECONDS)) {
			try {

				// get a connection from the db connection pool and
				// prepare the statement.
				con = this.dsourc.getConnection();

				PreparedStatement prep = con
						.prepareStatement("SELECT Window_ID FROM events_before_flare"
								+ " GROUP BY Window_ID ORDER BY Window_ID LIMIT ?,?;");
				
				prep.setInt(1, this.offset);
				prep.setInt(2, this.pageSize);

				// execute query and get the page of results.
				ResultSet rs = prep.executeQuery();
				int rowCount = 0;
				while (rs.next()) {

					// just make sure we don't go out of bounds because
					// more were returned than we expect.
					if (rowCount < this.pageOfWindowIds.length) {
						this.pageOfWindowIds[rowCount] = rs.getInt(1);
						rowCount++;
					}
				}

				// update the index of the current windowId to the first one
				// since we are getting a new page.
				this.currentIdx.set(0);
				this.currentPageSize = rowCount;

				this.loc.unlock();
				
				con.close();
				if (rowCount > 0) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				this.loc.unlock();

				if (con != null) {
					try {
						con.close();
					} catch (SQLException e1) {
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}
}
