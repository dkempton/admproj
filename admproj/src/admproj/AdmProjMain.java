package admproj;

import admproj.interfaces.IClassifierWorkSupervisor;
import admproj.interfaces.IFStatCalcWorkSupervisor;
import admproj.interfaces.IProjectFactory;
import exceptions.InvalidConfigException;

public class AdmProjMain {

	public static void main(String[] args) {

		try {
			IProjectFactory fctry = new ProjectFactory();
			// TransformWorkSupervisor supervisor = fctry.getTransformSuper();
			// supervisor.run();
			// IFStatCalcWorkSupervisor supervisor2 = fctry.getFStatCalcSuper();
			// supervisor2.run();
			IClassifierWorkSupervisor supervisor3 = fctry.getClassifierSuper();
			supervisor3.run();

		} catch (InvalidConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");

		System.exit(0);
		return;
	}

}
