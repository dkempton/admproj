package admproj;

import dbconnect.IDbCon;

public class AdmProjMain {

	public static void main(String[] args) {
		System.out.println("Hello!!!!!!");

		// System.out.println(dbCon.selectStuff());
		try {
			IProjectFactory fctry = new ProjectFactory();
			IDbCon dbc = fctry.getDbCon();
			for (int i = 0; i < 100; i++) {
				dbc.getWindows();
			}
		} catch (InvalidConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
