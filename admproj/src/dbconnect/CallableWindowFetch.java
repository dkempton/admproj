package dbconnect;

import java.util.concurrent.Callable;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IWindowSet;

public class CallableWindowFetch implements Callable<IWindowSet> {

	public CallableWindowFetch(DataSource dsourc, IProjectFactory factory, int windowId){
		
	}
	
	@Override
	public IWindowSet call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
