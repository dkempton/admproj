package dbconnect;

import java.sql.SQLException;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IWindowSet;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;

public class DbConnection implements IDbCon {
	DataSource dsourc;
	IProjectFactory factory;

	public DbConnection(IProjectFactory factory) {
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in DustinDbConnection constructor.");

		this.factory = factory;
	}

	@Override
	public IDbWindowSetResults getWindows() {
		try {
			return this.factory.getWindowResultSet();
		} catch (SQLException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public FutureTask<Boolean> saveTransformToDb(IWindowSet transformedSet) {
		return new FutureTask<Boolean>(
				this.factory.getTransformSaveCallable(transformedSet));
	}


}
