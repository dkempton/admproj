package dbConnectTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;

import org.junit.Test;

import admproj.interfaces.IProjectFactory;
import dbconnect.CallableWindowFetch;

public class TestCallableWindowFetch {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullDataSource() {
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetch cwf = new CallableWindowFetch(null, factory,
				new int[1], new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullFactory() {
		DataSource dsourc = mock(DataSource.class);
		CallableWindowFetch cwf = new CallableWindowFetch(dsourc, null,
				new int[1], new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullWavelenght() {
		DataSource dsourc = mock(DataSource.class);
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetch cwf = new CallableWindowFetch(dsourc, factory,
				null, new int[1], 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNullParams() {
		DataSource dsourc = mock(DataSource.class);
		IProjectFactory factory = mock(IProjectFactory.class);
		CallableWindowFetch cwf = new CallableWindowFetch(dsourc, factory,
				new int[1], null, 1, 1);
	}

}
