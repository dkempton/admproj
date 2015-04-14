import java.util.ArrayList;




public class coff_values implements ICoff_Values{

      int cls_lable;
	  ArrayList<Double> coff;
	 
	
	 
	 public coff_values(){
	 
	 }
	 
	 public int size() {
			return this.coff.size();
		}
	 

	
	public int getindex(int indx) {
		  return coff.indexOf(indx);
		}
	 
}
