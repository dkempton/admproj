package datatypes;


public interface IWindowSet {
	public int size();
	public IWavelengthSet[] getAllWavelengthSets();
	public IWavelengthSet getWavlengthSet(int waveId);
}
