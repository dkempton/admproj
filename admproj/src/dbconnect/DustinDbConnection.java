package dbconnect;

import snaq.db.DBPoolDataSource;

import java.sql.*;
import java.util.concurrent.FutureTask;

import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;


public class DustinDbConnection implements IDbCon {
	DBPoolDataSource dsourc;

	public DustinDbConnection(DBPoolDataSource dsourc) {
		this.dsourc = dsourc;
	}


	@Override
	public IDbWindowSetResults getWindows() {
		// TODO Auto-generated method stub
		try {
			Connection con = dsourc.getConnection();
			PreparedStatement prep = con
					.prepareStatement("Select count(*) from ar_track");
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				System.out
						.println("Num of Tracks in table: " + rs.getInt(1));
			}
			
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public FutureTask<Boolean> saveTransformToDb() {
		// TODO Auto-generated method stub
		return null;
	}

}
