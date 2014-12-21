package com.cboe.directoryService.persist.util;

import java.util.HashMap;
import java.util.Iterator;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.util.LDIF;
import netscape.ldap.util.LDIFAttributeContent;
import netscape.ldap.util.LDIFRecord;

import org.omg.CORBA.TCKind;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropertyMode;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.RegisterImpl;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.directoryService.persist.TraderOffer;
import com.cboe.directoryService.persist.TraderProp;
import com.cboe.directoryService.persist.TraderServiceType;
import com.objectwave.persist.Broker;
import com.objectwave.persist.BrokerFactory;
import com.objectwave.persist.QueryException;

/**
* Use this class to perform an initial load from a full LDIF dump of an existing
* Trader Service LDAP instance. This assumes that the Oracle databases have been
* created and are empty.
* There is no support for replacing records.
*
* Note: this class is dependent on the Netscape LDAP SDK to read LDIF files
*
* Usage: java com.cboe.directoryService.persist.util.LoadTrader full_path_to_LDIF_file

*/
public class LoadTrader
{
	public static void main(String[] args)
	{
		BrokerFactory.useDatabase();
		Broker broker = BrokerFactory.getDatabaseBroker();
		TraderDBUtil dbUtil = TraderDBUtil.getInstance();
		String val = null;
		String[] vals = null;
		boolean saveIt = true;
		int svcCount = 0;
		int offerCount = 0;
		org.omg.CORBA.ORB orb = com.cboe.ORBInfra.ORB.Orb.init();

		// resolve to get the transport loaded
		try {
			org.omg.CORBA.Object tmpObj = orb.resolve_initial_references("EventTransport");
		}
		catch(org.omg.CORBA.ORBPackage.InvalidName in) {
			System.out.println("\n*** resolve_initial_references failed for EventTransport, I quit. ***");
			System.exit(1);
		}

        String traderWithBothIORReferences = System.getProperty("TraderService.MirroredReferenceColumn", "false");
		TraderServiceType aServiceType = null;
		TraderOffer anOffer = null;
		TraderProp aProperty = null;
		HashMap propMap = new HashMap();
		try {
			LDIF parser = new LDIF( args[0] );
			LDIFRecord nextRec = parser.nextRecord();
			while ( nextRec != null ) {

				// process Offer LDIF records
				// writes to the TraderOffer database

				if ( nextRec.getDN().indexOf("offer=") >= 0 ) {
					Logger.debug("offer dn " + nextRec.getDN());

					LDIFAttributeContent rec = (LDIFAttributeContent)nextRec.getContent(); // should be this cast
					LDAPAttribute[] attrs = rec.getAttributes();
					saveIt = true;
					anOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences); 

					for (int i=0; i<attrs.length; i++) {
						Logger.debug("attr " + attrs[i].toString());
						String baseName = attrs[i].getBaseName();

						if ( baseName.equals("objectreference") ) { // db column reference
							val = getFirstValue(attrs[i]);
							anOffer.setReference(val);
							anOffer.setInformation( RegisterImpl.makeInformation(orb.string_to_object(val)) );
						}
						else if ( baseName.equals("offer") ) { // db column offerID
							val = getFirstValue(attrs[i]);
							IORImpl anImpl = new IORImpl();
							anImpl.destringify(val);
							Logger.debug("digest " + anImpl.getStringDigest());
							anOffer.setOfferID(anImpl.getStringDigest());
						}
						else if ( baseName.equals("svc") ) { // db column serviceType
							val = getFirstValue(attrs[i]);
							anOffer.setServiceType(val);
						}
						else if ( baseName.startsWith("prop") ) {
							val = getFirstValue(attrs[i]);
							propMap.put(baseName.substring(8), val);
						}
					}

					if ( saveIt ) {
						TraderProp prop = null;
						if ( !propMap.isEmpty() ) {
							int mapSize = propMap.size();
							String[] propNames = new String[mapSize];
							String[] propValues = new String[mapSize];
							int[] propTypes = new int[mapSize];
							Logger.debug("have " + mapSize + " properties");

							int idx = 0;
							Iterator iter = propMap.keySet().iterator();
							while ( iter.hasNext() ) {
								String name = (String)iter.next();
								propNames[idx] = name;
								propValues[idx] = (String)propMap.get(name);
								try {
									prop = dbUtil.getPropByName(name);
									if ( prop == null ) {
										String tmpName = name.toLowerCase();
										prop = dbUtil.getPropByName(tmpName);
										if ( prop != null ) {
											propNames[idx] = tmpName;
											propTypes[idx] = prop.getTypeCode();
										}
										else { 
											Logger.sysNotify("didn't find property " + name + " or " + tmpName +
											", setting tc to 18");
											propTypes[idx] = 18;
										}
									}
									else {
										propTypes[idx] = prop.getTypeCode();
									}
								}
								catch(QueryException qe) {
									Logger.debug("query exception, setting tc to 18 --> " + qe.getMessage());
									propTypes[idx] = 18;
								}

								Logger.debug("property " + name + " = " + propValues[idx] + ", tc " + propTypes[idx]);
								idx++;
							}

							anOffer.setPropertyNames(propNames);
							anOffer.setPropertyValues(propValues);
							anOffer.setPropertyTypes(propTypes);
						}

						try {
							anOffer.save();
							Logger.sysNotify("Saved " + nextRec.getDN());
							offerCount++;
						}
						catch(QueryException qe) {
							Logger.sysWarn("The exception is: ", qe);
						}

						propMap.clear();
					}

				}

				// process ServiceType LDIF records
				// writes to the TraderServiceType database
				// Note: this also populates the TraderProp database and the
				//       "linkage" database TraderPropJoin

				else if ( nextRec.getDN().indexOf("svc=") >= 0 ) {
					Logger.debug("service type dn " + nextRec.getDN());

					LDIFAttributeContent rec = (LDIFAttributeContent)nextRec.getContent(); // should be this cast
					LDAPAttribute[] attrs = rec.getAttributes();
					saveIt = true;
					aServiceType = new TraderServiceType();

					for (int i=0; i<attrs.length; i++) {
						Logger.debug("attr " + attrs[i].toString());
						String baseName = attrs[i].getBaseName();

						if ( baseName.equals("servicetypename") ) { // db column name
							val = getFirstValue(attrs[i]);
							aServiceType.setName(val);
						}
						else if ( baseName.equals("identifier") ) { // db column if_name
							val = getFirstValue(attrs[i]);
							aServiceType.setIfName(val);
						}

						// property type codes (e.g., tkString, tkLong, etc.)
						else if ( baseName.startsWith("tk") ) {
							vals = attrs[i].getStringValueArray();
							val = null;

							// this is a multi-valued attribute.
							// Values are actually the property names
							for (int j=0; j<vals.length; j++) {
								val = vals[j].substring(8).toLowerCase(); // strip off "property" prefix
								Pair tp = (Pair)propMap.get(val);
								if (tp == null) {
									tp = new Pair();
								}
								tp.first = baseName;
								propMap.put(val, tp);
							}
						}

						// property mode (e.g., normal, mandatory, etc.)
						else if ( baseName.startsWith("prop") ) {
							vals = attrs[i].getStringValueArray();
							val = null;

							// this is a multi-valued attribute.
							// Values are actually the property names
							for (int j=0; j<vals.length; j++) {
								val = vals[j].substring(8).toLowerCase(); // strip off "property" prefix
								Pair tp = (Pair)propMap.get(val);
								if (tp == null) {
									tp = new Pair();
								}
								tp.second = baseName;
								propMap.put(val, tp);
							}
						}
					}

					if ( saveIt ) {
						TraderProp[] props = null;
						if ( !propMap.isEmpty() ) {
							props = new TraderProp[propMap.size()];
							Logger.debug("have " + propMap.size() + " properties");
							Pair pair = null;
							String propname = null;
							int i = 0;
							Iterator iter = propMap.keySet().iterator();
							while ( iter.hasNext() ) {
								propname = ((String)iter.next()).toLowerCase();
								Logger.debug("property name = " + propname);

								pair = (Pair)propMap.get(propname);
								Logger.debug("property type = " + pair.first);
								Logger.debug("property mode = " + pair.second);

								props[i] = dbUtil.getPropByName(propname);
								if ( props[i] == null ) {
									props[i] = new TraderProp(propname);
									props[i].setTypeCode( convertType((String)pair.first) );
									props[i].setMode( convertMode((String)pair.second) );
									try {
										props[i].save();
										Logger.sysNotify("saved new TraderProp row for " + propname);
									}
									catch(QueryException pqe) {
										Logger.sysWarn("The exception is: ", pqe);
									}
								}

								i++;
							}
						}

						try {
							aServiceType.save();
							Logger.sysNotify("Saved " + nextRec.getDN());
							aServiceType.setProperties(props);
							Logger.sysNotify("Set ServiceType's properties");
							svcCount++;
						}
						catch(QueryException qe) {
							Logger.sysWarn("The exception is: ", qe);
						}

						propMap.clear();
					}
				}

				nextRec = parser.nextRecord();
			}
		}
		catch(Exception e) {
			Logger.sysWarn("The exception is: ", e);
			Logger.sysNotify("*** Quitting ***");
		}

		orb.shutdown(true);
		Logger.sysNotify("Service Types saved: " + svcCount);
		Logger.sysNotify("Offers saved: " + offerCount);
		Logger.sysNotify("\ndone!");
	}

	/**
	* Convert a String typecode name to the integer representation
	*/
	private static int convertType(String type)
	{
		int retVal = -1;
		if ( type.equalsIgnoreCase("tkstring") ) {
			retVal = TCKind._tk_string;
		}
		else if ( type.equalsIgnoreCase("tklong") ) {
			retVal = TCKind._tk_long;
		}
		else if ( type.equalsIgnoreCase("tkulong") ) {
			retVal = TCKind._tk_ulong;
		}
		else if ( type.equalsIgnoreCase("tkshort") ) {
			retVal = TCKind._tk_short;
		}
		else if ( type.equalsIgnoreCase("tkushort") ) {
			retVal = TCKind._tk_ushort;
		}
		else if ( type.equalsIgnoreCase("tkfloat") ) {
			retVal = TCKind._tk_float;
		}
		else if ( type.equalsIgnoreCase("tkdouble") ) {
			retVal = TCKind._tk_double;
		}
		else if ( type.equalsIgnoreCase("tkboolean") ) {
			retVal = TCKind._tk_boolean;
		}
		else if ( type.equalsIgnoreCase("tkchar") ) {
			retVal = TCKind._tk_char;
		}
		else throw new RuntimeException("typecode '" + type + "' not supported");

		return retVal;
	}

	/**
	* Convert a String Property Mode to the integer representation
	*/
	private static int convertMode(String mode)
	{
		int retVal = -1;
		if ( mode.equalsIgnoreCase("propnormal") ) {
			retVal = PropertyMode._PROP_NORMAL;
		}
		else if ( mode.equalsIgnoreCase("propreadonly") ) {
			retVal = PropertyMode._PROP_READONLY;
		}
		else if ( mode.equalsIgnoreCase("propmandatory") ) {
			retVal = PropertyMode._PROP_MANDATORY;
		}
		else if ( mode.equalsIgnoreCase("propmandatoryreadonly") ) {
			retVal = PropertyMode._PROP_MANDATORY_READONLY;
		}
		else throw new RuntimeException("property mode '" + mode + "' not supported");

		return retVal;
	}

	/**
	* Get the first Attribute value from an Attribute object
	*/
	private static String getFirstValue(LDAPAttribute attr)
	{
		String[] vals = attr.getStringValueArray();
		if ( vals == null | vals.length == 0 ) {
			return null;
		}
		else {
			return vals[0];
		}
	}
	
	/****************************************************************************
	 * Removed JGL's pair class, so implement a similar Pair class here
	 ****************************************************************************
	 */
	public static class Pair {
		
		public Object first;
		public Object second;
		
		public Pair(){
			first = null;
			second = null;
		}
		
		public Pair(Object firstIn, Object secondIn) {
			first = firstIn;
			second = secondIn;
		}
		
		public Object first(){
			return first;
		}
		
		public Object second(){
			return second;
		}
	}
}
