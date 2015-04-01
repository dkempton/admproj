package dbconnect;

import snaq.db.DBPoolDataSource;

import java.sql.*;
import java.util.concurrent.FutureTask;

import datatypes.IWindowSet;

public class DustinDbConnection implements IDbCon {
	DBPoolDataSource dsourc;

	public DustinDbConnection(DBPoolDataSource dsourc) {
		this.dsourc = dsourc;
	}


	@Override
	public FutureTask<IWindowSet>[] getWindows() {
		// TODO Auto-generated method stub
		try {
			Connection con = dsourc.getConnection();
			PreparedStatement prep = con
					.prepareStatement("Select count(*) from wavelengths");
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				System.out
						.println("Num of Wavlength in table: " + rs.getInt(1));
			}
			
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
