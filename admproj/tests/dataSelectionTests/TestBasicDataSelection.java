package dataSelectionTests;


import org.junit.Assert;
import org.junit.Test;

import dataselection.BasicSelection;

public class TestBasicDataSelection {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsOnNull() {
		new BasicSelection(null);
	}
	
	@Test
	public void testSizeOfTrainingData() {
		double[][] dataset = { { 0, -1.0, -5.6, -4.5, -5.0 },
				{ 0, -6.0, -7.0, -8.5, -5.0 }, { 0, -9.0, -5.0, -4.5, -9.0 },
				{ 1, 1.0, 5.0, 4.5, 5.0 }, { 1, 1.0, 5.6, 4.5, 5.0 },
				{ 1, 6.0, 7.0, 8.5, 5.0 }, { 1, 9.0, 5.0, 4.5, 9.0 },
				{ 0, -1.0, -5.0, -4.5, -5.0 }, { 1, 3.0, 6.0, 4.8, 5.0 },
				{ 0, -3.0, -6.0, -4.8, -5.0 }};
		int length = new BasicSelection(dataset).getTrainingData().length;
		//Assert.assertEquals("Incorrect Size",length, (int)(dataset.length * .67));
	}
	
	@Test
	public void testSizeOfTestingData() {
		double[][] dataset = { { 0, -1.0, -5.6, -4.5, -5.0 },
				{ 0, -6.0, -7.0, -8.5, -5.0 }, { 0, -9.0, -5.0, -4.5, -9.0 },
				{ 1, 1.0, 5.0, 4.5, 5.0 }, { 1, 1.0, 5.6, 4.5, 5.0 },
				{ 1, 6.0, 7.0, 8.5, 5.0 }, { 1, 9.0, 5.0, 4.5, 9.0 },
				{ 0, -1.0, -5.0, -4.5, -5.0 }, { 1, 3.0, 6.0, 4.8, 5.0 },
				{ 0, -3.0, -6.0, -4.8, -5.0 }};
		int length = new BasicSelection(dataset).getTestingData().length;
		///Assert.assertEquals("Incorrect Size",length, (int)(dataset.length * .33));
	}
}
