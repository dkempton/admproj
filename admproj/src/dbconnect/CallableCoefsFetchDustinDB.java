package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.ICoefValues;

public class CallableCoefsFetchDustinDB implements Callable<ICoefValues> {

	DataSource dsourc;
	IProjectFactory factory;

	int wavelengthId;
	int paramId;
	int windowId;
	int classId;
	String[] statNames = { "mean", "median", "std_dev", "min", "max" };
	String coefQuery;
	String classQuery;

	public CallableCoefsFetchDustinDB(DataSource dsourc,
			IProjectFactory factory, String table, int windowId,
			int wavelengthId, int paramId, int statId, int classId) {

		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableCoefsFetch constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableCoefsFetch constructor.");

		this.dsourc = dsourc;
		this.factory = factory;

		coefQuery = "SELECT ";
		coefQuery += statNames[statId - 1];
		coefQuery += "_coef FROM dmdata." + table + "_transform_coefs where "
				+ "wavelength_id = ? and param_id = ? and window_id = ? "
				+ "ORDER BY coef_num;";

		classQuery = "SELECT class_id FROM dmdata." + table
				+ "_transform_coefs where window_id = ? GROUP BY window_id;";

		this.wavelengthId = wavelengthId;
		this.paramId = paramId;
		this.windowId = windowId;
		this.classId = classId;
	}

	@Override
	public ICoefValues call() throws Exception {
		Connection con = null;
		try {

			// get a connection from the db connection pool and
			// prepare the statement.
			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			PreparedStatement getCoefsStmt = con
					.prepareStatement(this.coefQuery);

			getCoefsStmt.setInt(1, this.wavelengthId);
			getCoefsStmt.setInt(2, this.paramId);
			getCoefsStmt.setInt(3, this.windowId);

			// execute query and get the page of results.
			ResultSet coefResults = getCoefsStmt.executeQuery();

			ArrayList<Double> coefs = new ArrayList<Double>();

			while (coefResults.next()) {
				coefs.add(coefResults.getDouble(1));
			}

			double[] coefsArr = new double[coefs.size()];
			for (int i = 0; i < coefsArr.length; i++) {
				coefsArr[i] = coefs.get(i);
			}
			return this.factory.getCoefVals(this.classId, coefsArr);

		} catch (SQLException e) {

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					System.out.println(e.getErrorCode());
				}
			}
			throw new Exception(e.getMessage());
		}
	}

}
