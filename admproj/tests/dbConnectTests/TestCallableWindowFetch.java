package dbConnectTests;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import dbconnect.CallableWindowFetchDustinDb;

public class TestCallableWindowFetch {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullDataSource() {
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetchDustinDb cwf = new CallableWindowFetchDustinDb(null,
				factory, new int[1], new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullFactory() {
		DataSource dsourc = mock(DataSource.class);
		CallableWindowFetchDustinDb cwf = new CallableWindowFetchDustinDb(
				dsourc, null, new int[1], new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullWavelenghts() {
		DataSource dsourc = mock(DataSource.class);
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetchDustinDb cwf = new CallableWindowFetchDustinDb(
				dsourc, factory, null, new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullParams() {
		DataSource dsourc = mock(DataSource.class);
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetchDustinDb cwf = new CallableWindowFetchDustinDb(
				dsourc, factory, new int[1], null, 1, 1);
	}

	@Test
	public void testCallS() throws Exception {
		IProjectFactory factory = mock(IProjectFactory.class);
		DataSource dsourc = mock(DataSource.class);
		int[] params = { 1 };
		int[] wavelengths = { 2 };
		int winId = 3;
		int clsId = 4;

		CallableWindowFetchDustinDb cwf = new CallableWindowFetchDustinDb(
				dsourc, factory, wavelengths, params, winId, clsId);

		Connection con = mock(Connection.class);
		PreparedStatement psmt = mock(PreparedStatement.class);
		ResultSet paramsResults = mock(ResultSet.class);

		when(dsourc.getConnection()).thenReturn(con);
		when(con.prepareStatement(anyString())).thenReturn(psmt);
		when(psmt.executeQuery()).thenReturn(paramsResults);

		// give params to return when calling getDouble
		when(paramsResults.next()).thenReturn(true).thenReturn(false);
		when(paramsResults.getDouble(1)).thenReturn(1.0);
		when(paramsResults.getDouble(2)).thenReturn(2.0);
		when(paramsResults.getDouble(3)).thenReturn(3.0);
		when(paramsResults.getDouble(4)).thenReturn(4.0);
		when(paramsResults.getDouble(5)).thenReturn(5.0);

		cwf.call();
		//make sure auto commit is set because it is shut off normally
		verify(con, times(1)).setAutoCommit(true);
		
		//make sure the values get set in the query correctly
		verify(psmt, times(1)).setInt(1, winId);
		verify(psmt, times(1)).setInt(2, wavelengths[0]);
		verify(psmt, times(1)).setInt(3, params[0]);
		
		//just make sure the getDouble gets called 5 times because 
		//that is how many stats we have to get
		verify(paramsResults, times(5)).getDouble(anyInt());

		// verify everything gets a factory call
		verify(factory, times(1)).getStatSet(any(double[].class));
		verify(factory, times(1)).getParamSet(any(IStatSet[].class),
				eq(params[0]));
		verify(factory, times(1)).getWaveSet(any(IParamSet[].class),
				eq(wavelengths[0]));
		verify(factory, times(1)).getWindowSet(any(IWavelengthSet[].class),
				eq(clsId), eq(winId));

		// make sure the connection is closed
		verify(con, atLeastOnce()).close();

	}

}
