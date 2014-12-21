//-----------------------------------------------------------------------
// FILE: ServiceTypeRepositoryImpl.java
//
// PACKAGE: com.cboe.directoryService>CosTradingRepos
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------
package com.cboe.directoryService;

import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.IllegalServiceType;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPOA;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.AlreadyMasked;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.IncarnationNumber;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.NotMasked;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ServiceTypeExists;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.SpecifiedServiceTypes;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.TypeStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ValueTypeRedefinition;
import org.omg.PortableServer.POA;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.directoryService.persist.TraderServiceType;
import com.cboe.infrastructureUtility.SQLExceptionHelper;
import com.objectwave.persist.QueryException;

import static com.cboe.directoryService.TraderLogBuilder.*;


/**
*  This is the implementation class that services all requests
*  relating to service types.  This class provides the operations
*  add_type, list_types, remove_type and describe_type.
*
* @author             Judd Herman
*/
public class ServiceTypeRepositoryImpl
extends ServiceTypeRepositoryPOA
{
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = ServiceTypeRepositoryImpl.class.getSimpleName();
		
	/** Holder for properties retrieved from file or system management */
	private Properties traderProperties;

	/** Logging resource bundle */
	private static ResourceBundle rb = null;

	/** reference to the ORB */
	private ORB orb;

	/** reference to the persistent poa used under the rootPOA */
	private POA poa;

	/** Database utilities */
	private TraderDBUtil dbUtil;

	public ServiceTypeRepositoryImpl()
	{
		dbUtil = TraderDBUtil.getInstance();
	}

	public ServiceTypeRepositoryImpl(Properties props, POA poa_p)
	{
		initialize(props);
		poa = poa_p;
		dbUtil = TraderDBUtil.getInstance();

	}

	public POA _default_POA()
	{
		return poa;
	}

	/**
	* Initialize variables
	*/
	private void initialize(Properties props)
	{
		if (props == null) {
			throw new NullPointerException();
		}		

		traderProperties = props;
		orb = com.cboe.ORBInfra.ORB.Orb.init();
		rb = TraderServer.initializeLoggingRb();
	}

	/**	
	* Return the IncarnationNumber
	* @return an IncarnationNumber with 0 high and 0 low
	*/
	public IncarnationNumber incarnation()
	{
		return new IncarnationNumber(0,0);
	}

	/**
	* The add_type operation enables the creation of new service types in the service type
	* repository. If the type creation is successful, an IncarnationNumber is returned as the
	* value of the operation. Incarnation numbers are opaque values that are assigned to each
	* modification to the repository's state. An incarnation number can be quoted when
	* invoking the list_types operation to retrieve all changes to the service repository since
	* a particular logical time.
	*
	* If a property value type associated with this service type illegally modifies the value
	* type of a super-type property, or if two super-types incompatibly declare value
	* types for the same property name, then the ValueTypeRedefinition exception is
	* raised.
	*
	* @param name the "name" for the new type
	* @param if_name the identifier for the interface associated with instances of this service type
	* @param props the properties definitions for this service type
	* @param super_types the service type names of the immediate super-types to this service type
	* @return new incarnation number
	* @exception ServiceTypeExists type already exists
	*/
	public IncarnationNumber add_type( String name, 
									  String if_name, 
									  PropStruct[] props, 
									  String[] super_types)
	throws ServiceTypeExists, ValueTypeRedefinition
	{
		final String METHOD_ID = "add_type";
		Logger.sysNotify(formatEnter(CLASS_ID,METHOD_ID, "ServiceTypeName: %s Identifier %s", name, if_name));

		IncarnationNumber inc = new IncarnationNumber(0,0);
		try {
			dbUtil.addServiceType(name, if_name, props, super_types, inc);
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
		}
		catch(ServiceTypeExists ste) {
			/*
			 *  log catch and release, it's not logging the exception here on purpose because this event is so common. clients
			 *  add types assuming its arleady added 
			 */
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Duplicate add attempt, throwing back to client"));	
			throw ste;
		}
		catch (ValueTypeRedefinition vtr)
		{
			// log catch and release
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Request attempted to redefine existing properties, throwing back to client"));	
			throw vtr;
		}
		catch(QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		
		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

		return inc;
	}

	/**
	* The remove_type operation removes the named type from the service type repository.
	* @param name the service type name
	* @exception IllegalServiceType if "name" is malformed
	* @exception UnknownServiceType if "name" does not exist within the repository
	*/
	public void remove_type(String name)
	throws IllegalServiceType, UnknownServiceType
	{
		final String METHOD_ID = "remove_type";
		Logger.sysNotify(formatEnter(CLASS_ID,METHOD_ID, "ServiceTypeName %s", name));

		if ( name == null || name.length() == 0 ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Name parameter cannot be null or of length zero, notifying client."));
			throw new IllegalServiceType(name);
		}

		try {
			TraderServiceType aType = dbUtil.getServiceTypeByName(name);
			if ( aType != null ) {
				dbUtil.deleteServiceType(aType);
				Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
			}
			else {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unknown service for name, notifying client."));
				throw new UnknownServiceType(name);
			}
		}
		catch(QueryException qe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		Logger.sysNotify(formatExit(CLASS_ID,METHOD_ID));

	}

	/**
	* The list_types operation permits a client to obtain the names of service types
	* which are in the repository.
	*
	* <b>note:</b> this implementation ignores the which_types parameter, and always
	* acts as if which_type == "all_service_type"
	*
	* @param which_types permits the client to specify all types known to the repository
	* @return String[] - The names of the service types (for subsequent querying via the
	* describe_type operation.
	*/
	public String[] list_types(SpecifiedServiceTypes which_types)
	{
		final String METHOD_ID = "list_types";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "SpecifiedServicesTypes: %s", which_types));

		String[] retVal = null;
		try {
			retVal = dbUtil.getAllServiceTypeNames();
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
		}
		catch(QueryException qe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		if ( retVal == null ) {
			retVal = new String[0];
		}

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

		return retVal;
	}

	/**
	* The describe_type operation permits a client to obtain the details for a
	* particular service type.
	* @param name the service type name
	* @return typeStruct - The TypeStruct containing information for this type
	* @exception IllegalServiceType if name is malformed
	* @exception UnknownServiceType if name does not exist within the repository
	*/
	public TypeStruct describe_type(String name)
	throws IllegalServiceType, UnknownServiceType
	{
		final String METHOD_ID = "describe_type";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "ServiceTypeName: %s", name));

		TraderServiceType aType = null;
		try {
			aType = dbUtil.getServiceTypeByName(name);
		}
		catch(QueryException qe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		if ( aType == null ) {
			Logger.sysWarn(format(CLASS_ID,METHOD_ID, "Unknown service type"));
			throw new UnknownServiceType(name);
		}
		else {
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
			Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

			return aType.toStruct();
		}
	}

	/**	
	* Not Implemented
	* The fully_describe_type operation permits a client to obtain the details for a
	* particular service type. The property sequence returned in the TypeStruct
	* includes all properties inherited from the transitive closure of its super types;
	* the sequence of super types in the TypeStruct contains the names of the types
	* in the transitive closure of the super type relation.
	* @return servicetype struct
	* @exception IllegalServiceType if name is malformed
	* @exception UnknownServiceType if name does not exist within the repository
	*/
	public TypeStruct fully_describe_type(String name)
	{
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Mask the supplied Service Type
	* @param name the service type name
	*/
	public void mask_type(String name)
	throws UnknownServiceType, AlreadyMasked
	{
		final String METHOD_ID = "mask_type";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "ServiceTypeName: %s", name));
		
		TraderServiceType aType = null;
		try {
			aType = dbUtil.getServiceTypeByName(name);
		}
		catch(QueryException qe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		if ( aType == null ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Service type not defined, skipping"));
			throw new UnknownServiceType(name);
		}
		else if ( aType.isMasked() ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "name %s already masked", name));
			throw new AlreadyMasked(name);
		}
		else {
			try {
				dbUtil.maskServiceType(aType);
				Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
			}
			catch(QueryException qe) {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
				shutdownIfFatal(qe);
				throw new org.omg.CORBA.UNKNOWN(qe.toString());
			}
		}

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
	}

	/**	
	* Unmask the supplied Service Type
	* @param name the service type name
	*/
	public void unmask_type(String name)
	throws NotMasked, UnknownServiceType
	{
		final String METHOD_ID = "unmask_type";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "ServiceTypeName: %s", name));
	
		TraderServiceType aType = null;
		try {
			aType = dbUtil.getServiceTypeByName(name);
		}
		catch(QueryException qe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		if ( aType == null ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unknown service for %s", name));
			throw new UnknownServiceType(name);
		}
		else if ( !aType.isMasked() ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "%s not masked", name));
			throw new NotMasked(name);
		}
		else {
			try {
				dbUtil.unmaskServiceType(aType);
				Logger.sysNotify(format(CLASS_ID,METHOD_ID, "repository action"));
			}
			catch(QueryException qe) {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
				shutdownIfFatal(qe);
				throw new org.omg.CORBA.UNKNOWN(qe.toString());
			}
		}

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
	}

    /**
    * common routine for the SQLException Helper
    */
    private static void shutdownIfFatal(QueryException qe)
    {
        if (SQLExceptionHelper.RankSQLException((SQLException)qe.getOriginalException()) == SQLExceptionHelper.FATAL_SQL_ERROR) {
		   Logger.sysAlarm( rb, InfraLoggingRb.DS_TS_FATAL_SQL_EXCEPTION, null, qe ); // leaving in in case an alarm is set
		   Logger.sysAlarm(format(CLASS_ID, "shutdownIfFatal", "EMERGENCY SHUTDOWN! EXITING APPLICATION"));
		   System.exit(1);
        }
    }
}
