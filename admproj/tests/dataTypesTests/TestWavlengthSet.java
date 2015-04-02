package dataTypesTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import datatypes.WavelengthSet;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;

public class TestWavlengthSet {

		private class FakeParamSet implements IParamSet {

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

		int val;

		public FakeParamSet(int i) {
			this.val = i;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public IStatSet[] getAllStatSets() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IStatSet getStatSet(int idx) {
			return new FakeStatSet(this.val);
		}

	}

	IWavelengthSet wSet;

	@Before
	public void setUp() throws Exception {
		IParamSet[] parms = new FakeParamSet[3];
		parms[0] = new FakeParamSet(1);
		parms[1] = new FakeParamSet(2);
		parms[2] = new FakeParamSet(3);
		this.wSet = new WavelengthSet(parms);
	}

	@Test
	public void testSize() {
		assertTrue(this.wSet.size() == 3);
	}

	@Test
	public void testGetParamSet() {
		IParamSet p1 = this.wSet.getParamSet(0);
		assertTrue(p1.getStatSet(0).getStat(0) == 1);

		IParamSet p2 = this.wSet.getParamSet(1);
		assertTrue(p2.getStatSet(0).getStat(0) == 2);

		IParamSet p3 = this.wSet.getParamSet(2);
		assertTrue(p3.getStatSet(0).getStat(0) == 3);
	}

	@Test
	public void testGetAllParamSets() {
		IParamSet[] parms = this.wSet.getAllParamSets();

		IParamSet p1 = parms[0];
		assertTrue(p1.getStatSet(0).getStat(0) == 1);

		IParamSet p2 = parms[1];
		assertTrue(p2.getStatSet(0).getStat(0) == 2);

		IParamSet p3 = parms[2];
		assertTrue(p3.getStatSet(0).getStat(0) == 3);
	}

}
