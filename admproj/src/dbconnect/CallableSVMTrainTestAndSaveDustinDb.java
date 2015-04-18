package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import classifier.interfaces.IClassifier;
import admproj.interfaces.IProjectFactory;

public class CallableSVMTrainTestAndSaveDustinDb implements Callable<Boolean> {

	DataSource dsourc;
	IProjectFactory factory;
	String table;

	ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds;

	int kCount;
	int kernel;

	String[] statNames = { "mean", "median", "std_dev", "min", "max" };

	public CallableSVMTrainTestAndSaveDustinDb(DataSource dsourc,
			IProjectFactory factory, String table,
			ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds, int kCount,
			int kernel) {

		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (seperatedIds == null)
			throw new IllegalArgumentException(
					"ArrayList<ArrayList<ArrayList<Integer>>> cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");

		this.dsourc = dsourc;
		this.factory = factory;
		this.table = table;

		this.seperatedIds = seperatedIds;
		this.kCount = kCount;
		this.kernel = kernel;
	}

	@Override
	public Boolean call() throws Exception {
		int setIdx = 0;
		double[][] trainingArry = getArrysFromMap(setIdx);

		IClassifier clsify = this.factory.getClassifier(this.kernel);

		long startTime = System.currentTimeMillis();
		clsify.train(trainingArry);
		long endTime = System.currentTimeMillis();

		setIdx = 1;
		double[][] testingData = this.getArrysFromMap(setIdx);
		double[] accuracyResults = clsify.evaluate(testingData);

		return this.save(accuracyResults, (endTime - startTime));
	}

	private boolean save(double[] accuracyVals, long time) {
		Connection con = null;

		try {
			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			String saveQuery = "INSERT INTO ";
			saveQuery += this.table.toLowerCase();
			saveQuery += "_svm_results VALUES(?,?,?,?,?)";

			PreparedStatement saveStmt = con.prepareStatement(saveQuery);
			saveStmt.setInt(1, this.kCount);
			saveStmt.setInt(2, this.kernel);
			saveStmt.setLong(3, time);
			saveStmt.setDouble(4, accuracyVals[0]);
			saveStmt.setDouble(5, accuracyVals[1]);

			saveStmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	private double[][] getArrysFromMap(int setIdx) {

		// get a map of the classes.
		ArrayList<LinkedList<double[]>> trainingClsMap = this
				.getCoefficientMap(setIdx);
		// keep track of where the classes end
		ArrayList<Integer> clsEndNum = new ArrayList<Integer>();
		// get how many we have in the set.
		int count = 0;
		for (int m = 0; m < this.seperatedIds.size(); m++) {
			ArrayList<ArrayList<Integer>> idSet = this.seperatedIds.get(m);
			count += idSet.get(setIdx).size();
			clsEndNum.add(count);
		}

		// place the values into a 2d array to pass to the SVM where the first
		// value is the class label.
		double[][] returnArry = new double[count][this.kCount + 1];

		for (int i = 0; i < trainingClsMap.size(); i++) {

			// if we are processing the class after the first one
			// we need to offset where in the return array we are
			// inserting the time series we are processing
			int startIdx = 0;
			if (i > 0) {
				startIdx = clsEndNum.get(i - 1);
			}

			// the set of values for the class we are currently processing
			LinkedList<double[]> prmList = trainingClsMap.get(i);

			// for each of the time series in this class
			for (int k = 0; k < prmList.size(); k++) {

				// get a time series to process
				double[] prmVals = prmList.get(k);

				// loop through and put its values into the return array
				for (int p = 0; p < prmVals.length; p++) {

					// if the first one column
					if (k == 0) {
						returnArry[p + startIdx][0] = i;
						returnArry[p + startIdx][1] = prmVals[p];
					} else {
						returnArry[p + startIdx][k + 1] = prmVals[p];
					}
				}
			}

		}
		return returnArry;
	}

	private ArrayList<LinkedList<double[]>> getCoefficientMap(int setIdx) {

		Connection con = null;

		// create a map to place the different classes in.
		// the set of coefficients are stored in the arrays in the linked list
		// each array represents one coefficient of all the time series in the
		// class
		ArrayList<LinkedList<double[]>> trainingClsMap = new ArrayList<LinkedList<double[]>>();
		for (int i = 0; i < this.seperatedIds.size(); i++) {
			trainingClsMap.add(new LinkedList<double[]>());
		}

		try {

			// select the top k coefficients to pull from the database based on
			// what is in the
			// table of f-statistics
			String topKQuery = "SELECT wavelength_id, param_id, coef_num, stat_id FROM "
					+ this.table.toLowerCase()
					+ "_f_vals ORDER BY f_val DESC LIMIT ?";

			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			PreparedStatement getTopKStmt = con.prepareStatement(topKQuery);
			getTopKStmt.setInt(1, this.kCount);

			ResultSet rs = getTopKStmt.executeQuery();

			// get training set
			while (rs.next()) {
				for (int i = 0; i < this.seperatedIds.size(); i++) {
					ArrayList<Integer> ids = this.seperatedIds.get(i).get(
							setIdx);
					double[] vals = this.getCoefVals(con, ids, rs.getInt(1),
							rs.getInt(2), rs.getInt(3), rs.getInt(4));

					LinkedList<double[]> lst = trainingClsMap.get(i);
					lst.add(vals);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return trainingClsMap;
	}

	private double[] getCoefVals(Connection con, ArrayList<Integer> ids,
			int wavelength_id, int param_id, int coef_num, int stat_id) {
		double[] returnVals = new double[ids.size()];
		int currentIdx = 0;
		for (; currentIdx < ids.size(); currentIdx++) {
			try {
				StringBuilder sb = new StringBuilder("SELECT ");
				sb.append(this.statNames[stat_id - 1]);
				sb.append("_coef FROM ");
				sb.append(this.table);
				sb.append("_transform_coefs WHERE window_id = ");
				sb.append(ids.get(currentIdx));
				sb.append(" AND coef_num =? AND wavelength_id=? AND param_id=?;");
				String qry = sb.toString();
				PreparedStatement getTopKStmt = con.prepareStatement(qry);
				getTopKStmt.setInt(1, coef_num);
				getTopKStmt.setInt(2, wavelength_id);
				getTopKStmt.setInt(3, param_id);

				ResultSet rs = getTopKStmt.executeQuery();

				if (rs.next()) {
					returnVals[currentIdx] = rs.getDouble(1);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnVals;
	}
}
