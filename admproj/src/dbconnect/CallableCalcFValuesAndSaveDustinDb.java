package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import utils.interfaces.IFTestCalc;
import datatypes.interfaces.ICoefValues;
import admproj.interfaces.IProjectFactory;

public class CallableCalcFValuesAndSaveDustinDb implements Callable<Boolean> {

	DataSource dsourc;
	IProjectFactory factory;

	int wavelengthId;
	int paramId;
	int statId;
	ICoefValues[] vals;

	String saveQueryString;

	public CallableCalcFValuesAndSaveDustinDb(DataSource dsourc,
			IProjectFactory factory, String table, int wavelengthId,
			int paramId, int statId, ICoefValues[] vals) {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableCalcFValuesAndSaveDustinDb constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableCalcFValuesAndSaveDustinDb constructor.");
		if (vals == null)
			throw new IllegalArgumentException(
					"ICoefValues[] cannot be null in CallableCalcFValuesAndSaveDustinDb constructor.");

		this.dsourc = dsourc;
		this.factory = factory;

		this.wavelengthId = wavelengthId;
		this.paramId = paramId;
		this.statId = statId;

		this.vals = vals;

		this.saveQueryString = "INSERT INTO "
				+ table.toLowerCase()
				+ "_f_vals (`wavelength_id`, `param_id`, `stat_id`, `coef_num`, `f_val`) "
				+ "VALUES(?,?,?,?,?);";
	}

	@Override
	public Boolean call() throws Exception {
		IFTestCalc calculator = this.factory.getFValCalculator();
		double[] fVals = new double[0];
		try {
			fVals = calculator.fcalc(this.vals);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Connection con = null;
		try {

			con = this.dsourc.getConnection();
			con.setAutoCommit(false);

			PreparedStatement saveFValsStmt = con
					.prepareStatement(this.saveQueryString);

			for (int i = 0; i < fVals.length; i++) {
				saveFValsStmt.setInt(1, this.wavelengthId);
				saveFValsStmt.setInt(2, this.paramId);
				saveFValsStmt.setInt(3, this.statId);
				saveFValsStmt.setInt(4, i + 1);
				saveFValsStmt.setDouble(5, fVals[i]);
				saveFValsStmt.addBatch();
			}

			saveFValsStmt.executeBatch();
			con.commit();

		} catch (Exception e) {
			System.out.println("Wavelength: " + this.wavelengthId);
			System.out.println("Param: " + this.paramId);
			System.out.println("Stat: " + this.statId);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e1) {
				}
			}
		}

		// if all went well we can return true.
		return true;
	}
}
