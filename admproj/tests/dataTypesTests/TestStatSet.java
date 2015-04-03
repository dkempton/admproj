package dataTypesTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import datatypes.StatSet;
import datatypes.interfaces.IStatSet;

public class TestStatSet {
	private IStatSet sSet;

	@Before
	public void setUp() throws Exception {
		double[] vals = new double[3];
		vals[0] = 1;
		vals[1] = 2;
		vals[2] = 3;
		sSet = new StatSet(vals);
	}

	@Test
	public void testSize() {
		assertTrue(this.sSet.size() == 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNull() {
		IStatSet ss = new StatSet(null);
	}

	@Test
	public void testGetStat() {
		assertTrue(this.sSet.getStat(0) == 1);
		assertTrue(this.sSet.getStat(1) == 2);
		assertTrue(this.sSet.getStat(2) == 3);
	}

	@Test
	public void testGetAll() {
		double[] vals = this.sSet.getAllStats();
		assertTrue(vals[0] == 1);
		assertTrue(vals[1] == 2);
		assertTrue(vals[2] == 3);
	}

}
