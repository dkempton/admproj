package dbConnectTests;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Test;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IWindowSet;
import dbconnect.DbWindowSetResults;
import exceptions.DbConException;

public class TestDbWindowSetResults {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullDbConnection() {
		IProjectFactory factory = createMock(IProjectFactory.class);
		try {
			DbWindowSetResults rs = new DbWindowSetResults(null, factory, 1, 45);
		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullProjectFactory() {

		DataSource dsourc = createMock(DataSource.class);
		try {
			DbWindowSetResults rs = new DbWindowSetResults(dsourc, null, 1, 45);
		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHasNextOnLoad() {
		// datasource to pass into the class under test
		DataSource dsourc = createMock(DataSource.class);

		try {
			// result set to be returned by prepared statement query
			ResultSet rset = createMock(ResultSet.class);
			expect(rset.next()).andReturn((boolean) true).times(2)
					.andReturn((boolean) false).anyTimes();

			// the int value to return
			expect(rset.getInt(1)).andReturn(1);
			expect(rset.getInt(2)).andReturn(2);

			// statement to return from connection
			PreparedStatement stmt = createMock(PreparedStatement.class);
			expect(stmt.executeQuery()).andReturn((ResultSet) rset);
			stmt.setInt(anyInt(), anyInt());
			stmt.setInt(anyInt(), anyInt());

			// connection returned from datasourc
			Connection conn = createMock(Connection.class);
			expect(conn.prepareStatement((String) anyString())).andReturn(
					(PreparedStatement) stmt);
			conn.close();

			// set datasource getConnection method to return the connection
			expect(dsourc.getConnection()).andStubReturn((Connection) conn);

			// factory to pass into the class under test
			IProjectFactory factory = createMock(IProjectFactory.class);
			replay(dsourc);
			replay(conn);
			replay(stmt);
			replay(rset);

			DbWindowSetResults rs = new DbWindowSetResults(dsourc, factory, 1,
					45);
			boolean value = rs.hasNext();
			verify(dsourc);
			verify(conn);
			verify(stmt);
			verify(rset);
			assertTrue(value);

		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHasNoNextOnLoadWhenEmpty() {

		try {
			// datasource to pass into the class under test
			DataSource dsourc = createMock(DataSource.class);
			// result set to be returned by prepared statement query
			ResultSet rset = createMock(ResultSet.class);
			expect(rset.next()).andStubReturn((boolean) false);

			// statement to return from connection
			PreparedStatement stmt = createMock(PreparedStatement.class);
			expect(stmt.executeQuery()).andStubReturn((ResultSet) rset);
			stmt.setInt(anyInt(), anyInt());
			EasyMock.expectLastCall().anyTimes();

			// connection returned from datasourc
			Connection conn = createMock(Connection.class);
			expect(conn.prepareStatement((String) anyString())).andStubReturn(
					(PreparedStatement) stmt);
			conn.close();
			EasyMock.expectLastCall().anyTimes();

			// set datasource getConnection method to return the connection
			expect(dsourc.getConnection()).andStubReturn((Connection) conn);

			// factory to pass into the class under test
			IProjectFactory factory = createMock(IProjectFactory.class);
			replay(dsourc);
			replay(conn);
			replay(stmt);
			replay(rset);

			DbWindowSetResults rs = new DbWindowSetResults(dsourc, factory, 1,
					45);

			assertFalse(rs.hasNext());

		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetNext() {
		// datasource to pass into the class under test
		DataSource dsourc = createMock(DataSource.class);

		try {
			// result set to be returned by prepared statement query
			ResultSet rset = createMock(ResultSet.class);
			expect(rset.next()).andReturn((boolean) true).times(2)
					.andReturn((boolean) false).anyTimes();

			// the int value to return
			expect(rset.getInt(1)).andReturn(1);
			expect(rset.getInt(2)).andReturn(2);

			// statement to return from connection
			PreparedStatement stmt = createMock(PreparedStatement.class);
			expect(stmt.executeQuery()).andReturn((ResultSet) rset);
			stmt.setInt(anyInt(), anyInt());
			stmt.setInt(anyInt(), anyInt());

			// connection returned from datasourc
			Connection conn = createMock(Connection.class);
			expect(conn.prepareStatement((String) anyString())).andReturn(
					(PreparedStatement) stmt);
			conn.close();

			// set datasource getConnection method to return the connection
			expect(dsourc.getConnection()).andStubReturn((Connection) conn);

			// result from factory call
			@SuppressWarnings("unchecked")
			Callable<IWindowSet> wset = createMock(Callable.class);

			// factory to pass into the class under test
			IProjectFactory factory = createMock(IProjectFactory.class);
			expect(factory.getWinSetCallable(1, 2)).andReturn(
					(Callable<IWindowSet>) wset);

			replay(dsourc);
			replay(conn);
			replay(stmt);
			replay(rset);
			replay(factory);

			DbWindowSetResults rs = new DbWindowSetResults(dsourc, factory, 1,
					45);

			try {
				Callable<IWindowSet> ftsk = rs.getNextWindow();
				verify(dsourc);
				verify(conn);
				verify(stmt);
				verify(rset);
			} catch (DbConException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHasNoNext() {
		// datasource to pass into the class under test
		DataSource dsourc = createMock(DataSource.class);

		try {
			// result set to be returned by prepared statement query
			ResultSet rset = createMock(ResultSet.class);
			expect(rset.next()).andReturn((boolean) true).times(2)
					.andReturn((boolean) false).anyTimes();

			// the int value to return
			expect(rset.getInt(1)).andReturn(1);
			expect(rset.getInt(2)).andReturn(2);

			// statement to return from connection
			PreparedStatement stmt = createMock(PreparedStatement.class);
			expect(stmt.executeQuery()).andStubReturn((ResultSet) rset);
			stmt.setInt(anyInt(), anyInt());
			EasyMock.expectLastCall().anyTimes();

			// connection returned from datasourc
			Connection conn = createMock(Connection.class);
			expect(conn.prepareStatement((String) anyString())).andStubReturn(
					(PreparedStatement) stmt);
			conn.close();
			EasyMock.expectLastCall().anyTimes();

			// set datasource getConnection method to return the connection
			expect(dsourc.getConnection()).andStubReturn((Connection) conn);

			// result from factory call
			@SuppressWarnings("unchecked")
			Callable<IWindowSet> wset = createMock(Callable.class);

			// factory to pass into the class under test
			IProjectFactory factory = createMock(IProjectFactory.class);
			expect(factory.getWinSetCallable(anyInt(), anyInt())).andReturn(
					(Callable<IWindowSet>) wset);

			replay(dsourc);
			replay(conn);
			replay(stmt);
			replay(rset);
			replay(factory);

			DbWindowSetResults rs = new DbWindowSetResults(dsourc, factory, 1,
					45);

			try {
				Callable<IWindowSet> ftsk = rs.getNextWindow();
				assertTrue(rs.hasNext() == false);
			} catch (DbConException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = DbConException.class)
	public void testThrowsOnGetNextWithNoNext() throws DbConException {
		// datasource to pass into the class under test
		DataSource dsourc = createMock(DataSource.class);

		try {
			// result set to be returned by prepared statement query
			ResultSet rset = createMock(ResultSet.class);
			expect(rset.next()).andReturn((boolean) true).times(2)
					.andReturn((boolean) false).anyTimes();

			// the int value to return
			expect(rset.getInt(1)).andReturn(1);
			expect(rset.getInt(2)).andReturn(1);

			// statement to return from connection
			PreparedStatement stmt = createMock(PreparedStatement.class);
			expect(stmt.executeQuery()).andStubReturn((ResultSet) rset);
			stmt.setInt(anyInt(), anyInt());
			EasyMock.expectLastCall().anyTimes();

			// connection returned from datasourc
			Connection conn = createMock(Connection.class);
			expect(conn.prepareStatement((String) anyString())).andStubReturn(
					(PreparedStatement) stmt);
			conn.close();
			conn.close();

			// set datasource getConnection method to return the connection
			expect(dsourc.getConnection()).andStubReturn((Connection) conn);

			// result from factory call
			@SuppressWarnings("unchecked")
			Callable<IWindowSet> wset = createMock(Callable.class);

			// factory to pass into the class under test
			IProjectFactory factory = createMock(IProjectFactory.class);
			expect(factory.getWinSetCallable(anyInt(), anyInt())).andReturn(
					(Callable<IWindowSet>) wset);

			replay(dsourc);
			replay(conn);
			replay(stmt);
			replay(rset);
			replay(factory);

			DbWindowSetResults rs = new DbWindowSetResults(dsourc, factory, 1,
					45);

			Callable<IWindowSet> ftsk = rs.getNextWindow();
			rs.getNextWindow();

		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
