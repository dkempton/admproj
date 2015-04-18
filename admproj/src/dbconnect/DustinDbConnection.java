package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;

public class DustinDbConnection implements IDbCon {
	DataSource dsourc;
	IProjectFactory factory;

	String transIdQuery;

	public DustinDbConnection(DataSource dsourc, IProjectFactory factory,
			String transform) {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in DustinDbConnection constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in DustinDbConnection constructor.");

		this.factory = factory;
		this.dsourc = dsourc;

		this.transIdQuery = "SELECT * FROM " + transform + "_window_ids;";
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
	public int[][] getTransformWinIdsWithClassLabels() {
		Connection con = null;
		int[][] result = new int[0][0];
		try {

			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			PreparedStatement selectIdsStmt = con
					.prepareStatement(this.transIdQuery);

			ResultSet rs = selectIdsStmt.executeQuery();
			ArrayList<int[]> vals = new ArrayList<int[]>();
			while (rs.next()) {
				int[] pair = new int[2];
				pair[0] = rs.getInt(1);
				pair[1] = rs.getInt(2);
				vals.add(pair);
			}

			result = new int[vals.size()][2];
			vals.toArray(result);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return result;
	}

}
