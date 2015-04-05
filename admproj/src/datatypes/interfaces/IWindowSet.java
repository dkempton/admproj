package datatypes.interfaces;



public interface IWindowSet {
	public int size();
	public IWavelengthSet[] getAllWavelengthSets();
	public IWavelengthSet getWavlengthSet(int waveId);
	public int memberOfClass();
	public int getWindowId();
}
