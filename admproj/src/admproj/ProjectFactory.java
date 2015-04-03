/**
 * 
 */
package admproj;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import datatypes.interfaces.IParamSet;
import datatypes.interfaces.IStatSet;
import datatypes.interfaces.IWavelengthSet;
import datatypes.interfaces.IWindowSet;
import dbconnect.DustinDbConnection;
import dbconnect.interfaces.IDbCon;
import dbconnect.interfaces.IDbWindowSetResults;
import exceptions.InvalidConfigException;

import org.w3c.dom.*;

import admproj.interfaces.IProjectFactory;
import snaq.db.DBPoolDataSource;

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

	// for dbpool logging
	PrintWriter wrtr;
	DBPoolDataSource dbPoolSourc = null;

	public ProjectFactory() throws InvalidConfigException {
		this.config();
	}

	@Override
	public IDbCon getDbCon() {
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

		return new DustinDbConnection(this.dbPoolSourc, this);
	}

	@Override
	public IDbWindowSetResults getWindowResultSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Callable<IWindowSet> getWinSetCallable(int windowId, int classId) {
		// TODO Auto-generated method stub
		return null;
	}

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

	@Override
	public IStatSet getStatSet(double[] stats) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IParamSet getParamSet(IStatSet[] statSets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWavelengthSet getWaveSet(IParamSet[] paramSets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowSet getWindowSet(IWavelengthSet[] waveSets) {
		// TODO Auto-generated method stub
		return null;
	}

}
