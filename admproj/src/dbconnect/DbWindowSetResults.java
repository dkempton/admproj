/**
 * 
 */
package dbconnect;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.sql.*;

import javax.sql.DataSource;

import com.google.common.util.concurrent.ListenableFutureTask;

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
	private IProjectFactory factory;
	private int[][] pageOfWindowIds;

	private int pageSize;
	private int currentPageSize;
	private AtomicInteger currentIdx;
	private int offset;
	private long locTimeout;
	private Lock loc;

	public DbWindowSetResults(DataSource dsourc, IProjectFactory factory,
			int pageSize, long locTimeout) throws SQLException,
			InterruptedException {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in DbWindowSetResults constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in DbWindowSetResults constructor.");

		this.dsourc = dsourc;
		this.factory = factory;
		this.pageSize = pageSize;
		this.locTimeout = locTimeout;

		this.loc = new ReentrantLock();
		this.currentIdx = new AtomicInteger();
		this.offset = 0;
		this.pageOfWindowIds = new int[this.pageSize][2];
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
	public Callable<IWindowSet> getNextWindow() throws DbConException {
		if (this.hasNext()) {
			try {
				if (this.loc.tryLock(this.locTimeout, TimeUnit.SECONDS)) {
					// get the id of the window to return a future task to
					int idxVal = this.currentIdx.getAndIncrement();
					int winId = this.pageOfWindowIds[idxVal][0];
					int clsId = this.pageOfWindowIds[idxVal][1];
					this.loc.unlock();
					// now return a future task for this window id.
					return this.factory.getWinSetCallable(winId, clsId);
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
				con.setAutoCommit(true);

				PreparedStatement prep = con
						.prepareStatement("SELECT window_id, class_id  FROM combined_windows "
								+ "GROUP BY window_id ORDER BY window_id LIMIT ?,?;");

				prep.setInt(1, this.offset);
				prep.setInt(2, this.pageSize);

				// execute query and get the page of results.
				ResultSet rs = prep.executeQuery();
				int rowCount = 0;
				while (rs.next()) {

					// just make sure we don't go out of bounds because
					// more were returned than we expect.
					if (rowCount < this.pageOfWindowIds.length) {
						this.pageOfWindowIds[rowCount][0] = rs.getInt(1);
						this.pageOfWindowIds[rowCount][1] = rs.getInt(2);
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
					this.offset += this.pageSize;
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
						System.out.println(e1.getMessage());
					}
				}
				System.out.println(e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
}
