package testset;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import admproj.ExampleClass;
import admproj.IExampleInterface;

public class Test1 {
	
	private IExampleInterface classObj;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Before Class SetUp Called");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("After Class Teardwon Called");
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("Setup Called");
		classObj = new ExampleClass();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Teardown Called");
	}

	@Test
	public void test() {
		this.classObj.firstMethod("In test!");
		assertTrue(true);
	}

}
