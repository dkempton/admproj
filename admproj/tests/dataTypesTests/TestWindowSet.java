package dataTypesTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import datatypes.IParamSet;
import datatypes.IStatSet;
import datatypes.IWavelengthSet;
import datatypes.IWindowSet;
import datatypes.WindowSet;

public class TestWindowSet {

	private class FakeWavelenghtSet implements IWavelengthSet {
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

		private int val;

		public FakeWavelenghtSet(int i) {
			this.val = i;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public IParamSet[] getAllParamSets() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IParamSet getParamSet(int idx) {
			return new FakeParamSet(this.val);
		}

	}

	IWindowSet wSet;

	@Before
	public void setUp() throws Exception {
		IWavelengthSet[] wavSets = new FakeWavelenghtSet[3];
		wavSets[0] = new FakeWavelenghtSet(1);
		wavSets[1] = new FakeWavelenghtSet(2);
		wavSets[2] = new FakeWavelenghtSet(3);
		this.wSet = new WindowSet(wavSets);
	}

	@Test
	public void testSize() {
		assertTrue(this.wSet.size() == 3);
	}

	@Test
	public void testGetParamSet() {
		IWavelengthSet wavSet1 = this.wSet.getWavlengthSet(0);
		IParamSet parmSet1 = wavSet1.getParamSet(0);
		IStatSet statSet1 = parmSet1.getStatSet(0);
		assertTrue(statSet1.getStat(0) == 1);

		IWavelengthSet wavSet2 = this.wSet.getWavlengthSet(1);
		IParamSet parmSet2 = wavSet2.getParamSet(0);
		IStatSet statSet2 = parmSet2.getStatSet(0);
		assertTrue(statSet2.getStat(0) == 2);

		IWavelengthSet wavSet3 = this.wSet.getWavlengthSet(2);
		IParamSet parmSet3 = wavSet3.getParamSet(0);
		IStatSet statSet3 = parmSet3.getStatSet(0);
		assertTrue(statSet3.getStat(0) == 3);
	}

	@Test
	public void testGetAllParamSet() {
		IWavelengthSet[] wavSets = this.wSet.getAllWavelengthSets();

		IWavelengthSet wavSet1 = wavSets[0];
		IParamSet parmSet1 = wavSet1.getParamSet(0);
		IStatSet statSet1 = parmSet1.getStatSet(0);
		assertTrue(statSet1.getStat(0) == 1);

		IWavelengthSet wavSet2 = wavSets[1];
		IParamSet parmSet2 = wavSet2.getParamSet(0);
		IStatSet statSet2 = parmSet2.getStatSet(0);
		assertTrue(statSet2.getStat(0) == 2);

		IWavelengthSet wavSet3 = wavSets[2];
		IParamSet parmSet3 = wavSet3.getParamSet(0);
		IStatSet statSet3 = parmSet3.getStatSet(0);
		assertTrue(statSet3.getStat(0) == 3);
	}

}
