package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	Map<String, double[]> rangeMap;
	Lock lock;

	public CallableSVMTrainTestAndSaveDustinDb(DataSource dsourc,
			IProjectFactory factory, String table,
			ArrayList<ArrayList<ArrayList<Integer>>> seperatedIds,
			Map<String, double[]> rangeMap, Lock lock, int kCount, int kernel) {

		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (factory == null)
			throw new IllegalArgumentException(
					"IProjectFactory cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (seperatedIds == null)
			throw new IllegalArgumentException(
					"ArrayList<ArrayList<ArrayList<Integer>>> cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (rangeMap == null)
			throw new IllegalArgumentException(
					"Map<Integer, double[]> cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");
		if (lock == null)
			throw new IllegalArgumentException(
					"Lock cannot be null in CallableSVMTrainTestAndSaveDustinDb constructor.");

		this.dsourc = dsourc;
		this.factory = factory;
		this.table = table;

		this.seperatedIds = seperatedIds;
		this.kCount = kCount;
		this.kernel = kernel;

		this.rangeMap = new HashMap<String, double[]>();
		this.lock = new ReentrantLock();
	}

	@Override
	public Boolean call() throws Exception {
		int setIdx = 0;
		double[][] trainingArry = getArrysFromMap(setIdx, true);

		IClassifier clsify = this.factory.getClassifier(this.kernel,
				this.kCount);

		long startTime = System.currentTimeMillis();
		clsify.train(trainingArry);
		long endTime = System.currentTimeMillis();

		setIdx = 1;
		double[][] testingData = this.getArrysFromMap(setIdx, false);
		double[] accuracyResults = clsify.evaluate(testingData);

		return this.save(accuracyResults, (endTime - startTime));
	}

	private ArrayList<double[]> normalizeData(ArrayList<double[]> dataArrays,
			boolean updateMap, int wavelength_id, int param_id, int coef_num,
			int stat_id) {

		String key = String.format("%03d", wavelength_id);
		key += String.format("%03d", param_id);
		key += String.format("%03d", stat_id);
		key += String.format("%04d", coef_num);
		// System.out.println("Key:" + key);
		if (updateMap) {
			this.lock.lock();
			try {

				// find the min/max for this set
				double min, max;

				min = dataArrays.get(0)[0];
				max = dataArrays.get(0)[0];

				for (int k = 0; k < dataArrays.size(); k++) {
					double[] dataArray = dataArrays.get(k);
					for (int i = 1; i < dataArray.length; i++) {
						if (dataArray[i] > max) {
							max = dataArray[i];
						}

						if (dataArray[i] < min) {
							min = dataArray[i];
						}
					}
				}

				Object minMaxObj = this.rangeMap.get(key);
				if (minMaxObj == null) {
					double[] minMax = new double[2];
					minMax[0] = min;
					minMax[1] = max;
					this.rangeMap.put(key, minMax);
				} else {

					double[] minMax = (double[]) minMaxObj;
					if (minMax[0] > min) {
						minMax[0] = min;
					}

					if (minMax[1] < max) {
						minMax[1] = max;
					}
				}
			} finally {
				this.lock.unlock();
			}
		}

		ArrayList<double[]> returnList = new ArrayList<double[]>();

		this.lock.lock();
		try {
			Object minMaxObj = this.rangeMap.get(key);
			double[] minMax = (double[]) minMaxObj;
			double range = minMax[1] - minMax[0];
			// System.out.println("Min:" + minMax[0]);
			// System.out.println("Max:" + minMax[1]);

			for (int k = 0; k < dataArrays.size(); k++) {
				double[] dataArray = dataArrays.get(k);
				double[] returnVals = new double[dataArray.length];
				for (int i = 0; i < dataArray.length; i++) {
					returnVals[i] = -1.0
							+ (((dataArray[i] - minMax[0]) * (2.0)) / range);

				}
				returnList.add(returnVals);
			}
		} finally {
			this.lock.unlock();
		}

		return returnList;
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

	private double[][] getArrysFromMap(int setIdx, boolean updateMap) {

		// get a map of the classes.
		ArrayList<LinkedList<double[]>> trainingClsMap = this
				.getCoefficientMap(setIdx, updateMap);
		// keep track of where the classes end
		ArrayList<Integer> clsEndNum = new ArrayList<Integer>();
		// get how many we have in the set.
		int count = 0;
		for (int m = 0; m < trainingClsMap.size(); m++) {
			LinkedList<double[]> valSet = trainingClsMap.get(m);
			count += valSet.get(0).length;
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

			// for each of the coefficient sets in this class
			for (int k = 0; k < prmList.size(); k++) {

				// get a coefficient set to process
				double[] prmVals = prmList.get(k);

				// loop through and put its values into the return array
				for (int p = 0; p < prmVals.length; p++) {

					// if the first column
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

	private ArrayList<LinkedList<double[]>> getCoefficientMap(int setIdx,
			boolean updateMap) {

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
				ArrayList<double[]> coefSet = new ArrayList<double[]>();
				for (int i = 0; i < this.seperatedIds.size(); i++) {

					ArrayList<Integer> ids = this.seperatedIds.get(i).get(
							setIdx);
					double[] vals = this.getCoefVals(con, ids, rs.getInt(1),
							rs.getInt(2), rs.getInt(3), rs.getInt(4));

					// add the values to the array list for normalization
					coefSet.add(vals);
				}

				// normalize the values
				ArrayList<double[]> normCoefVals = this.normalizeData(coefSet,
						updateMap, rs.getInt(1), rs.getInt(2), rs.getInt(3),
						rs.getInt(4));
				// add them to the array list for return
				for (int j = 0; j < normCoefVals.size(); j++) {
					LinkedList<double[]> lst = trainingClsMap.get(j);
					lst.add(normCoefVals.get(j));
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
		try {
			StringBuilder sb = new StringBuilder("SELECT ");
			sb.append(this.statNames[stat_id - 1]);
			sb.append("_coef FROM ");
			sb.append(this.table);
			sb.append("_transform_coefs WHERE window_id =? AND coef_num =? AND wavelength_id=? AND param_id=?;");
			String qry = sb.toString();
			PreparedStatement getTopKStmt = con.prepareStatement(qry);

			for (int currentIdx = 0; currentIdx < ids.size(); currentIdx++) {
				getTopKStmt.setInt(1, ids.get(currentIdx));
				getTopKStmt.setInt(2, coef_num);
				getTopKStmt.setInt(3, wavelength_id);
				getTopKStmt.setInt(4, param_id);

				ResultSet rs = getTopKStmt.executeQuery();

				if (rs.next()) {
					returnVals[currentIdx] = rs.getDouble(1);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return returnVals;
	}
}
