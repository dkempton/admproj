package dbConnectTests;


import static org.mockito.Mockito.*;

import org.junit.Test;

import admproj.interfaces.ITransformWorkSupervisor;
import datatypes.interfaces.IWindowSet;
import dbconnect.WindowRetrievalCallBack;

public class TestWindowRetrievalCallBack {

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsOnNullWorkSuper() {
		WindowRetrievalCallBack callBack = new WindowRetrievalCallBack(null);
	}

	@Test
	public void testCallsGetMessageOnThrowable() {
		Throwable thr = mock(Throwable.class);
		ITransformWorkSupervisor spr = mock(ITransformWorkSupervisor.class);
		WindowRetrievalCallBack callBack = new WindowRetrievalCallBack(spr);
		callBack.onFailure(thr);
		verify(thr, times(1)).getMessage();
	}

	@Test
	public void testSupervisorCalled() {
		ITransformWorkSupervisor spr = mock(ITransformWorkSupervisor.class);
		WindowRetrievalCallBack callBack = new WindowRetrievalCallBack(spr);
		IWindowSet windowSet = mock(IWindowSet.class);
		callBack.onSuccess(windowSet);
		verify(spr, times(1)).handleWindowRetrieval(windowSet);
	}
}
