package dbconnect;

import snaq.db.DBPoolDataSource;

import java.io.*;
import java.sql.*;

public class DustinDbConnection implements IDbCon {
	DBPoolDataSource dsourc;
	PrintWriter wrtr;
	File fl;
	public DustinDbConnection(){
		dsourc = new DBPoolDataSource();
		dsourc.setName("pool-ds");
		dsourc.setDescription("Pooling DataSource");
		dsourc.setIdleTimeout(650);
		dsourc.setMinPool(2);
		dsourc.setMaxPool(10);
		dsourc.setMaxSize(40);
		dsourc.setUser("admuser");
		dsourc.setPassword("admuser");
		dsourc.setValidationQuery("Select count(*) from wavelengths;");
		dsourc.setDriverClassName("com.mysql.jdbc.Driver");
		dsourc.setUrl("jdbc:mysql://dkempton1.ddns.net:1034/dmdata");
	}

	@Override
	public boolean selectStuff() {
		try {
			Connection con = dsourc.getConnection();
			PreparedStatement prep = con.prepareStatement("Select count(*) from wavelengths");
			ResultSet rs = prep.executeQuery();
			while(rs.next()){
				System.out.println("Num of Wavlength in table: "+rs.getInt(1));
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
