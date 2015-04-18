package dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
		Map<Integer, LinkedList<double[]>> trainingClsMap = this
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
		double[][] input = new double[count][this.kCount + 1];
		Collection<LinkedList<double[]>> clsCollect = trainingClsMap.values();
		Iterator<LinkedList<double[]>> itr = clsCollect.iterator();
		int lastClass = -1;
		while (itr.hasNext()) {
			int startIdx = 0;
			if (lastClass >= 0) {
				startIdx = clsEndNum.get(lastClass);
			}

			// the set of values for the class we are currently processing
			LinkedList<double[]> prmList = itr.next();
			for (int k = 0; k < prmList.size(); k++) {
				double[] prmVals = prmList.get(k);
				for (int p = 0; p < prmVals.length; p++) {
					if (k == 0) {
						input[p + startIdx][0] = lastClass + 1;
						input[p + startIdx][1] = prmVals[p];
					} else {
						input[p + startIdx][k + 1] = prmVals[p];
					}
				}
			}
			lastClass++;
		}
		return input;
	}

	private Map<Integer, LinkedList<double[]>> getCoefficientMap(int setIdx) {

		Connection con = null;
		Map<Integer, LinkedList<double[]>> trainingClsMap = new HashMap<Integer, LinkedList<double[]>>();
		try {
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

					Object clsListObj = trainingClsMap.get(i);
					if (clsListObj == null) {
						LinkedList<double[]> clsArrayList = new LinkedList<double[]>();
						clsArrayList.add(vals);
						trainingClsMap.put(i, clsArrayList);
					} else {
						@SuppressWarnings("unchecked")
						LinkedList<double[]> clsArrayList = (LinkedList<double[]>) clsListObj;
						clsArrayList.add(vals);
					}
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
		while (currentIdx < ids.size()) {
			try {
				StringBuilder sb = new StringBuilder("SELECT ");
				sb.append(this.statNames[stat_id - 1]);
				sb.append("_coef FROM ");
				sb.append(this.table);
				sb.append("_transform_coefs WHERE window_id IN (");
				int i = 0;
				for (; i < 100 && (i + currentIdx) < (ids.size() - 1); i++) {
					sb.append(ids.get(i + currentIdx));
					sb.append(", ");
				}
				sb.append(ids.get(i + currentIdx));
				sb.append(") AND coef_num =? AND wavelength_id=? AND param_id=?;");
				String qry = sb.toString();
				PreparedStatement getTopKStmt = con.prepareStatement(qry);
				getTopKStmt.setInt(1, coef_num);
				getTopKStmt.setInt(2, wavelength_id);
				getTopKStmt.setInt(3, param_id);

				ResultSet rs = getTopKStmt.executeQuery();

				while (rs.next()) {
					returnVals[currentIdx++] = rs.getDouble(1);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnVals;
	}
}
