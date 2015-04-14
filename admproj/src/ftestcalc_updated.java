
import java.util.*;

import org.apache.commons.math3.stat.inference.OneWayAnova;



public class ftestcalc_updated {

	
	public ArrayList<Double> fcalc(ArrayList<coff_values> coff)
	{
		OneWayAnova ow = new OneWayAnova();

		ArrayList<Integer> classes = new ArrayList<Integer> ();
		double [] timeseries = null;
		ArrayList <Double> f_value = new ArrayList<Double>();
		int x =0;
		int index =0;
		int j =0;
		double fval1;
		Collection<double[]> coll = null;
		
		ArrayList<Double> fvalue = new ArrayList<Double>();
		
		while(j<coff.size())
		{
			for(int i=0; i<coff.size();i++)
		{
        	 coff_values cv = new coff_values();
        	 classes.add(cv.cls_lable);
			timeseries[i] = cv.coff.get(x);
			
		}
		    coll.add(timeseries);
						
							
				fval1= ow.anovaFValue(coll);
				fvalue.add(fval1);
			
			
		x=x+1;
	}
		
		return fvalue;
	}
}
