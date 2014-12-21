package com.cboe.directoryService.persist.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPEntry;
import netscape.ldap.util.LDIFWriter;

import com.cboe.common.log.Logger;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.directoryService.persist.TraderOffer;
import com.objectwave.persist.Broker;
import com.objectwave.persist.BrokerFactory;
import com.objectwave.persist.QueryException;
import com.objectwave.persist.SQLQuery;

/**
* Use this class to dump all offers or only AdminService offers from the
* Oracle database and format them for import into LDAP (LDIF format is printed)
*
* offers are written to stderr
*
* If Argument 1 is non-blank, then all offers will be dumped, otherwise
* only AdminService offers will be dumped.
*
* Note: this class is dependent on the Netscape LDAP SDK to write LDIF files
*
* Usage: java $JAVA_GRINDER_OPTS com.cboe.directoryService.persist.util.DumpAdmin [all]
*
*/
public class DumpAdmin
{
	/* Output LDIF record format
	*	dn: offer=IOR:000..., svc=IDL:adminService/Admin:1.2,ts=CBOETrader, o=cboe.com
	*	svc: IDL:adminService/Admin:1.2
	*	ts: CBOETrader
	*	objectreference: IOR:000...
	*	offer: IOR:000...
	*	objectclass: IDLadminServiceAdmin12
	*	objectclass: servicetype
	* 	objectclass: traderhost
	*	objectclass: top
	*	propertyroutename: PCSAdminService
	*	propertyprocessname: (as in record)
	*/

	public static void main(String[] args)
	{
		boolean onlyAdmin = true;
		if ( args != null && args.length > 0 ) {
		    Logger.sysNotify("*** Dumping All Offers ***");
			onlyAdmin = false;
		}

		int recordCount=0;
		BrokerFactory.useDatabase();
		Broker broker = BrokerFactory.getDatabaseBroker();
		TraderDBUtil dbUtil = TraderDBUtil.getInstance();
		org.omg.CORBA.ORB orb = com.cboe.ORBInfra.ORB.Orb.init();
		LDIFWriter aWriter = new LDIFWriter( new PrintWriter(System.err, true) );

        String traderWithBothIORReferences = System.getProperty("TraderService.MirroredReferenceColumn", "false");

        TraderOffer anOffer = null;
		Vector theResults = null;
		try {
			anOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences); 
			SQLQuery query = new SQLQuery(anOffer);
			if ( onlyAdmin ) {
				anOffer.setServiceType("%adminService/Admin:1.2%");
				query.setAsLike(true);
			}
			theResults = query.find();
		}
		catch(QueryException qe) {
		    Logger.sysNotify("Query failed, I quit.");
			qe.printStackTrace();
			System.exit(1);
		}

		LDAPAttribute attrObjectClassSvc = new LDAPAttribute("objectclass", "servicetype");
		LDAPAttribute attrObjectClassTradHost = new LDAPAttribute("objectclass", "traderhost");
		LDAPAttribute attrObjectClassTop = new LDAPAttribute("objectclass", "top");
		Iterator iter = theResults.iterator();
		while ( iter.hasNext() ) {
			ArrayList aList = new ArrayList(20);
			TraderOffer nextOffer = (TraderOffer)iter.next();

			// create objectclass attributes & values
			String tmpSvc = nextOffer.getServiceType();
			LDAPAttribute anAttr = null;
			if ( onlyAdmin) {
				anAttr = new LDAPAttribute("objectclass", "IDLadminServiceAdmin12");
			}
			else {
				tmpSvc.replaceAll("[:./]", "");
				anAttr = new LDAPAttribute("objectclass", tmpSvc);
			}
			aList.add(anAttr);

			aList.add(attrObjectClassSvc);
			aList.add(attrObjectClassTradHost);
			aList.add(attrObjectClassTop);

			// create servicetype attribute & value
			anAttr = new LDAPAttribute("svc", tmpSvc);
			aList.add(anAttr);

			// create offer attribute & value
			String ior = nextOffer.getReference();
			anAttr = new LDAPAttribute("offer", ior);
			aList.add(anAttr);

			// create objectreference attribute & value
			anAttr = new LDAPAttribute("objectreference", ior);
			aList.add(anAttr);

			// create ts attribute & value
			anAttr = new LDAPAttribute("ts", "CBOETrader");
			aList.add(anAttr);

			String[] propNames = nextOffer.getPropertyNames();
			String[] propValues = nextOffer.getPropertyValues();
			if ( onlyAdmin ) {
				// create propertyroutename attribute & value
				anAttr = new LDAPAttribute("propertyroutename", "PCSAdminService");
				aList.add(anAttr);

				// create propertyprocessname attribute & value
				String theName = "NOT_PRESENT";
				for (int i=0; i<propNames.length; i++) {
					if ( propNames[i].equalsIgnoreCase("processname") ) {
						theName = propValues[i];
						break;
					}
				}

				anAttr = new LDAPAttribute("propertyprocessname", theName);
				aList.add(anAttr);
			}
			else { // create property attributes & values
				if ( propNames != null ) {
					for (int i=0; i<propNames.length; i++) {
						anAttr = new LDAPAttribute("property" + propNames[i], propValues[i]);
						aList.add(anAttr);
					}
				}
			}

			// write the LDIF record
			LDAPAttribute[] attrs = new LDAPAttribute[ aList.size() ];
			aList.toArray( attrs );

			LDAPAttributeSet attrSet = new LDAPAttributeSet(attrs);
			Logger.debug("writing: " + attrSet);
			String dnString = "offer=" + ior + ",svc=" + tmpSvc + ",ts=CBOETrader, o=cboe.com";
			LDAPEntry anEntry = new LDAPEntry(dnString, attrSet);
			try {
				aWriter.printEntry(anEntry);
				Logger.debug("done with entry\n");
				recordCount++;
			}
			catch(java.io.IOException ioe) {
			    Logger.debug("I/O error dn: " + dnString);
			}
		}

		orb.shutdown(true);
		Logger.sysNotify("Done!");
		Logger.sysNotify("wrote " + recordCount + " ldif records");
	}
}
