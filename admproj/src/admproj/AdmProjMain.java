package admproj;

import admproj.interfaces.IProjectFactory;
import dbconnect.interfaces.IDbCon;
import exceptions.InvalidConfigException;

public class AdmProjMain {

	public static void main(String[] args) {
		System.out.println("Hello!!!!!!");

		try {
			IProjectFactory fctry = new ProjectFactory();
			WorkSupervisor supervisor = fctry.getSuper();
			supervisor.run();
		} catch (InvalidConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
