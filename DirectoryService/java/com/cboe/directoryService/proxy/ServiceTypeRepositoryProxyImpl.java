//-----------------------------------------------------------------------
// FILE: ServiceTypeRepositoryProxyImpl.java
//
// PACKAGE: com.cboe.directoryService.proxy
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------
package com.cboe.directoryService.proxy;

// local packages
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.DuplicatePropertyName;
import org.omg.CosTrading.IllegalPropertyName;
import org.omg.CosTrading.IllegalServiceType;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTradingRepos.POA_ServiceTypeRepository;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.DuplicateServiceTypeName;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.HasSubTypes;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.IncarnationNumber;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.InterfaceTypeMismatch;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ListOption;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ServiceTypeExists;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.SpecifiedServiceTypes;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.TypeStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ValueTypeRedefinition;
import org.omg.PortableServer.POA;

import com.cboe.common.log.Logger;

/**
*  This is the implementation class that services all requests
*  relating to service types.  This class provides the operations
*  add_type, list_types, remove_type and describe_type.
*
* @author             Murali Yellepeddy
*/


public class ServiceTypeRepositoryProxyImpl
extends POA_ServiceTypeRepository
{

	/*
	* A reference to the actual serviceTypeRepository object
	*/
	private ServiceTypeRepository actual_ServiceTypeRepository; 

	/**
    	*  Holder for properties retrieved from file or system management
	*/
	private Properties traderProperties;
	 
	/**
	*  reference to the ORB
	*/
	private ORB orb;

	/**
	*  reference to the persistent poa used under the rootPOA
	*/
	private POA poa;
	

	public ServiceTypeRepositoryProxyImpl()
	{ }

	public ServiceTypeRepositoryProxyImpl( Properties props, POA poa_p,ServiceTypeRepository aServiceTypeRepository)
	{
	   initialize(props);
	   poa = poa_p;
	   actual_ServiceTypeRepository = aServiceTypeRepository;	
	}
	
	public POA _default_POA()
    	{
     		return poa;
    	}

    /**
    * Initialize variables
    */
	private void initialize( Properties props) {
		traderProperties = props;
		orb = com.cboe.ORBInfra.ORB.Orb.init();
	    
    	}

	
	/**	The add_type operation enables the creation of new service types in the service type
		repository. 
		@param name the "name" for the new type
		@param if_name the identifier for the interface associated with instances of this service type
		@param props the properties definitions for this service type
		@param super_types the service type names of the immediate super-types to this service type
		@return new incarnation number
		@exception ServiceTypeExists type already exists
	*/
	public synchronized IncarnationNumber add_type(String name, String if_name, PropStruct[] props, String[] super_types)
	throws ServiceTypeExists, InterfaceTypeMismatch,ValueTypeRedefinition, IllegalServiceType,IllegalPropertyName, UnknownServiceType, DuplicatePropertyName,DuplicateServiceTypeName
	{
		return actual_ServiceTypeRepository.add_type(name,if_name,props,super_types);
	}
	
 
	/**	The remove_type operation removes the named type from the service type repository.
		@param name the service type name
		@exception  IllegalServiceType if "name" is malformed
		@exception UnknownServiceType if "name" does not exist within the repository
	*/
	public synchronized void remove_type(String name)
	throws IllegalServiceType, UnknownServiceType, HasSubTypes
	{
		actual_ServiceTypeRepository.remove_type(name);
		
	}

	/**	The list_types operation permits a client to obtain the names of service types
		which are in the repository.
		@param which_types permits the client to specify all types known to the repository
		@return String[] - The names of the service types (for subsequent querying via the
		describe_type operation.
	*/
	public synchronized String[] list_types(SpecifiedServiceTypes which_types){
	    Logger.debug(this.getClass().getName() + " the type is " + which_types);
	    Logger.debug(this.getClass().getName() + " the type is " + which_types.discriminator());
		SpecifiedServiceTypes specifiedTypes = new SpecifiedServiceTypes(ListOption.all);
		return actual_ServiceTypeRepository.list_types(specifiedTypes);
	}


	/**	The describe_type operation permits a client to obtain the details for a
		particular service type.
		@param name the service type name
		@return typeStruct - The TypeStruct containing information for this type
		@exception IllegalServiceType if name is malformed
		@exception UnknownServiceType if name does not exist within the repository
	*/
	public synchronized TypeStruct describe_type(String name)
		throws IllegalServiceType, UnknownServiceType
	{
        	return actual_ServiceTypeRepository.describe_type(name);
	}


	/**	
	    Not Implemented
	    The fully_describe_type operation permits a client to obtain the details for a
		particular service type. The property sequence returned in the TypeStruct
		includes all properties inherited from the transitive closure of its super types;
		the sequence of super types in the TypeStruct contains the names of the types
		in the transitive closure of the super type relation.
		@return servicetype struct
		@exception IllegalServiceType if name is malformed
		@exception UnknownServiceType if name does not exist within the repository
	*/
	public TypeStruct fully_describe_type(String name)
	{
		return null;
	}

	/**	
        Return the IncarnationNumber
    */
	public IncarnationNumber incarnation()
	{
		return new IncarnationNumber(0,0);
	}

	/**	
        Stub code for later use (if needed) 
    */
    public void mask_type(String name)
	{
		return;
	 }
	 
	/**	
        Stub code for later use (if needed) 
    */
	public void unmask_type(String name)
	{
		return;
	 }





}  //<EOF>
