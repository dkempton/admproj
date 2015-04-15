package admproj;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import datatypes.*;
import datatypes.interfaces.*;
import dbconnect.*;
import dbconnect.interfaces.*;
import exceptions.InvalidConfigException;

import org.w3c.dom.*;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import admproj.interfaces.IFStatCalcWorkSupervisor;
import admproj.interfaces.IProjectFactory;
import admproj.interfaces.ITransformWorkSupervisor;
import snaq.db.DBPoolDataSource;
import transform.Transform;
import transform.TransformCallback;
import utils.FTestCalc;
import utils.interfaces.IFTestCalc;
import wavelets.DCT;
import wavelets.HaarWavelet;

/**
 * @author Dustin Kempton
 * @version 1.0
 */
public class ProjectFactory implements IProjectFactory {

	String poolName;
	String poolDescript;
	int poolIdleTime;
	int minPool;
	int maxPool;
	int poolMaxSize;
	String user;
	String pass;
	String valQuery;
	String driverClass;
	String url;
	String transformName;
	// Settings for dataset retrieval
	int pageSize;
	int wavelengths[];
	int params[];
	int stats[];

	// for dbpool logging
	// PrintWriter wrtr;
	DBPoolDataSource dbPoolSourc = null;
	ListeningExecutorService executor = null;
	IDbCon dbcon = null;

	public ProjectFactory() throws InvalidConfigException {
		this.config();

		if (this.dbPoolSourc == null) {
			this.dbPoolSourc = new DBPoolDataSource();
			this.dbPoolSourc.setName(this.poolName);
			this.dbPoolSourc.setDescription(this.poolDescript);
			this.dbPoolSourc.setIdleTimeout(this.poolIdleTime);
			this.dbPoolSourc.setMinPool(this.minPool);
			this.dbPoolSourc.setMaxPool(this.maxPool);
			this.dbPoolSourc.setMaxSize(this.poolMaxSize);
			this.dbPoolSourc.setUser(this.user);
			this.dbPoolSourc.setPassword(this.pass);
			this.dbPoolSourc.setValidationQuery(this.valQuery);
			this.dbPoolSourc.setDriverClassName(this.driverClass);
			this.dbPoolSourc.setUrl(this.url);
		}

		if (this.executor == null) {
			this.executor = MoreExecutors.listeningDecorator(Executors
					.newFixedThreadPool(this.maxPool));
		}

		if (this.dbcon == null) {
			this.dbcon = new DbConnection(this);
		}
	}

	@Override
	public IDbCon getDbCon() {
		return this.dbcon;
	}

	@Override
	public IDbWindowSetResults getWindowResultSet() throws SQLException,
			InterruptedException {
		return new DbWindowSetResults(this.dbPoolSourc, this, this.pageSize,
				this.poolIdleTime);
	}

	@Override
	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId) {
		return new CallableWindowFetchDustinDb(this.dbPoolSourc, this,
				this.wavelengths, this.params, windowId, classId);
	}

	@Override
	public Callable<Boolean> getTransformSaveCallable(IWindowSet transformedSet) {
		return new CallableTransfromSaveDustinDb(this.dbPoolSourc,
				transformedSet, this.wavelengths, this.params,
				this.transformName);
	}

	@Override
	public Callable<Boolean> getCalcFValsAndSaveCallable(ICoefSet coefSet) {
		return new CallableCalcFValuesAndSaveDustinDb(this.dbPoolSourc, this,
				this.transformName.toLowerCase(), coefSet.getWavelengthId(),
				coefSet.getParamId(), coefSet.getStatId(), coefSet.getCoefs());
	}

	@Override
	public Callable<ICoefValues> getCoefValuesCallable(int windowId,
			int wavelengthId, int paramId, int statId, int classId) {
		return new CallableCoefsFetchDustinDB(this.dbPoolSourc, this,
				this.transformName.toLowerCase(), windowId, wavelengthId,
				paramId, statId, classId);
	}

	@Override
	public Callable<ICoefSet> getCoefValuesSetCallable(int wavelengthId,
			int paramId, int statId) throws InterruptedException {
		return new CallableCoefsSetFetchDustinDB(this.dbPoolSourc, this,
				this.executor, this.transformName, this.pageSize,
				this.poolIdleTime, wavelengthId, paramId, statId);
	}

	@Override
	public IStatSet getStatSet(double[] stats) {
		return new StatSet(stats);
	}

	@Override
	public IParamSet getParamSet(IStatSet[] statSets, int paramId) {
		return new ParamSet(statSets, paramId);
	}

	@Override
	public IWavelengthSet getWaveSet(IParamSet[] paramSets, int waveId) {
		return new WavelengthSet(paramSets, waveId);
	}

	@Override
	public IWindowSet getWindowSet(IWavelengthSet[] waveSets, int classId,
			int windowId) {
		return new WindowSet(waveSets, classId, windowId);
	}

	@Override
	public ICoefValues getCoefVals(int clslabel, double[] coefs) {
		return new CoefValues(clslabel, coefs);
	}

	@Override
	public ICoefSet getCoefSet(int wavelenghtId, int paramId, int statId,
			ICoefValues[] coefs) {
		return new CoefSet(wavelenghtId, paramId, statId, coefs);
	}

	@Override
	public FutureCallback<IWindowSet> getWindowRetrievalCallBack(
			ITransformWorkSupervisor supervisor) {
		return new WindowRetrievalCallBack(supervisor);
	}

	@Override
	public FutureCallback<IWindowSet> getTransformCallBack(
			ITransformWorkSupervisor supervisor) {
		return new TransformCallback(supervisor);
	}

	@Override
	public FutureCallback<Boolean> getSavedTransfromCallBack(
			ITransformWorkSupervisor supervisor) {
		return new TransformSavedCallback(supervisor);
	}

	@Override
	public FutureCallback<ICoefSet> getCoefValuesRetreivalCallBack(
			IFStatCalcWorkSupervisor supervisor) {
		return new CoefsSetFetchCallback(supervisor);
	}

	@Override
	public FutureCallback<Boolean> getSavedFStatValsCallBack(
			IFStatCalcWorkSupervisor supervisor) {
		return new FValuesSavedCallback(supervisor);
	}

	@Override
	public Callable<IWindowSet> getTransformWinSetCallable(IWindowSet inputSet) {
		if (this.transformName.compareTo("DCT") == 0) {
			return new Transform(this, inputSet, new DCT());
		} else {
			return new Transform(this, inputSet, new HaarWavelet());
		}
	}

	@Override
	public TransformWorkSupervisor getTransformSuper() {
		return new TransformWorkSupervisor(this.executor, this.dbcon, this);
	}

	@Override
	public IFStatCalcWorkSupervisor getFStatCalcSuper() {
		return new FStatCalcWorkSupervisor(this.executor, this,
				this.wavelengths, this.params, this.stats);
	}

	@Override
	public IFTestCalc getFValCalculator() {
		return new FTestCalc();
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// Start of private methods
	// ////////////////////////////////////////////////////////////////////////////////
	private void config() throws InvalidConfigException {
		try {
			DocumentBuilderFactory fctry = DocumentBuilderFactory.newInstance();
			Document doc;
			String fileLoc = System.getProperty("user.dir") + File.separator
					+ "config" + File.separator + "admproj.cfg.xml";
			DocumentBuilder bldr = fctry.newDocumentBuilder();
			doc = bldr.parse(new File(fileLoc));
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();
			NodeList ndLst = root.getChildNodes();
			this.getElms(ndLst);

			/*
			 * String logLoc = System.getProperty("user.dir") + File.separator +
			 * "admproj.log"; this.wrtr = new PrintWriter(new File(logLoc));
			 */
			// this.dbPoolSourc.setLogWriter(wrtr);
		} catch (Exception e) {
			throw new InvalidConfigException(e.getMessage());
		}

	}

	private void getElms(NodeList ndLst) {

		for (int i = 0; i < ndLst.getLength(); i++) {
			Node nde = ndLst.item(i);
			if (nde.getNodeType() == Node.ELEMENT_NODE) {
				String ndName = nde.getNodeName();
				switch (ndName) {
				case "poolname":
					this.poolName = this.getAttrib(nde, "value");
					break;
				case "description":
					this.poolDescript = this.getAttrib(nde, "value");
					break;
				case "ideltimeout":
					String idlStr = this.getAttrib(nde, "value");
					this.poolIdleTime = Integer.parseInt(idlStr);
					break;
				case "minpool":
					String minStr = this.getAttrib(nde, "value");
					this.minPool = Integer.parseInt(minStr);
					break;
				case "maxpool":
					String maxStr = this.getAttrib(nde, "value");
					this.maxPool = Integer.parseInt(maxStr);
					break;
				case "maxsize":
					String maxszStr = this.getAttrib(nde, "value");
					this.maxPool = Integer.parseInt(maxszStr);
					break;
				case "username":
					this.user = this.getAttrib(nde, "value");
					break;
				case "password":
					this.pass = this.getAttrib(nde, "value");
					break;
				case "validationquery":
					this.valQuery = this.getAttrib(nde, "value");
					break;
				case "driverclass":
					this.driverClass = this.getAttrib(nde, "value");
					break;
				case "url":
					this.url = this.getAttrib(nde, "value");
					break;
				case "pagesize":
					String pgStr = this.getAttrib(nde, "value");
					this.pageSize = Integer.parseInt(pgStr);
					break;
				case "wavelengthrange":
					String wvMinStr = this.getAttrib(nde, "min");
					String wvMaxStr = this.getAttrib(nde, "max");
					int wvMin = Integer.parseInt(wvMinStr);
					int wvMax = Integer.parseInt(wvMaxStr);
					this.wavelengths = new int[wvMax - wvMin + 1];
					for (int k = wvMin; k <= wvMax; k++) {
						this.wavelengths[k - wvMin] = k;
					}
					break;
				case "paramrange":
					String parmMinStr = this.getAttrib(nde, "min");
					String parmMaxStr = this.getAttrib(nde, "max");
					int parmMin = Integer.parseInt(parmMinStr);
					int parmMax = Integer.parseInt(parmMaxStr);
					this.params = new int[parmMax - parmMin + 1];
					for (int k = parmMin; k <= parmMax; k++) {
						this.params[k - parmMin] = k;
					}
					break;
				case "statsrange":
					String statsMinStr = this.getAttrib(nde, "min");
					String statsMaxStr = this.getAttrib(nde, "max");
					int statMin = Integer.parseInt(statsMinStr);
					int statMax = Integer.parseInt(statsMaxStr);
					this.stats = new int[statMax - statMin + 1];
					for (int k = statMin; k <= statMax; k++) {
						this.stats[k - statMin] = k;
					}
					break;
				case "transform":
					this.transformName = this.getAttrib(nde, "name");
					break;
				default:
					System.out.print("Unknown Element in admproj.cfg.xml: ");
					System.out.println(ndName);
				}
			}
		}
	}

	private String getAttrib(Node prntNde, String attName) {
		StringBuffer buf = new StringBuffer("");
		boolean isSet = false;
		if (prntNde.hasAttributes()) {
			NamedNodeMap ndeMp = prntNde.getAttributes();
			for (int i = 0; i < ndeMp.getLength(); i++) {
				Node nde = ndeMp.item(i);
				if (nde.getNodeName().compareTo(attName) == 0) {
					buf.append(nde.getNodeValue());
					isSet = true;
					break;
				}
			}
		}

		if (!isSet) {
			return "";
		} else {
			return buf.toString();
		}
	}

}
