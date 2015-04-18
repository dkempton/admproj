package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;

public class CallableTransfromSaveDustinDb implements Callable<Boolean> {

	DataSource dsourc;
	IWindowSet transformedSet;
	int[] wavelengthIds;
	int[] paramIds;
	String transformTablePrefix;

	public CallableTransfromSaveDustinDb(DataSource dsourc,
			IWindowSet transformedSet, int[] wavelengthIds, int[] paramIds,
			String transformTablePrefix) {

		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableTransformSave constructor.");
		if (transformedSet == null)
			throw new IllegalArgumentException(
					"IWindowSet cannot be null in CallableTransformSave constructor.");
		if (wavelengthIds == null)
			throw new IllegalArgumentException(
					"int[] wavelengthIds cannot be null in CallableTransformSave constructor.");
		if (paramIds == null)
			throw new IllegalArgumentException(
					"int[] paramIds cannot be null in CallableTransformSave constructor.");

		this.dsourc = dsourc;
		this.transformedSet = transformedSet;
		this.transformTablePrefix = transformTablePrefix;
		this.wavelengthIds = wavelengthIds;
		this.paramIds = paramIds;
	}

	@Override
	public Boolean call() throws Exception {
		Connection con = null;
		try {
			// get a connection from the db connection pool and
			// prepare the statement.
			con = this.dsourc.getConnection();
			con.setAutoCommit(false);
			String insString = "INSERT INTO "
					+ this.transformTablePrefix.toLowerCase()
					+ "_transform_coefs (`window_id`,`coef_num`, `wavelength_id`, `param_id`, `class_id`, `mean_coef`, `median_coef`, `std_dev_coef`, `min_coef`, `max_coef`) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement saveParamsStmt = con.prepareStatement(insString);
			// prepare a batch insert
			for (int i = 0; i < this.transformedSet.size(); i++) {
				IWavelengthSet wSet = this.transformedSet.getWavelengthSet(i);
				int waveId = this.wavelengthIds[i];
				for (int j = 0; j < wSet.size(); j++) {
					IParamSet pSet = wSet.getParamSet(j);
					int paramId = this.paramIds[j];
					for (int k = 0; k < pSet.size(); k++) {
						IStatSet stSet = pSet.getStatSet(k);
						saveParamsStmt.setInt(1,
								this.transformedSet.getWindowId());
						saveParamsStmt.setInt(2, k + 1);
						saveParamsStmt.setInt(3, waveId);
						saveParamsStmt.setInt(4, paramId);
						saveParamsStmt.setInt(5,
								this.transformedSet.memberOfClass());
						for (int m = 0; m < stSet.size(); m++) {
							double val = stSet.getStat(m);
							saveParamsStmt.setDouble(m + 6, val);
						}
						saveParamsStmt.addBatch();
					}
				}

			}

			// insert batch and commit
			saveParamsStmt.executeBatch();
			con.commit();

			// insert into window_ids table too
			String insWinString = "INSERT INTO "
					+ this.transformTablePrefix.toLowerCase()
					+ "_window_ids VALUES(?,?);";
			PreparedStatement saveParamsStmt2 = con
					.prepareStatement(insWinString);
			saveParamsStmt2.setInt(1, this.transformedSet.getWindowId());
			saveParamsStmt2.setInt(2, this.transformedSet.memberOfClass());
			saveParamsStmt2.executeUpdate();
			con.commit();

		} catch (Exception e) {

			System.out.println(e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
				}
			}
		}

		// if all went well we can return true.
		return true;
	}
}
