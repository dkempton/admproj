package dbconnect;

/**
 * @author Dustin Kempton
 * @version 1.0
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import admproj.interfaces.IProjectFactory;
import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;

public class CallableWindowFetch implements Callable<IWindowSet> {
	DataSource dsourc;
	IProjectFactory factory;
	int[] wavelenghts;
	int[] params;
	int windowId;
	int classId;

	String joinQueryString = "SELECT mean, median, std_dev, min, max "
			+ "FROM interpolated_ar_event_params JOIN events_before_flare "
			+ "ON events_before_flare.event_id = interpolated_ar_event_params.ar_event_id "
			+ "WHERE events_before_flare.Window_ID = ? AND "
			+ "interpolated_ar_event_params.wavelength_id = ? AND "
			+ "interpolated_ar_event_params.img_param_id = ? "
			+ "ORDER BY events_before_flare.start_time ASC;";

	public CallableWindowFetch(DataSource dsourc, IProjectFactory factory,
			int[] wavelenghts, int[] params, int windowId, int classId) {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableWindowFetch constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableWindowFetch constructor.");
		if (wavelenghts == null)
			throw new IllegalArgumentException(
					"int[] wavelenghts cannot be null in CallableWindowFetch constructor.");
		if (params == null)
			throw new IllegalArgumentException(
					"int[] params cannot be null in CallableWindowFetch constructor.");

		this.dsourc = dsourc;
		this.factory = factory;
		this.wavelenghts = wavelenghts;
		this.params = params;
		this.windowId = windowId;
		this.classId = classId;

	}

	@Override
	public IWindowSet call() throws Exception {
		Connection con = null;
		try {

			// get a connection from the db connection pool and
			// prepare the statement.
			con = this.dsourc.getConnection();

			PreparedStatement getParamsStmt = con
					.prepareStatement(this.joinQueryString);

			ArrayList<IWavelengthSet> waveSets = new ArrayList<IWavelengthSet>();
			for (int i = 0; i < this.wavelenghts.length; i++) {
				ArrayList<IParamSet> paramSets = new ArrayList<IParamSet>();
				for (int j = 0; j < this.params.length; j++) {
					ArrayList<IStatSet> statSets = new ArrayList<IStatSet>();

					getParamsStmt.setInt(1, this.windowId);
					getParamsStmt.setInt(2, this.wavelenghts[i]);
					getParamsStmt.setInt(3, this.params[j]);

					// execute query and get the page of results.
					ResultSet paramsResults = getParamsStmt.executeQuery();
					while (paramsResults.next()) {
						double[] paramsStats = new double[5];
						paramsStats[0] = paramsResults.getDouble(1);
						paramsStats[1] = paramsResults.getDouble(2);
						paramsStats[2] = paramsResults.getDouble(3);
						paramsStats[3] = paramsResults.getDouble(4);
						paramsStats[4] = paramsResults.getDouble(5);
						statSets.add(this.factory.getStatSet(paramsStats));
					}
					paramSets.add(this.factory
							.getParamSet((IStatSet[]) statSets.toArray()));
				}
				waveSets.add(this.factory.getWaveSet((IParamSet[]) paramSets
						.toArray()));
			}
			con.close();

			return this.factory.getWindowSet((IWavelengthSet[]) waveSets
					.toArray());
		} catch (SQLException e) {

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
				}
			}
			throw new Exception(e.getMessage());
		}
	}
}
