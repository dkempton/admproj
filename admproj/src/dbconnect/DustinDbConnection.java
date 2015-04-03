package dbconnect;

import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;

public class DustinDbConnection implements IDbCon {
	DataSource dsourc;
	IProjectFactory factory;

	public DustinDbConnection(DataSource dsourc, IProjectFactory factory) {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in DustinDbConneciton constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in DustinDbConnection constructor.");

		this.dsourc = dsourc;
		this.factory = factory;
	}

	@Override
	public IDbWindowSetResults getWindows() {
		return this.factory.getWindowResultSet();
	}

	@Override
	public FutureTask<Boolean> saveTransformToDb() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getWavelenghts() {
		return null;
	}

	public int[] getParams() {
		return null;
	}

}
