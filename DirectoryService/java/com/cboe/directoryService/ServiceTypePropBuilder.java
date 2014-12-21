//-----------------------------------------------------------------------
// FILE: ServiceTypePropBuilder
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
import java.util.*;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import javax.naming.NamingException;


// local packages
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;

import com.cboe.common.log.Logger;
import static com.cboe.directoryService.TraderLogBuilder.*;

/**
*  The ServiceTypePropBuilder class helps in the construction of a 
*  TypeStruct object.  The public method buildTypeStruct is the entry point
*  for this purpose.  
*  ever be implemented.
*
* @author             Judd Herman
*/
public class ServiceTypePropBuilder 
{
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = ServiceTypePropBuilder.class.getSimpleName();
	/**
	*  A Hastable built from the mandatory and optional attributes found in the schema
	*/	
    private Hashtable propTable;
	/**
	*  A Hastable holds the modes of the property
	*/	
    private Hashtable modeTable;
	/**
	*  A Hastable holds the type codes of the property
	*/	
    private Hashtable tkTable;
	/**
	*  holds the name of the interface for the servicetype
	*/	
    private String ifName; 
	/**
	*  holds the superTypes for the servicetype
	*/	
	private String[] superTypes;

	public ServiceTypePropBuilder() {
	}
	
	/** 
	    return the TypeStruct[] object 
	    returns an empty TypeStruct[] if no PropStructs exist
	    @param attrs - the NamingEnumeration result passed in from the caller
	    @param vec1 - A Vector of names of mandatory attribute from the schema for 
	       a particular servicetype
	    @param vec2 - A Vector of names of optional attributes from the schema for 
	        a particular servicetype
	    @return TypeStruct[] 
	*/
	public TypeStruct buildTypeStruct(NamingEnumeration attrs, Vector vec1, Vector vec2 ){
	   TypeStruct tStruct = new TypeStruct();
	   PropStruct[] newPropStructs = null;
	   
	   //buildTables(attrs, vec1, vec2);
	   Vector propVec = createPropStructs(attrs, vec1, vec2);
	   newPropStructs = new PropStruct[propVec.size()];
	    if (propVec.size() > 0) {
	         propVec.copyInto(newPropStructs);
	    }  

		tStruct.props = newPropStructs;
	    tStruct.super_types = new String[0];
	    tStruct.if_name = ifName;
	    tStruct.masked = false;
	    tStruct.incarnation = new IncarnationNumber(0,0);

	    return tStruct;     
	}   
	

	/** 
	    The buildTables method creates Hashtable objects to store each kind of attribute in
	    @param attrEnum - the enueration of attributes from the entry
	    @param must - A Vector of mandatory schema attributes
	    @param may - A Vector of optional schema attributes
	    @exception javax.naming.NamingException - thrown when error is encountered when retrieving
	                values from the attrEnum.
	*/
	private void  buildTables(NamingEnumeration attrEnum, Vector must, Vector may) throws NamingException {
	        
	        propTable = new Hashtable();
	        tkTable = new Hashtable();
	        modeTable = new Hashtable();
	        loadPropTableFromVector( must);
	        loadPropTableFromVector(may);
	        while (attrEnum.hasMoreElements()){
	            Attribute att = (Attribute)attrEnum.next();
	            
	            if (att.getID().equals("identifier")) {
			        ifName = (String)att.get();		    
			    } else 	           
	            if (att.getID().startsWith("prop")){
	                   NamingEnumeration myEnum = att.getAll();
	                   while(myEnum.hasMoreElements()) {
	                       String val = (String)myEnum.next();
			               modeTable.put(val.toUpperCase(), att.getID().toUpperCase());
	                   }
	            } else 
	            if (att.getID().startsWith("tk") ) {
		                NamingEnumeration myEnum = att.getAll();
	                    while(myEnum.hasMoreElements()) {
	                        String val = (String)myEnum.next();
			                tkTable.put(val.toUpperCase(), att.getID().toUpperCase());
			            }  //end while
			     } //end if             
	         }// end else
	 }
	 
	 
	 /**  
	     This method loads the propTable Hashtable with keys derived from the
	     the vec parameter.
	     @param vec - Vector of attribute name strings.
	 */
	 private void loadPropTableFromVector(Vector vec) {
	 	 String aString = null;
	 	 for (int i= 0; i < vec.size(); i++){
	 	       aString = (String)vec.elementAt(i);
	            if (aString.startsWith("property")){
	            propTable.put( aString.toUpperCase(), "");
	            } 
	     }	    
	 }
	 
	/** 
	    The createPropStructs builds PropStruct objects from the tables created in the buildTables
	    method.
	    @param attrEnum - the enumeration of attributes from the entry passed through to the 
	                    buildTables method.
	    @param must - A Vector of mandatory schema attributes passed through to the 
	                    buildTables method.
	    @param may - A Vector of optional schema attributes passed through to the 
	                    buildTables method.
	*/
	 protected Vector createPropStructs(NamingEnumeration attrEnum, Vector must, Vector may) {
		 final String METHOD_ID = "createPropStructs";
		 
		 
	   Vector propVec = null;
	   try {
	        propTable = new Hashtable();
	        tkTable = new Hashtable();
	        modeTable = new Hashtable();
	        buildTables(attrEnum, must, may);
	        propVec = new Vector(propTable.size());
	        Enumeration hashEnum = propTable.keys();
	        String name = null;
	        while (hashEnum.hasMoreElements()) {
	            name = ((String)hashEnum.nextElement()).toUpperCase();
	            String mode = ((String)modeTable.get(name)).toUpperCase();
	            
	            String typeCode = (String)tkTable.get(name);
	            if (typeCode == null) {
	              typeCode = "TKSTRING";
	            }
	            PropStruct prop = new PropStruct();
	            prop.name = name.substring(8);
	            prop.value_type = TraderUtility.getTypeCodeFromString(typeCode);
	            if (mode == null) {
	               prop.mode = (PropertyMode)TraderUtility.getPropertyModeFromString(TraderUtility.PROPNORMAL);
	            } else {
	               prop.mode = (PropertyMode)TraderUtility.getPropertyModeFromString(mode);
	            }
	            propVec.addElement(prop);
	        } // end while   
	    
	    } catch (NamingException ne) {
	    	Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Problem with naming"), ne);
		}
		catch (Exception ne) {
	    	Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unexpected exception"), ne);
		}
		return propVec;
    }
 	 
}
