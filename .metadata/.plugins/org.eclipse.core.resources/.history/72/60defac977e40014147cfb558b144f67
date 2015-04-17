package admproj;

import admproj.interfaces.IFStatCalcWorkSupervisor;
import admproj.interfaces.IProjectFactory;
import dbconnect.interfaces.IDbCon;
import exceptions.InvalidConfigException;

public class AdmProjMain {

	public static void main(String[] args) {
		System.out.println("Hello!!!!!!");

		try {
			IProjectFactory fctry = new ProjectFactory();
			//TransformWorkSupervisor supervisor = fctry.getSuper();
			//supervisor.run();
			IFStatCalcWorkSupervisor supervisor2 = fctry.getFStatCalcSuper();
			supervisor2.run();
			
		} catch (InvalidConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
		return;
	}

}
