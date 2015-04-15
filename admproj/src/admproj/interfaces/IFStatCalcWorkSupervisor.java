package admproj.interfaces;

import datatypes.interfaces.ICoefSet;

public interface IFStatCalcWorkSupervisor {

	public void run();

	public void handleCoefsArrFetched(ICoefSet coefs);

	public void handleFValsSaved(Boolean done);
}
