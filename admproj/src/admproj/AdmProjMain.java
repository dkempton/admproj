package admproj;


import snaq.db.DBPoolDataSource;

public class AdmProjMain {

	public static void main(String[] args) {
		System.out.println("Hello!!!!!!");
		//ConnectionPool pool = new ConnectionPool("Pool1", 3, 5, 10, 45, "tcp:\\192.168.1.65", "user", "user");
		DBPoolDataSource dsourc = new DBPoolDataSource();
		
	}

}
