//-----------------------------------------------------------------------
// FILE: CorbaPropertyBuilder
//
// PACKAGE: com.cboe.directoryService
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------
package com.cboe.directoryService;

// java packages
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.Property;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;

/**
This Class is responsible for building an Array of CORBA Property
objects from a set of attributes
*/

public class CorbaPropertyBuilder {
	/**
	 *  A Vector for temporary holding of the Propertys
	 */
	protected Vector propVec = null;
	/**
	 *   A Hashtable keyed by property name to hold Property objects 
	 */
	protected Hashtable propTable = null;
	/**
	 *  A Hashtable keyed by type code to hold refs back to Property object names 
	 */
	protected Hashtable tkTable = null;
	/**
	 *  Ref to the ORB
	 */
	protected ORB orb = null;

	private ResourceBundle rb = null;

	/**
	 *  Logging component name suffix
	 */
	private String  componentSuffix;
	
	public CorbaPropertyBuilder() {
		orb = ORB.init(new String[0], null);

		rb = TraderServer.initializeLoggingRb();
	}
	
		
	/** 
	return the Property[] object 
	returns an empty Property[] if no Propertys exist
	@return Property[] 
	@exception NamingException - Thrown 
	*/
	public Property[] buildProperties(NamingEnumeration attrs) {
		Property[] newPropertys = null;
		
		buildTables(attrs);
		buildPropertys();
		newPropertys = new Property[propVec.size()];
		if (propVec.size() > 0) {
			propVec.copyInto(newPropertys);
		} 
		
		return newPropertys;     
	}
	
	
	/** The buildProperty operation fills in the values of the Propertys in
	the propTable hashtable object.
	@param attrEnum - An Attribute object whose values are used to update
	the Propertys 
	*/
	private void buildTables(NamingEnumeration attrEnum)  {
		try {
			propTable = new Hashtable();
			tkTable = new Hashtable();
			while (attrEnum.hasMore()){
				Attribute att = (Attribute)attrEnum.next();
				if (att.getID().startsWith("property")){
					propTable.put(att.getID(), att);
				} else if (att.getID().startsWith("tk") ) {
					NamingEnumeration myEnum = att.getAll();
					while(myEnum.hasMore()) {
						String val = (String)myEnum.next();
						tkTable.put(val, att.getID());
					} //end while
				} //end else
			} //end while
		} catch (NamingException ne) {
			Logger.sysAlarm( rb,
						  InfraLoggingRb.DS_TS_PROPBUILDER_NAMING_EXCEPTION,
						  new java.lang.Object[] {"CorbaPropertyBuilder", "buildTables" },
						  ne );
		}
		catch (Throwable ne) {
			Logger.sysAlarm( rb,
						  InfraLoggingRb.DS_TS_PROPBUILDER_UNCAUGHT_EXCEPTION,
						  new java.lang.Object[] {"CorbaPropertyBuilder", "buildTables" },
						  ne );	
		}      
	}  

	/** 
	    The buildPropertys method creates CORBA Property objects 
	    from data in the propTable
	*/
	private void buildPropertys() {	       
		try {
			propVec = new Vector();
			Enumeration hashEnum = propTable.elements();
			String name = null;
			while (hashEnum.hasMoreElements()) {
				Attribute propAtt = (Attribute)hashEnum.nextElement();
				name = propAtt.getID();
				String typeCode = (String)tkTable.get(name);
				if (typeCode == null) {
					typeCode = "tkstring";
				}
				NamingEnumeration e = propAtt.getAll();
				while (e.hasMore()){	
					Property newProp = new Property();
					newProp.name = name.substring(8);
					String value = (String)e.next();
					System.out.println("propertyName = " + newProp.name + " Value " + value);
					newProp.value = (TraderUtility.createAnyFromString(typeCode, value));
					propVec.addElement(newProp);
				}
			} // end while   
	   	} catch( NamingException ne ) {
			Logger.sysAlarm( rb,
						  InfraLoggingRb.DS_TS_PROPBUILDER_NAMING_EXCEPTION,
						  new java.lang.Object[] {"CorbaProperyBuilder", "buildProperty" },
						  ne );
		}
		catch (Throwable ne) {
			Logger.sysAlarm( rb,
						  InfraLoggingRb.DS_TS_PROPBUILDER_UNCAUGHT_EXCEPTION,
						  new java.lang.Object[] {"CorbaPropertyBuilder", "buildProperty" },
						  ne );	
		}       				
	}			
} 
