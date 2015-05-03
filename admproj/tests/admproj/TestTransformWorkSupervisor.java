package admproj;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import admproj.interfaces.IProjectFactory;

import com.google.common.util.concurrent.ListeningExecutorService;

import dbconnect.interfaces.IDbCon;

public class TestTransformWorkSupervisor {

	private ListeningExecutorService executor;
	private IDbCon dbCon;
	private IProjectFactory factory;

	@Before
	public void setUp() {
		executor = Mockito.mock(ListeningExecutorService.class);
		dbCon = Mockito.mock(IDbCon.class);
		factory = Mockito.mock(IProjectFactory.class);
	}

	@Test
	public void test() {
		TransformWorkSupervisor supervisor = new TransformWorkSupervisor(executor, dbCon, factory);
		Assert.assertTrue(supervisor != null);
	}
}
