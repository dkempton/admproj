package dataTypesTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import datatypes.ParamSet;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;


public class TestParamSet {

	private class FakeStatSet implements IStatSet {
		int iVal;

		public FakeStatSet(int i) {
			this.iVal = i;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double[] getAllStats() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public double getStat(int idx) {
			return this.iVal;
		}

	}

	private IParamSet pSet;

	@Before
	public void setUp() throws Exception {
		IStatSet[] stats = new FakeStatSet[3];
		stats[0] = new FakeStatSet(1);
		stats[1] = new FakeStatSet(2);
		stats[2] = new FakeStatSet(3);
		this.pSet = new ParamSet(stats);
	}

	@Test
	public void testSize() {
		assertTrue(this.pSet.size() == 3);
	}
	
	@Test (expected = AssertionError.class)
	public void testConstructorThrowsOnNull(){
		IParamSet prm = new ParamSet(null);
	}

	@Test
	public void testGetStatSet() {
		assertTrue(this.pSet.getStatSet(0).getStat(0) == 1);
		assertTrue(this.pSet.getStatSet(1).getStat(0) == 2);
		assertTrue(this.pSet.getStatSet(2).getStat(0) == 3);
	}

	@Test
	public void testGetAllStatSets() {
		IStatSet[] stats = this.pSet.getAllStatSets();
		assertTrue(stats[0].getStat(0) == 1);
		assertTrue(stats[1].getStat(0) == 2);
		assertTrue(stats[2].getStat(0) == 3);
	}

}
