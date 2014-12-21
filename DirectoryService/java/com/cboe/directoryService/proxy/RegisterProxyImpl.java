//-------------------------------------------------------------------------
//FILE: RegisterProxyImpl.java
//
// PACKAGE: com.cboe.directoryService.proxy
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------
 
  
package com.cboe.directoryService.proxy;

// java packages
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.Admin;
import org.omg.CosTrading.DuplicatePropertyName;
import org.omg.CosTrading.IllegalConstraint;
import org.omg.CosTrading.IllegalOfferId;
import org.omg.CosTrading.IllegalPropertyName;
import org.omg.CosTrading.IllegalServiceType;
import org.omg.CosTrading.Link;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.MissingMandatoryProperty;
import org.omg.CosTrading.POA_Register;
import org.omg.CosTrading.Property;
import org.omg.CosTrading.PropertyTypeMismatch;
import org.omg.CosTrading.Proxy;
import org.omg.CosTrading.ReadonlyDynamicProperty;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.UnknownOfferId;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTrading.RegisterPackage.InvalidObjectRef;
import org.omg.CosTrading.RegisterPackage.MandatoryProperty;
import org.omg.CosTrading.RegisterPackage.NoMatchingOffers;
import org.omg.CosTrading.RegisterPackage.OfferInfo;
import org.omg.CosTrading.RegisterPackage.ProxyOfferId;
import org.omg.CosTrading.RegisterPackage.ReadonlyProperty;
import org.omg.CosTrading.RegisterPackage.UnknownPropertyName;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.PortableServer.POA;

/**
*  This proxy implementation calls the actual implementation
*  This is the implementation class that provides the services to
*  describe, export, and withdraw offers.
*
* @author             Murali Yellepeddy
*/


public class RegisterProxyImpl
extends POA_Register
{
	/*
	* The reference of the actual register object
	*/

	private Register actual_Register; 
	
	/**
	*  The ServiceTypeRepository reference
	*/
	private ServiceTypeRepository serviceTypeRepository;
	
	
	/**
    	*  Holder for properties retrieved from file or system management
	*/
	private Properties traderProperties;
	
	/**
    	*  The reference to the Lookup interface
	*/
	private Lookup lookup;
	
	/**
    	*  The reference to the Admin interface
	*/
	private Admin admin;
	
	/**
	*  reference to the ORB
	*/
	private ORB orb;
	/**
	*  reference to the persistent poa used under the rootPOA
	*/
	private POA poa;
    


	public RegisterProxyImpl() { }
	
    /**

     Constructor

    @param repo an instance of a ServicesTypeRepository
    @param props an instance of the properties for this trader
    */
	public RegisterProxyImpl( Properties props, POA poa_p, Register aRegister)
	{

       		initialize( props);
       		poa = poa_p;
		actual_Register = aRegister;
        
    	}
    
	public POA _default_POA()
    	{
     		return poa;
    	}
    
    	/**
    	* Initialize the variables
    	*/
	private void initialize( Properties props) {
	    
	    	if ( props == null) {
            		throw new NullPointerException();
        	}
        
	    	orb = com.cboe.ORBInfra.ORB.Orb.init();
	   
		traderProperties = props;
		
	}

	
	/**
    The export operation allows an advertiser to advertise a
     service through the trader.

    @param objectRef - This is the object reference of the
        advertised service
    @param serviceTypeName - This is the name of the service
      type examples are "EventChannel2", "OrderHandlingService4"
    @param properties - This is an optional parameter that contains
      a Property array that can be used for subsequent query purposes.

    @return String containing the IOR of the Service
    
    @exception org.omg.CosTrading.InvalidObjectRef - thrown if unexportable 
    @exception org.omg.CosTrading.IllegalServiceType - thrown if a schema violation is encountered
    @exception org.omg.CosTrading.UnknownServiceType - thrown if the service type is not found
    @exception org.omg.CosTrading.IllegalPropertyName - thrown if the attribute is in use
    @exception org.omg.CosTrading.PropertyTypeMismatch - thrown if the value supplied with the 
                                                         property does not match its type
    @exception org.omg.CosTrading.ReadonlyDynamicProperty - thrown when attempting to assign a value to a readonly property
    @exception org.omg.CosTrading.MissingMandatoryProperty - thrown when a mandatory property is not supplied
    @exception org.omg.CosTrading.DuplicatePropertyName - thrown when the property name is used more than once in the properties param 
    
    */

	public synchronized String export(org.omg.CORBA.Object reference, String type, Property[] properties)
	throws InvalidObjectRef, IllegalServiceType, UnknownServiceType, IllegalPropertyName,
	PropertyTypeMismatch, ReadonlyDynamicProperty, MissingMandatoryProperty, DuplicatePropertyName, org.omg.CosTrading.RegisterPackage.InterfaceTypeMismatch
	{
		return actual_Register.export(reference,type, properties);
	}



	/**	The withdraw operation removes the service offer from the trader (i.e., after
		withdraw the offer can no longer be returned as the result of a query).

		@param String the Offer ID which was originally returned by export.
		@exception IllegalOfferId if the ID does not obey the rules for offer identifiers
		@exception UnknownOfferId if there is no offer within the trader with that ID
	*/
	public synchronized void withdraw(String id)
	throws IllegalOfferId, UnknownOfferId,org.omg.CosTrading.RegisterPackage.ProxyOfferId
	{
		actual_Register.withdraw(id);	
	}
	
	/**
	The withdraw_using_constraint operation withdraws
	a set of offers from a trader. Using the constraints
	in the same way as a query.

    @param serviceTypeName - This is the name of the
      service type examples are "EventChannel2", "OrderHandlingService4"
    @param constr - This is an optional field that
      contains a string of the properties that are to be used for query purposes.
    @exception org.omg.CosTrading.IllegalServiceType - thrown if a schema violation is encountered
    @exception org.omg.CosTrading.UnknownServiceType - thrown if the service type is not found
    @exception org.omg.CosTrading.IllegalConstraint - thrown if the constraint cannot be converted to an LDAP filter
    @exception org.omg.CosTrading.NoMatchingOffers - thrown if no offers for the constraint are found
    
    */
	public synchronized void withdraw_using_constraint(String type, String constr)
	throws IllegalServiceType, UnknownServiceType, IllegalConstraint, NoMatchingOffers {
		actual_Register.withdraw_using_constraint(type,constr);
	}



	/**	The describe operation returns the information about an offered service that is held by
		the trader. It comprises the "reference" of the offered service, the "type" of the service
		offer, and the "properties" that describe this offer of service.

		@param String the Offer ID which was originally returned by export.
		@return OfferInfo the offer information as described above
		@exception IllegalOfferId if the ID does not obey the rules for offer identifiers
		@exception UnknownOfferId if there is no offer within the trader with that ID
		@exception ProxyOfferId if ID identifies a proxy offer rather than an ordinary offer
	*/
	public synchronized OfferInfo describe(String id)
	throws IllegalOfferId, UnknownOfferId,org.omg.CosTrading.RegisterPackage.ProxyOfferId
	{
		return actual_Register.describe(id);
	}

	/**
     The "modify" operation is used to alter the description of a
     trader service offer. The operation can add new non-mandatory
     properties, change the values of existing non-read only
     properties, or delete existing properties (except mandatory
     and read only properties). The object reference and the service
     type of the offer can not be changed by the "modify" operation.
     To change the object reference of service type, the offer must
     be withdrawn and then re-exported to the trader.  The "modify"
     operation either succeeds completely or it fails.

    @param id The "id" parameter is used to identify the trader's
      offer that is to be modified.

    @param del_list The "del_list" parameter is used to identify
       the properties that are to be removed from the service offer.

    @param modify_list The "modify_list" parameter is used to
      identify the service offer's properties that are to be altered.
      If a property in the list is not currently in the service offer, then it is added to the offer.
    @exception org.omg.CosTrading.IllegalOfferId - thrown if a schema violation is encountered
    @exception org.omg.CosTrading.UnknownOfferId - thrown if the service type is not found    
    @exception org.omg.CosTrading.IllegalPropertyName - thrown if the attribute is in use
    @exception org.omg.CosTrading.UnknownPropertyName - thrown if the service type is not found  
    @exception org.omg.CosTrading.PropertyTypeMismatch - thrown if the value supplied with the 
                                                         property does not match its type
    @exception org.omg.CosTrading.MandatoryProperty - thrown when a mandatory property is in the del_list 
    @exception org.omg.CosTrading.ReadonlyProperty - thrown when an illegal action is taken on a readonly property
    @exception org.omg.CosTrading.DuplicatePropertyName - thrown when the same property name is used more than once in the properties param 
    
    */
	public synchronized void modify(String id, String[] del_list, Property[] modify_list) 
	throws IllegalOfferId, UnknownOfferId, IllegalPropertyName,
	UnknownPropertyName, PropertyTypeMismatch, 
	MandatoryProperty, ReadonlyProperty, DuplicatePropertyName, org.omg.CosTrading.RegisterPackage.ProxyOfferId, org.omg.CosTrading.NotImplemented, org.omg.CosTrading.ReadonlyDynamicProperty
	{
		actual_Register.modify(id,del_list,modify_list);
	}

    
    /** Validate the incoming IOR 
    *@param id - the IOR string
    *@return boolean - true if valid, otherwise false
    **/
    private boolean validOfferId(String id) throws IllegalOfferId {
     String prefix = "IOR:";
     
     if ((id == null) || id.length() < 40) {
        throw new IllegalOfferId(id);
     }
     String compareId = id.substring(0,4);
     if (compareId.equals(prefix)) {
        return true;
     }
     return false;
    }
	/**
	Create a new tie object for the Lookup interface if it does not already exist
    @return the Lookup interface
    */
	public Lookup lookup_if()
	{
		return lookup;
	}

	/**
	Create a new tie object for the Register interface if it does not already exist
    @return the Register interface
    */
	public Register register_if() {
		return _this();
    }

	/**
    return the Link interface
    */
    public Link link_if() {
        Link linkRef = null;
        return linkRef;
    }

	/**
    	*return the Proxy Interface ( if implemented)
    	*@exception org.omg.CosTrading.ProxyPackage.ProxyNotSupported - always thrown 
    	*/
    	public Proxy proxy_if() {
        	return null;
    	}

	/**
    return the Register Interface ( if implemented)
    @exception org.omg.CosTrading.RegisterPackage.RegisterNotSupported - always thrown 
    */
    public Register resolve(String[] name) throws org.omg.CosTrading.RegisterPackage.IllegalTraderName,
        org.omg.CosTrading.RegisterPackage.UnknownTraderName,
        org.omg.CosTrading.RegisterPackage.RegisterNotSupported {
        throw new org.omg.CosTrading.RegisterPackage.RegisterNotSupported();

    }

	/**
	Create a new tie object for the Admin interface if it does not already exist
    @return the Admin interface
    */
    public Admin admin_if() {
	 return admin;
    }
    /**
       set the Admin interface
       @param anAdmin - an Admin object
    */
    public void setAdmin_if(Admin anAdmin) {
		admin = anAdmin;
	 
    }
    
	/**
    Return true if we will support modifyable properties
    @return boolean
    */
	public boolean supports_modifiable_properties()
	{
		return true;
	}
    /**
       Set the  tie object representing the remote repository
     */
	public org.omg.CORBA.Object set_type_repos(org.omg.CORBA.Object repository){
		serviceTypeRepository = (ServiceTypeRepository)repository;
		return repository;
	}
	/**
    Return the tie object representing the remote repository 
    it must be narrowed after calling this method
    @return org.omg.CORBA.Object 
    */
	public org.omg.CORBA.Object type_repos()
	{
		return (org.omg.CORBA.Object)serviceTypeRepository;
	}

	/**
    Return  true if we support dynamic properties.
    @return boolean
    */
	public boolean supports_dynamic_properties()
	{
		return false;
	}

	/**
    Return true if we support proxy offers.
    @return boolean
    */
	public boolean supports_proxy_offers()
	{	    
		return false;
	}
}
