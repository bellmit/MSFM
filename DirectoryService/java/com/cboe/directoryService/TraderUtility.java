//-----------------------------------------------------------------------
// FILE: TraderUtility
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
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.naming.directory.*;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.CommunicationException;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.Context;



// local packages
import org.omg.CORBA.ORB;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropertyMode;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.Any;
import org.omg.CosTrading.Property;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.IllegalConstraint;
import org.omg.CORBA.UserException;
import org.omg.CORBA.SystemException;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import com.cboe.directoryService.parser.*;
import com.cboe.loggingService.*;

/**
*  This is Utility class that provides access to common functions
*  needed by the Trader.  
*  
*  
*  It has methods that do common conversions between CORBA objects
*  and database objects.
*
*  It contains methods for common JNDI functions for conversion,
*  schema usage and searches.
*
*
*  All of the methods are static.
*
* @author             Judd Herman
*/


public class TraderUtility
{
	public static final String ATTR_SYNTAX_BINARY = "1.3.6.1.4.1.1466.115.121.1.5"; // rfc2252 Binary
	public static final String ATTR_SYNTAX_DN = "1.3.6.1.4.1.1466.115.121.1.12"; // rfc2252 DN
	public static final String ATTR_SYNTAX_CIS = "1.3.6.1.4.1.1466.115.121.1.15"; // rfc2252 Directory String
	public static final String ATTR_SYNTAX_CES = "1.3.6.1.4.1.1466.115.121.1.26"; // rfc2252 IA5 String
	public static final String ATTR_SYNTAX_INTEGER = "1.3.6.1.4.1.1466.115.121.1.27"; // rfc2252 INTEGER
	public static final String ATTR_SYNTAX_TELEPHONE = "1.3.6.1.4.1.1466.115.121.1.50"; // rfc2252 Telephone Number
    /**
    *  Holds a reference to the ORB
    */
    public static ORB orb =  com.cboe.ORBInfra.ORB.Orb.init();
    /**
    *  Holds a reference to a CorbaPropertyBuilder object
    */
    private static CorbaPropertyBuilder corbaPropertyBuilder = null;
    /**
    *  Static strings
    */
    public static String PROPNORMAL = "propnormal";
    public static String PROPMANDATORY = "propmandatory";
    public static String PROPREADONLY = "propreadonly";
    public static String PROPMANDATORYREADONLY = "propmandatoryreadonly";
    public static String RESTRICTEDMODES[] = {
        PROPMANDATORY, PROPREADONLY, PROPMANDATORYREADONLY};

	private static LdapContext mainDirContext = null;
	private static Hashtable dirContextEnvVars = null;
    
	public TraderUtility() {

	}

    /*************************************************************
    
    TypeCode(CORBA.Any) CONVERSION UTILITIES
    
    *************************************************************/
    
    
    
     /**
    returns an BasicAttribute supporting the prop that is passed in
    @param prop - property struct
    @exception - Exception means that the type code passed in in the
    property is not supported
    
    */
	public static String getKind(Any any) throws Exception{
	    return getTypeCodeFromKind(getTCKind(any));
	}

    /**
    returns a String of the corresponding typecode which is stored 
    n the LDAP repository.
    @param kind - TCKind  
    */
	public static String getTypeCodeFromKind( TCKind kind )
	                        throws InvalidAttributeValueException {
		String tc = null;
		switch ( kind.value() )
		{
		    case TCKind._tk_string:
				tc = "tkString";
	    			break;
			case TCKind._tk_short:
				tc = "tkShort";
    				break;
			case TCKind._tk_long:
				tc = "tkLong";
				break;
			case TCKind._tk_ushort:
				tc = "tkUshort";
				break;
			case TCKind._tk_ulong:
				tc = "tkUlong";
				break;
			case TCKind._tk_float:
				tc = "tkFloat";
				break;
			case TCKind._tk_double:
				tc = "tkDouble";
				break;
			case TCKind._tk_boolean:
				tc = "tkBoolean";
				break;
			case TCKind._tk_char:
				tc = "tkChar";
				break;
			default:
			    throw new InvalidAttributeValueException
			    ( "TCKind not Supported");
			/*  
			// Here are the unsupported type codes
			// Leave just in case they are needed
			// in the future
			case TCKind._tk_octet:
				tc = "tkOctet";
				break;
			case TCKind._tk_any:
				tc = "tkAny";
				break;
			case TCKind._tk_TypeCode:
				tc = "tkTypeCode";
				break;
			case TCKind._tk_Principal:
				tc = "tkPrincipal";
				break;
			case TCKind._tk_objref:
				tc = "tkObjectref";
				break;
			case TCKind._tk_struct:
				tc = "tkStruct";
				break;
			case TCKind._tk_union:
				tc = "tkUnion";
				break;
			case TCKind._tk_enum:
				tc = "tkEnum";
				break;
			case TCKind._tk_null:
				tc = "tkNull";
				break;
			case TCKind._tk_void:
				tc = "tkVoid";
				break;
			case TCKind._tk_sequence:
				tc = "tkSequence";
				break;
			case TCKind._tk_array:
				tc = "tkArray";
				break;
			case TCKind._tk_alias:
				tc = "tkAlias";
				break;
			case TCKind._tk_except:
				tc = "tkExcept";
				break;
			case TCKind._tk_longlong:
				tc = "tkLonglong";
				break;
			case TCKind._tk_ulonglong:
				tc = "tkUlonglong";
				break;
			case TCKind._tk_longdouble:
				tc = "tkLongdouble";
				break;
			case TCKind._tk_wchar:
				tc = "tkWchar";
				break;
			case TCKind._tk_wstring:
				tc = "tkString";
				break;
			case TCKind._tk_fixed:
				tc = "tkFixed";
				break;
		    */
		}
		return tc;
	}

/**
Create an Any object from a type code and the string value
@param tc - a type code string e.g. "tk_string"
@param value - the value to assign to the Any object 
@return Any object
*/
public static Any createAnyFromString( String tc, String value) 
                            throws InvalidAttributeValueException {
	     try {
	        Any any = orb.create_any();
            if (tc.equalsIgnoreCase("tkstring")) {
			    any.insert_string(value);
			} else
            if (tc.equalsIgnoreCase("tk_string")) {
			    any.insert_string(value);
			} else
    		if (tc.equalsIgnoreCase("tkShort")) {
			    any.insert_short(Short.parseShort(value));
			} else 
			if (tc.equalsIgnoreCase("tkLong")) {
			   any.insert_long(new Integer(value).intValue());
			} else 
            if (tc.equalsIgnoreCase("tkUshort")) {
			    any.insert_ushort(Short.parseShort(value));
			} else 
			if (tc.equalsIgnoreCase("tkUlong")) {
			    any.insert_ulong(new Integer(value).intValue());
			} else 
			if (tc.equalsIgnoreCase("tkFloat")) {
			    any.insert_float(new Float(value).floatValue());
			} else 
			if (tc.equalsIgnoreCase("tkDouble")) {
			    any.insert_double(new Double(value).doubleValue());
			} else 
			if (tc.equalsIgnoreCase("tkBoolean")) {
			   any.insert_boolean(Boolean.getBoolean(value));
			} else 
			if (tc.equalsIgnoreCase("tkChar")) {
			    any.insert_char(value.toCharArray()[0]);
			}
			
			return any;
            
		// default
		} catch (NumberFormatException ex) {
		        throw new InvalidAttributeValueException
		        ( "Property: " + tc +" with value:" + value +" not Supported"); 
		} 
		
    }
    
/**
Create an TypeCode object from a type code string 
@param tc - a type code string e.g. "tk_string"
@return TypeCode object
*/
        
public static TypeCode getTypeCodeFromString( String tc ) throws InvalidAttributeValueException
	{
        TypeCode typeCode = null;
            if (tc.equalsIgnoreCase("tkstring")) {
			    return getOrb().create_string_tc(0);
			} 
            if (tc.equalsIgnoreCase("tk_string")) {
			    return getOrb().create_string_tc(0);
			} else
    		if (tc.equalsIgnoreCase("tkShort")) {
			    return getOrb().get_primitive_tc(TCKind.tk_short);
			} else 
			if (tc.equalsIgnoreCase("tkLong")) {
			    return getOrb().get_primitive_tc(TCKind.tk_long);
			} else 
            if (tc.equalsIgnoreCase("tkUshort")) {
			    return getOrb().get_primitive_tc(TCKind.tk_ushort);
			} else 
			if (tc.equalsIgnoreCase("tkUlong")) {
			    return getOrb().get_primitive_tc(TCKind.tk_ulong);
			} else 
			if (tc.equalsIgnoreCase("tkFloat")) {
			    return getOrb().get_primitive_tc(TCKind.tk_float);
			} else 
			if (tc.equalsIgnoreCase("tkDouble")) {
			    return getOrb().get_primitive_tc(TCKind.tk_double);
			} else 
			if (tc.equalsIgnoreCase("tkBoolean")) {
			    return getOrb().get_primitive_tc(TCKind.tk_boolean);
			} else 
			if (tc.equalsIgnoreCase("tkChar")) {
			    return getOrb().get_primitive_tc(TCKind.tk_char);
			}
			// default	
			throw new InvalidAttributeValueException
			( "Property: " + tc + " not Supported");
			
			/*  
			// Here are the unsupported type codes
			// Leave just in case they are needed
			// in the future
			else 
			if (tc.equalsIgnoreCase("tkNull")) {
			    return getOrb.get_primitive_tc(TCKind.tk_null);
			} else 
			if (tc.equalsIgnoreCase("tkVoid")) {
			    return getOrb.get_primitive_tc(TCKind.tk_void);
			} else 
			if (tc.equalsIgnoreCase("tkSequence")) {
			    return getOrb.get_primitive_tc(TCKind.tk_sequence);
			} else 
			if (tc.equalsIgnoreCase("tkArray")) {
			    return getOrb.get_primitive_tc(TCKind.tk_array);
			} else 
			if (tc.equalsIgnoreCase("tkAlias")) {
			    return getOrb.get_primitive_tc(TCKind.tk_alias);
			} else 
			if (tc.equalsIgnoreCase("tkExcept")) {
			    return getOrb.get_primitive_tc(TCKind.tk_except);
			} else 
			if (tc.equalsIgnoreCase("tkLonglong")) {
			    return getOrb.get_primitive_tc(TCKind.tk_longlong);
			} else 
			if (tc.equalsIgnoreCase("tkUlonglong")) {
			    return getOrb.get_primitive_tc(TCKind.tk_ulonglong);
			} else 
			if (tc.equalsIgnoreCase("tkLongdouble")) {
			    return getOrb.get_primitive_tc(TCKind.tk_longdouble);
			} else 
			if (tc.equalsIgnoreCase("tkWchar")) {
			    return getOrb.get_primitive_tc(TCKind.tk_wchar);
			} else 
			if (tc.equalsIgnoreCase("tkWString")) {
			    return getOrb.get_primitive_tc(TCKind.tk_wstring);
			} else 
			if (tc.equalsIgnoreCase("tkFixed")) {
			    return getOrb.get_primitive_tc(TCKind.tk_fixed);
			}
			*/
			
	}
    

	/**
	return the TCKind from the org.omg.CORBA.TCKind
	*/
	public static TCKind getTCKind(Any any) {
	    return any.type().kind();
	}
	
    /*************************************************************
    
    PropStruct CONVERSION UTILITIES
    
    *************************************************************/
    /**
    This operation is a helper for the trader export operation.
	This is the place where we can throw an Exception in case a type is
	used that is beyond the scope of the Trader
	@return BasicAttribute
	*/

	public static BasicAttribute createAttributeFromProperty(Property property) 
	                                                    throws InvalidAttributeValueException {

	    BasicAttribute returnAttribute = new BasicAttribute(prependPropertyString(property.name));
        TCKind kind = getTCKind(property.value);
	    Any any = property.value;
		switch ( kind.value() )
		{	
		    case TCKind._tk_string:
				returnAttribute.add(any.extract_string());
				break;
			case TCKind._tk_short:
				returnAttribute.add(new Integer(any.extract_short()).toString());
    			break;
			case TCKind._tk_long:
				returnAttribute.add(new Integer(any.extract_long()).toString());
				break;
			case TCKind._tk_ushort:
				returnAttribute.add(new Integer(any.extract_ushort()).toString());
				break;
			case TCKind._tk_ulong:
				returnAttribute.add(new Integer(any.extract_ulong()).toString());
				break;
			case TCKind._tk_float:
				returnAttribute.add(new Float(any.extract_float()).toString());
				break;
			case TCKind._tk_double:
                returnAttribute.add(new Double(any.extract_double()).toString());
				break;
			case TCKind._tk_boolean:
			    returnAttribute.add(new Boolean(any.extract_boolean()).toString());
				break;
			case TCKind._tk_char:
			    returnAttribute.add(new Character(any.extract_char()).toString());
				break;
			// If you get here the param is not supported
			default:
			    throw new InvalidAttributeValueException( "Property: " + property.toString() + " not Supported");
			/*  
			// Here are the unsupported type codes
			// Leave just in case they are needed
			// in the future
			case TCKind._tk_null:
				 break;
			case TCKind._tk_void:
				break;
			case TCKind._tk_octet:
				break;
			case TCKind._tk_any:
				break;
			case TCKind._tk_TypeCode:
				break;
			case TCKind._tk_Principal:
				break;
			case TCKind._tk_objref:
				break;
			case TCKind._tk_struct:
				break;
			case TCKind._tk_union:
				break;
			case TCKind._tk_enum:
				break;
			case TCKind._tk_sequence:
				break;
			case TCKind._tk_array:
				break;
			case TCKind._tk_alias:
				break;
			case TCKind._tk_except:
				break;
			case TCKind._tk_longlong:
				break;
			case TCKind._tk_ulonglong:
				break;
			case TCKind._tk_longdouble:
				break;
			case TCKind._tk_wchar:
				break;
			case TCKind._tk_wstring:
				break;
			case TCKind._tk_fixed:
				break;
			*/
	    }

		return returnAttribute;
	}
	public static BasicAttribute addValueToAttribute(BasicAttribute previousAttribute, Property property) 
	                                                    throws InvalidAttributeValueException {

	    BasicAttribute returnAttribute = previousAttribute;
        TCKind kind = getTCKind(property.value);
	    Any any = property.value;
		switch ( kind.value() )
		{	
		    case TCKind._tk_string:
				returnAttribute.add(any.extract_string());
				break;
			case TCKind._tk_short:
				returnAttribute.add(new Integer(any.extract_short()).toString());
    			break;
			case TCKind._tk_long:
				returnAttribute.add(new Integer(any.extract_long()).toString());
				break;
			case TCKind._tk_ushort:
				returnAttribute.add(new Integer(any.extract_ushort()).toString());
				break;
			case TCKind._tk_ulong:
				returnAttribute.add(new Integer(any.extract_ulong()).toString());
				break;
			case TCKind._tk_float:
				returnAttribute.add(new Float(any.extract_float()).toString());
				break;
			case TCKind._tk_double:
                returnAttribute.add(new Double(any.extract_double()).toString());
				break;
			case TCKind._tk_boolean:
			    returnAttribute.add(new Boolean(any.extract_boolean()).toString());
				break;
			case TCKind._tk_char:
			    returnAttribute.add(new Character(any.extract_char()).toString());
				break;
			// If you get here the param is not supported
			default:
			    throw new InvalidAttributeValueException( "Property: " + property.toString() + " not Supported");
			/*  
			// Here are the unsupported type codes
			// Leave just in case they are needed
			// in the future
			case TCKind._tk_null:
				 break;
			case TCKind._tk_void:
				break;
			case TCKind._tk_octet:
				break;
			case TCKind._tk_any:
				break;
			case TCKind._tk_TypeCode:
				break;
			case TCKind._tk_Principal:
				break;
			case TCKind._tk_objref:
				break;
			case TCKind._tk_struct:
				break;
			case TCKind._tk_union:
				break;
			case TCKind._tk_enum:
				break;
			case TCKind._tk_sequence:
				break;
			case TCKind._tk_array:
				break;
			case TCKind._tk_alias:
				break;
			case TCKind._tk_except:
				break;
			case TCKind._tk_longlong:
				break;
			case TCKind._tk_ulonglong:
				break;
			case TCKind._tk_longdouble:
				break;
			case TCKind._tk_wchar:
				break;
			case TCKind._tk_wstring:
				break;
			case TCKind._tk_fixed:
				break;
			*/
	    }

		return returnAttribute;
	}
    /**
    returns an BasicAttribute supporting the prop that is passed in
    @param prop - property struct
    @exception - Exception means that the type code passed in in the
    property is not supported
    */
	public static BasicAttribute getTypeCode(PropStruct prop) 
	{
	    BasicAttribute tc = null;
	    try {
	        tc = new BasicAttribute(getTypeCodeFromKind( prop.value_type.kind()));
		    tc.add(prependPropertyString(prop.name));
		}
		    catch (InvalidAttributeValueException  iaex) {
		    // Dont do anything, except let it return a null tc   
		}
		return tc;
	}
    /*
    Convert a PropertyMode CONSTANT to a String
    @param mode - a CORBA PropertyMode object
    @return - a string
    */
    public static String getStringFromPropertyMode(int mode) {
            String returnString = null;
            switch(mode)
				{
					case PropertyMode._PROP_READONLY:
						returnString = PROPREADONLY;
						break;
					case PropertyMode._PROP_MANDATORY:
						returnString =  PROPMANDATORY;
						break;
					case PropertyMode._PROP_MANDATORY_READONLY:
						returnString = PROPMANDATORYREADONLY;
                        break;
					default:
						returnString = PROPNORMAL;						
						break;
		    }
		    return returnString;
	}
	
	 /*
    Convert a String to a PropertyMode 
    @param aMode - a string 
    @return - a CORBA PropertyMode object
    */
    public static PropertyMode getPropertyModeFromString(String aMode) {
       // System.out.println("mode = " + aMode);
	        if (aMode.equalsIgnoreCase(PROPNORMAL)) {
				return (PropertyMode)PropertyMode.PROP_NORMAL;
		    }
	        if (aMode.equalsIgnoreCase(PROPREADONLY)) {
				return PropertyMode.PROP_READONLY;
		    }
			if (aMode.equalsIgnoreCase(PROPMANDATORY)) {
				return PropertyMode.PROP_MANDATORY;
			}
			if (aMode.equalsIgnoreCase(PROPMANDATORYREADONLY)) {
				return PropertyMode.PROP_MANDATORY_READONLY;
		    }
		    // default -
		    return PropertyMode.PROP_NORMAL;
	}

    /*************************************************************
    
    String CONVERSION UTILITIES
    
    *************************************************************/
	/**
    Removes special characters from strings
    */
	public static String stripped( String what )
	{
		StringBuffer newName = new StringBuffer();
		StringCharacterIterator iter = new StringCharacterIterator(what);
		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			switch( c )
			{
				case '\\':
				case ':':
				case '"':
				case '/':
				case '.':
				case ';':
					break;
				default:
					newName.append(c);
			}
		}
		return newName.toString();
	}

	public static String standardizeIDLName( String aName )
	{
		StringBuffer newName = new StringBuffer();
		int start = aName.indexOf(':')+1;
		int end = aName.lastIndexOf(':');
		StringCharacterIterator iter = new StringCharacterIterator(aName, start, end, start);
		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			switch( c )
			{
				case '/':
				case '.':
					newName.append("::");
					break;
				default:
					newName.append(c);
			}
		}
		return newName.toString();
	}

	public static String escapeSlashes( String aName )
	{
		StringBuffer newName = new StringBuffer();
		StringCharacterIterator iter = new StringCharacterIterator(aName);
		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			switch( c )
			{
				case '/':
					newName.append("\\");  // want to fall through
				default:
					newName.append(c);
			}
		}
		return newName.toString();
	}
	
	/**
	Prepend "property" to the supplied string
	Used before instantiating the database
	@param inputName - the name of the property
	@return  the concatenated string
	*/
    public static String prependPropertyString(String inputName) {
        return "property" + inputName;
    }
    
    


     private static ORB getOrb() {
      return com.cboe.ORBInfra.ORB.Orb.init();
     }
     
     /*************************************************************
    
     JNDI UTILITIES
    
     *************************************************************/
     /**
     Standard query with a subtree scope
     @return NamingEnumeration
    */ 
    public static NamingEnumeration jndiSearch(DirContext dirContext, String searchBase, String filter)
	                                                                throws NamingException {                                                                             
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		return jndiSearch( dirContext, searchBase,  filter, controls);
    } 
    
    /**
    Use this one in case you need to modify countLimit
    @return NamingEnumeration
    
    */
     public static NamingEnumeration jndiSearch(DirContext dirContext, String searchBase, String filter, int countLimit)
	                                                                throws NamingException {                                                                             
	   
	    SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setCountLimit(countLimit);
		return jndiSearch( dirContext, searchBase,  filter, controls);

    } 
    /**
     @return NamingEnumeration
    */
    public static NamingEnumeration jndiSearch(DirContext dirContext, String searchBase, String filter, SearchControls controls)
	                                                                throws NamingException {                                                                             
		return dirContext.search(searchBase, filter, controls);	   
    } 
    
   /**
	Create a search filter string that will work with LDAP,  Convert it from the
	one supplied to the trader from the application
	Used before instantiating the database
	@param type - service type
	@param constraints - the constraints String from the Trader to convert
	@exception - IllegalConstraint thrown when the constraint cannot be parsed
	@return  the concatenated string
	*/
   public static String buildSearchFilter(String type, String constraints) throws IllegalConstraint{
        String returnString = null;
        try {
            DirectoryParser parser = new DirectoryParser(constraints);
	        ASTconstraint n = parser.constraint();
	        DirectoryConstraintNodeVisitor visitor = new DirectoryConstraintNodeVisitor();
	        returnString = (String)n.jjtAccept(visitor, null);
	        returnString = "(&(svc=" + type +")" + returnString +")";
	        //System.out.println("ldap constraint = " +returnString);
	    
	    } catch ( TokenMgrError pe ) {
	        throw new IllegalConstraint(constraints); 
	    
	    } catch ( Exception pe ) {
	        throw new IllegalConstraint(constraints); 
	    }
	    return returnString;

    }
    /**
     This code helps get around the problem of getting schema info 
     for LDAP.  
     @param schema - the Directory Context object
     @param objectClasses - a vector containing a list of the object class
     string names 
     @return two Vectors the first is the MUST have(Mandatory) attributes
        the second is the MAY have (Optional) attributes
    */ 				
	public static Vector[] getAttributeLists(DirContext schema, Vector objectClasses)
    throws NamingException {
    Vector mandatory = new Vector();
	Vector optional = new Vector();

	for (int i = 0; i < objectClasses.size(); i++) {
	    String oc = (String)objectClasses.elementAt(i);
	    Attributes ocAttrs = 
		schema.getAttributes("ClassDefinition/" + oc);
	    Attribute must = ocAttrs.get("MUST");
	    Attribute may = ocAttrs.get("MAY");

	    if (must != null) {
		addAttrNameToList(mandatory, must.getAll());
	    }
	    if (may != null) {
		addAttrNameToList(optional, may.getAll());
	    }
	}
	return new Vector[] {mandatory, optional};
    }	
    /**
     Private method for getAttributeLists  
    
    */ 	
    public static void addAttrNameToList(Vector store, NamingEnumeration vals) 
	throws NamingException {
	    while (vals.hasMoreElements()) {
		java.lang.Object val = vals.next();
		if (!store.contains(val)) {
		    store.addElement(val);
		}
	    }
    }

    
     /*************************************************************
    
     Lookup UTILITIES
    
     *************************************************************/
     /**
     Return an Offer object
     @param searchResult - a search result containing attributes of 
     an offer
     @exception NamingException	thrown from a JNDI error resulting from bad data in LDAP
     @exception SystemException	can be thrown from orb operation of string_to_object, 
                                can be a bad IOR causing this, so send a logging message to clean
                                up the offer in LDAP. 

     @exception UserException	can be thrown from orb operation of string_to_object, 
                                can be a bad IOR causing this, so send a logging message to clean
                                up the offer in LDAP. 
     @return new Offer object
     */
     public static Offer buildOffer( SearchResult searchResult ) throws NamingException,
                                                                        SystemException,
                                                                        UserException {
   
			String stringifiedIOR = null;
            Attributes attrs = searchResult.getAttributes();
			stringifiedIOR = (String)attrs.get("objectreference").get();
			Hashtable propsTable = new Hashtable();
			Any tempAny = null;
			NamingEnumeration attrEnum = attrs.getAll();
			Vector propVec = new Vector();
			Property[] propertySeq = getCorbaPropertyBuilder().buildProperties(attrEnum);	
			return new Offer(orb.string_to_object(stringifiedIOR), propertySeq) ;
     }
        	
	/**
     Return the current CorbaPropertyBuilder object
     
     @return CorbaPropertyBuilder
     */
		private static CorbaPropertyBuilder getCorbaPropertyBuilder(){
		    if (corbaPropertyBuilder == null) {
		        corbaPropertyBuilder = new CorbaPropertyBuilder();
		    }
		    return corbaPropertyBuilder;
		}
		

	 /*************************************************************
    
     Logging UTILITIES
    
     *************************************************************/
    
	/**
     Initialize the instance of the LoggingServiceInterface
     @param props - A java.util.properties object of the properties passed in from system management.
     @return LoggingServiceInterface
     */	
        public static LoggingServiceInterface initializeLogging(Properties props) throws AlreadyInitializedException, InitializationFailedException {
        // uncomment the next line for incr3 and beyond
        LoggingServiceImpl.setName( "LoggingService_TradingService1" );
        LoggingServiceImpl.initialize( props.getProperty("LOGGINGSERVICEFILE"));
        return LoggingServiceImpl.getInstance( props.getProperty("LOGGINGNAME") );
     }
     

	/**
	 * This method will force a reconnection to happen between this context and the ldap server.  Normally the
	 * reconnect method is used when you want to change connection-controls, but should suffice to attempt
	 * recovery of a failed connection.  Catch exceptions here, but return without rethrowing any exceptions.
	 * The callers already have ways of dealing with failures.
	 *
	 * @param ldapCtx a <code>LdapContext</code> value
	 * @return a <code>DirContext</code> value
	 */
	public static DirContext reconnectLdapContext( LdapContext ldapCtx ) {

		try {
			ldapCtx.reconnect( null );
		} catch ( CommunicationException ce ) {
			LdapContext dirCtx = null;
			LdapContext saveMainCtx = mainDirContext;
			mainDirContext = null;
			try {
				// Try to get around reconnection problems.  Create a brand new
				// main context and get a new-instance from it.
				dirCtx = (LdapContext)createDirContext( null );
				ldapCtx = dirCtx;
			}
			catch ( NamingException e ) {
				// Still can't get reconnected, so set things back.
				mainDirContext = saveMainCtx;
			}
		} catch ( NamingException e ) {
		}
		finally {
			return ldapCtx;
		}
	}

	/**
	 * This method refreshes the schema from a DirContext.  I need to do this after a reconnect of
	 * the DirContext is done.
	 *
	 * @param ldapCtx a <code>DirContext</code> value
	 * @param schema a <code>DirContext</code> value
	 * @return a <code>DirContext</code> value
	 */
	public static DirContext refreshSchemaFromContext( DirContext ldapCtx, DirContext schema ) {

		try {
			return ldapCtx.getSchema("");
		} catch ( Throwable t ) {
		}

		return schema; // Return original

	}

	/**
	 * This method initializes the main Directory Context and derives a new one
	 * from the main.
	 *
	 * @param props a <code>Properties</code> value
	 * @return a <code>DirContext</code> value
	 */
	public synchronized static DirContext createDirContext( Properties props ) throws NamingException {
		if ( mainDirContext == null ) {
			if ( dirContextEnvVars == null && props != null ) {
				dirContextEnvVars = new Hashtable();
				dirContextEnvVars.put( Context.INITIAL_CONTEXT_FACTORY, props.getProperty("INITIAL_CONTEXT_FACTORY") );
				dirContextEnvVars.put( Context.PROVIDER_URL, props.getProperty("PROVIDER_URL") );
				dirContextEnvVars.put( Context.SECURITY_AUTHENTICATION, props.getProperty("SECURITY_AUTHENTICATION") );
				dirContextEnvVars.put( Context.SECURITY_PRINCIPAL, props.getProperty("SECURITY_PRINCIPAL") );
				dirContextEnvVars.put( Context.SECURITY_CREDENTIALS, props.getProperty("SECURITY_CREDENTIALS") );
				dirContextEnvVars.put( Context.BATCHSIZE, "20" );
				if ( props.getProperty("HAVE_SCHEMA_BUG").equalsIgnoreCase("true") ) {
					dirContextEnvVars.put( props.getProperty("SCHEMA_BUG_PROPERTY"), "true");
				}
				if ( (props.getProperty("DO_BER_TRACE")).equalsIgnoreCase("true") ) {
					String temp = props.getProperty("BER_TRACE_STREAM", "stdout");
					if (temp.equalsIgnoreCase("stdout")) dirContextEnvVars.put( props.getProperty("BER_TRACE_PROPERTY"), System.out);
					else dirContextEnvVars.put( props.getProperty("BER_TRACE_PROPERTY"), System.err);
				}
			}

			mainDirContext = new InitialLdapContext(dirContextEnvVars, null);
		}

		return mainDirContext.newInstance( null );
	}
}
