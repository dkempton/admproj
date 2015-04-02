package dbconnect.interfaces;

import java.util.concurrent.FutureTask;

public interface IDbCon {
	public IDbWindowSetResults getWindows();

	public FutureTask<Boolean> saveTransformToDb();
}
