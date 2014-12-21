//-------------------------------------------------------------------------
//FILE: RegisterImpl.java
//
// PACKAGE: com.cboe.directoryService
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------
package com.cboe.directoryService;

import static com.cboe.directoryService.TraderLogBuilder.format;
import static com.cboe.directoryService.TraderLogBuilder.formatEnter;
import static com.cboe.directoryService.TraderLogBuilder.formatExit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

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
import org.omg.CosTrading.Property;
import org.omg.CosTrading.PropertyTypeMismatch;
import org.omg.CosTrading.Proxy;
import org.omg.CosTrading.ReadonlyDynamicProperty;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.RegisterPOA;
import org.omg.CosTrading.UnknownOfferId;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTrading.RegisterPackage.IllegalTraderName;
import org.omg.CosTrading.RegisterPackage.InvalidObjectRef;
import org.omg.CosTrading.RegisterPackage.MandatoryProperty;
import org.omg.CosTrading.RegisterPackage.NoMatchingOffers;
import org.omg.CosTrading.RegisterPackage.OfferInfo;
import org.omg.CosTrading.RegisterPackage.ProxyOfferId;
import org.omg.CosTrading.RegisterPackage.ReadonlyProperty;
import org.omg.CosTrading.RegisterPackage.RegisterNotSupported;
import org.omg.CosTrading.RegisterPackage.UnknownPropertyName;
import org.omg.CosTrading.RegisterPackage.UnknownTraderName;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropertyMode;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.TypeStruct;
import org.omg.PortableServer.POA;

import com.cboe.EventService.Transport.EventProfileImpl;
import com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl;
import com.cboe.ORBInfra.IOPImpl.GIOPProfileImpl;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.SDIOPTransport.SDIOPProfileImpl;
import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.directoryService.persist.TraderOffer;
import com.cboe.directoryService.persist.TraderServiceType;
import com.cboe.infrastructureUtility.SQLExceptionHelper;
import com.objectwave.persist.QueryException;
/**
*  This is the implementation class that provides the services to
*  describe, export, and withdraw offers.
*
* @author             Judd Herman
*/
public class RegisterImpl
extends RegisterPOA
{
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = RegisterImpl.class.getSimpleName();
	
	/** ServiceTypeRepository interface reference */
	private ServiceTypeRepository serviceTypeRepository;

	/** Lookup interface reference */
	private Lookup lookup;

	/** Admin interface reference */
	private Admin admin;

	/** Link interface reference */
	private Link link;

	/** properties retrieved from file or system management */
	private Properties traderProperties;

	/** ORB reference */
	private static ORB orb = com.cboe.ORBInfra.ORB.Orb.init();

	/** reference to the persistent poa used under the rootPOA */
	private POA poa;

	/** Used to give standard names to some important logging */
	private static ResourceBundle rb = null;

	/** reference to DB Utilities */
	private TraderDBUtil dbUtil;

	/** set to true if using digested IOR format form Offer IDs */
	private boolean digestIOR;

	/** set to true if strict adherence to the spec is required.
	    if false, duplicate properties are allowed (default behavior) */
	private static boolean STRICT_SPEC = (System.getProperty("STRICT_SPEC") != null);

	/**
	* Constructor
	*/
	public RegisterImpl() {
		dbUtil = TraderDBUtil.getInstance();
	}
	
	/**
	* Constructor
	* @param props an instance of the properties for this trader
	* @param poa our POA reference
	*/
	public RegisterImpl(Properties props, POA poa_p) {
		dbUtil = TraderDBUtil.getInstance();
		initialize(props);
		poa = poa_p;
	}

	/**
	* Accessor for the POA reference
	* @return our POA reference
	*/
	public POA _default_POA() {
		return poa;
	}

	/**
	* Initialize the variables
	*/
	private void initialize(Properties props) {
		if (props == null) {
			throw new NullPointerException();
		}
		final String METHOD_ID = "initialize";
		rb = TraderServer.initializeLoggingRb();
		traderProperties = props;
		digestIOR = traderProperties.getProperty("Trader.exportDigestedIORs","true").equalsIgnoreCase("true");
		if (digestIOR) {
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "digested"));
		}
		else {
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "not digested"));
		}
		Logger.sysNotify("STRICT_SPEC property set to " + STRICT_SPEC + ".");
	}

	/**
	* The export operation allows an advertiser to advertise a service through the trader.
	* @param reference the object reference of the advertised service
	* @param type the service type name
	* @param properties optional parameter containing a Property array that can be used for subsequent query purposes.
	* @return String offer ID composed of the IOR and the Service
	* @exception org.omg.CosTrading.InvalidObjectRef object_to_string on reference failed
	* @exception org.omg.CosTrading.IllegalServiceType a schema violation is encountered
	* @exception org.omg.CosTrading.UnknownServiceType the service type is not found
	* @exception org.omg.CosTrading.IllegalPropertyName the attribute is in use
	* @exception org.omg.CosTrading.PropertyTypeMismatch the value supplied with the property does not match its type
	* @exception org.omg.CosTrading.ReadonlyDynamicProperty when attempting to assign a value to a readonly property
	* @exception org.omg.CosTrading.MissingMandatoryProperty when a mandatory property is not supplied
	* @exception org.omg.CosTrading.DuplicatePropertyName when the property name is used more than once in the properties param 
	*/
	public String export( org.omg.CORBA.Object reference, 
						     String type, 
						     Property[] properties)
	throws InvalidObjectRef, IllegalServiceType, UnknownServiceType, 
			 IllegalPropertyName, PropertyTypeMismatch, ReadonlyDynamicProperty, 
			 MissingMandatoryProperty, DuplicatePropertyName 
	{
		final String METHOD_ID = "export";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "type:%s properties:%s", type, getPropertyNames(properties)));
		
		String offerId = calculateOfferId(reference, type);
		String offerInfo = "";

		TraderServiceType serviceType = getServiceTypeByName(type);
		try {
			validateProperties(properties, serviceType);
		}
		// catch and release after logging
		catch (DuplicatePropertyName dpn) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), dpn);
			throw dpn;
		}
		catch (IllegalPropertyName ipn) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), ipn);
			throw ipn;
		}
		catch (PropertyTypeMismatch ptm) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), ptm);
			throw ptm;
		}
		catch (MissingMandatoryProperty mmp) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), mmp);
			throw mmp;
		}

		try {
			offerInfo = makeInformation(reference);
			dbUtil.exportOffer(offerId, offerInfo, reference, type, properties);
		}
		catch (QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "type:%s properties:%s offerid%s", type, getPropertyNames(properties), offerId), qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}
		
		
		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID, "type:%s properties:%s offerid%s", type, getPropertyNames(properties), offerId));
		
		return offerId;
	}
	
	private String calculateOfferId(org.omg.CORBA.Object reference, String type) 
	throws InvalidObjectRef
	{
		final String METHOD_ID = "calculateOfferId";
		String iorValue = null;
		String offerId = null;
		try {
			iorValue = orb.object_to_string(reference);
			if ( digestIOR ) {
				IORImpl ior = new IORImpl();
				ior.destringify(iorValue);
				ior.setExtraDigestData(type);
				offerId = ior.getStringDigest();
			}
			else {
				offerId = iorValue;
			}
		}
		catch(Exception ex) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), ex);
			throw new InvalidObjectRef();
		}	
		return offerId;
	}

	private TraderServiceType getServiceTypeByName(String typeName) 
	throws UnknownServiceType {
		final String METHOD_ID = "getServiceTypeByName";
		TraderServiceType serviceType = null;
		try {
			serviceType = dbUtil.getServiceTypeByName(typeName);
		}
		catch(QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		if ( serviceType == null || serviceType.isMasked() ) { // if it's masked, it's not available for export
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "UnknownServiceType:%s", typeName));
			throw new UnknownServiceType(typeName);
		}	
		return serviceType;
	}
	
	private void validateProperties(Property[] properties, TraderServiceType serviceType) 
	throws DuplicatePropertyName, IllegalPropertyName,PropertyTypeMismatch,MissingMandatoryProperty
	{
		TypeStruct aStruct = serviceType.toStruct();
		// check property syntax and type definition
		ArrayList<String> propList = new ArrayList<String>();
		for (int i=0; i<properties.length; i++)
		{
			validateProperty(properties[i], propList, aStruct);
			propList.add(properties[i].name.toLowerCase());
		}		
		checkMandatoryProperty(aStruct, propList);		
	}

	private void validateProperty(Property property, ArrayList propList, TypeStruct typeStruct) 
	throws DuplicatePropertyName, IllegalPropertyName,PropertyTypeMismatch
	{
		final String METHOD_ID = "validateProperty";
		checkPropertyNameSyntax( property.name );
		if ( STRICT_SPEC ) {
			// if it's already in the list, its a Dup
			if ( propList.contains(property.name) ) {
				Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Duplicate property:%s", property.name));
				throw new DuplicatePropertyName( property.name );
			}
		}
		checkPropertyDeclaration( property, typeStruct.props );		
	}
	
	private void checkMandatoryProperty(TypeStruct typeStruct, ArrayList propList) 
	throws MissingMandatoryProperty 
	{
		final String METHOD_ID = "checkMandatoryProperty";
		
		// check that all mandatory properties are present
		for ( int i=0; i< typeStruct.props.length; i++ )
		{
			int aMode = typeStruct.props[i].mode.value();
			if ( aMode == PropertyMode._PROP_MANDATORY || aMode == PropertyMode._PROP_MANDATORY_READONLY ) {
				String aName = typeStruct.props[i].name;
				if ( !propList.contains(aName) ) {
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Missing MandatoryProperty:%s", aName));
					throw new MissingMandatoryProperty(typeStruct.if_name, aName);
				}
			}
		}		
	}
	/**
	* Create an Offer Information from IOR information
	* If a TIOP profile is present, the Offer ID is "ORBName_ORBHost_ORBPort_InterfaceName"
	* If an EventChannel profile is present, the Offer ID is "ChannelName_InterfaceName"
	* Otherwise, return the full Type ID from the IOR
	* @param reference the Offer's IOR
	* @return an Offer ID
	*/
	public static String makeInformation(org.omg.CORBA.Object reference)
	{
		String iorValue = orb.object_to_string(reference);
		IORImpl ior = new IORImpl();
		ior.destringify(iorValue);
		String typeID = ior.getTypeId();
		
	
		boolean isSDIOP = false;
		SDIOPProfileImpl sdProfile = null;
		try { 
			sdProfile = (SDIOPProfileImpl) ior .getProfile(SDIOPProfileImpl.tag);
		}catch(ProfileNotPresent pnp) {}
		
		
		boolean isTIOP = false;
		TIOPProfileImpl tProfile = null;
		try {
			tProfile = (TIOPProfileImpl)ior.getProfile( TIOPProfileImpl.tag );
			isTIOP = true;
		}
		catch(ProfileNotPresent pnp) { }

		boolean isIIOP = false;
		IIOPProfileImpl iProfile = null;
		try {
			iProfile = (IIOPProfileImpl)ior.getProfile( new Integer(org.omg.IOP.TAG_INTERNET_IOP.value) );
			isIIOP = true;
		}
		catch(ProfileNotPresent pnp) { }

		boolean isEC = false;
		EventProfileImpl eProfile = null;
		try {
			eProfile = (EventProfileImpl)ior.getProfile( EventProfileImpl.tag );
			isEC = true;
		}
		catch(ProfileNotPresent pnp) { }
		
		
		String retVal = null;
		if ( isSDIOP) {
			StringBuffer aBuffer = new StringBuffer(128);
			aBuffer.append( sdProfile.getHost() );
			aBuffer.append( '_' );
			aBuffer.append( sdProfile.getPort() );
			aBuffer.append( '_' );
			aBuffer.append( typeID );
			retVal = aBuffer.toString();
		}else if ( isTIOP ) {
			StringBuffer aBuffer = new StringBuffer(128);
			aBuffer.append( tProfile.getUniqueName() );
			aBuffer.append( '_' );
			aBuffer.append( tProfile.getHost() );
			aBuffer.append( '_' );
			aBuffer.append( tProfile.getPort());
			aBuffer.append( '_' );
			aBuffer.append( typeID );
			retVal = aBuffer.toString();
		}
		else if ( isIIOP ) {
			StringBuffer aBuffer = new StringBuffer(128);
			aBuffer.append( iProfile.getHost() );
			aBuffer.append( '_' );
			aBuffer.append( iProfile.getPort() );
			aBuffer.append( '_' );
			aBuffer.append( typeID );
			retVal = aBuffer.toString();
		}
		else if ( isEC ) {
			StringBuffer aBuffer = new StringBuffer(128);
			aBuffer.append( eProfile.getChannelName() );
			aBuffer.append( '_' );
			aBuffer.append( (eProfile.getInterfaceIds())[0] ); // take the top interface
			retVal = aBuffer.toString();
		}
		else {
			retVal = typeID;
		}

		return retVal;
	}

	/**
	* The withdraw operation removes the service offer from the trader (i.e., after
	* withdraw the offer can no longer be returned as the result of a query).
	* @param String the Offer ID which was originally returned by export.
	* @exception IllegalOfferId if the ID does not obey the rules for offer identifiers
	* @exception UnknownOfferId if there is no offer within the trader with that ID
	*/
	public void withdraw(String id)
	throws IllegalOfferId, UnknownOfferId
	{
		final String METHOD_ID = "withdraw";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "OfferId:%s", id));

		if ( !validOfferId(id) ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Invalid offer id:%s", id));
			throw new IllegalOfferId(id);
		}
		
		try {
			dbUtil.deleteOfferWithID(id); 
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "service offer withdrawn:%s", id));
		}
		catch (QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		} 

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
	}

	/**
	* The withdraw_using_constraint operation withdraws a set of offers from a trader.
	* It uses the constraints in the same way as a query.
	* @param serviceTypeName service type name
	* @param constr optional field that contains a property list that is to be used for query purposes.
	* @exception org.omg.CosTrading.IllegalServiceType schema violation is encountered
	* @exception org.omg.CosTrading.UnknownServiceType service type is not found
	* @exception org.omg.CosTrading.IllegalConstraint the constraint syntax is incorrect
	* @exception org.omg.CosTrading.NoMatchingOffers no offers for the constraint are found
	*/
	public void withdraw_using_constraint(String type, String constr)
	throws IllegalServiceType, UnknownServiceType, IllegalConstraint, NoMatchingOffers {

	
		final String METHOD_ID = "withdraw_using_constraint";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "ServiceTypeName:%s Constraint:%s", type, constr));
		
		TraderOffer[] offers = null;
		try {
			/*
			 * Get all of the offer types, this will also check to make sure the type is known first.
			 */
			offers = dbUtil.getTraderOffersForType(type, constr);
		}
		catch(PropertyTypeMismatch ptm) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID,"ServiceTypeName:%s Constraint:%s", type, constr),ptm);
			throw new org.omg.CORBA.UNKNOWN(ptm.toString());
		}
		catch(QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID,"ServiceTypeName:%s Constraint:%s", type, constr),qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}catch(IllegalConstraint ic) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID),ic);
			throw ic;
		}catch(UnknownServiceType ust) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID,"unknown service type, skipping"),ust);
			throw ust;
		}

		if ( offers != null ) {
			for (int i=0; i<offers.length; i++) {
				try {
					withdraw( offers[i].getOfferID() );
					Logger.sysNotify(format(CLASS_ID, METHOD_ID,"Offer withdrawn for ServiceTypeName:%s Constraint:%s", type, constr));
				}
				catch(IllegalOfferId ioi) {
					Logger.sysWarn(format(CLASS_ID, METHOD_ID,"ServiceTypeName:%s Constraint:%s", type, constr),ioi);
				}
				catch(UnknownOfferId uoi) {
					Logger.sysWarn(format(CLASS_ID, METHOD_ID,"ServiceTypeName:%s Constraint:%s", type, constr),uoi);
				}
			}
		}
		else {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID,"No matching offers for: ServiceTypeName:%s Constraint:%s", type, constr));
			throw new NoMatchingOffers();
		}

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
	}

	/**
	* The describe operation returns the information about an offered service that is held by
	* the trader. It comprises the "reference" of the offered service, the "type" of the service
	* offer, and the "properties" that describe this offer of service.
	* @param String the Offer ID which was originally returned by export.
	* @return OfferInfo the offer information as described above
	* @exception IllegalOfferId if the ID does not obey the rules for offer identifiers
	* @exception UnknownOfferId if there is no offer within the trader with that ID
	* @exception ProxyOfferId if ID identifies a proxy offer rather than an ordinary offer
	*/
	public OfferInfo describe(String id)
		throws IllegalOfferId, UnknownOfferId {

		final String METHOD_ID = "describe";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "OfferID:%s ", id));
		
		if ( !validOfferId(id) ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Ivalid offer id %s ", id));
			throw new IllegalOfferId(id);
		}

		OfferInfo retVal = new OfferInfo();
			
		try {
			retVal = dbUtil.getOfferWithID(id); // getOfferWithID will throw UnknownOfferId if the offer isn't there
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "id %s ", id));
		}
		catch(PropertyTypeMismatch ptm) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), ptm);
			throw new org.omg.CORBA.UNKNOWN(ptm.toString());
		}
		catch(QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

		return retVal;
	}

	/**
	* The "modify" operation is used to alter the description of a trader service offer.
	* The operation can add new non-mandatory properties, change the values of existing
	* non-read only properties, or delete existing properties (except mandatory and read
	* only properties). The object reference and the service type of the offer can not be
	* changed by the "modify" operation. To change the object reference of service type,
	* the offer must be withdrawn and then re-exported to the trader.
	* The "modify" operation either succeeds completely or it fails.
	*
	* @param id the trader offer that is to be modified.
	* @param del_list identifies properties that are to be removed from the service offer.
	* @param modify_list Gives the names and values of properties to be changed.
	* If the property is not in the offer, then the modify operation adds it.
	* @exception org.omg.CosTrading.IllegalOfferId - schema violation is encountered
	* @exception org.omg.CosTrading.UnknownOfferId - service type is not found    
	* @exception org.omg.CosTrading.IllegalPropertyName - the attribute is in use
	* @exception org.omg.CosTrading.UnknownPropertyName - the service type is not found  
	* @exception org.omg.CosTrading.PropertyTypeMismatch - the value supplied with the property does not match its type
	* @exception org.omg.CosTrading.MandatoryProperty - mandatory property is in the del_list 
	* @exception org.omg.CosTrading.ReadonlyProperty - when an illegal action is taken on a readonly property
	* @exception org.omg.CosTrading.DuplicatePropertyName - the same property name is used more than once in the properties parm 
	*/
	public void modify(String id, String[] del_list, Property[] modify_list) 
	throws IllegalOfferId, 
		  UnknownOfferId, 
		  IllegalPropertyName, 
		  UnknownPropertyName, 
		  PropertyTypeMismatch, 
		  MandatoryProperty, 
		  ReadonlyProperty, 
		  DuplicatePropertyName {
		final String METHOD_ID = "modify";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "ServiceTypeName:%s,del_list.length:%s, modify_list.length:%s", id,  del_list.length, modify_list.length));

		boolean delNeeded = ( del_list.length > 0 );
		boolean modNeeded = ( modify_list.length > 0 );

		if ( !delNeeded ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "del required, skipping"));
			return;
		}
		if ( !modNeeded ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "mod required, skipping"));
			return;
		}

		OfferInfo offerInfo = describe(id);
		Property[] oldProps = offerInfo.properties;

		TraderServiceType aType = null;
		try {
			aType = dbUtil.getServiceTypeForOffer(id);
		}
		catch(QueryException qe) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), qe);
			shutdownIfFatal(qe);
			throw new org.omg.CORBA.UNKNOWN(qe.toString());
		}

		TypeStruct aStruct = null;
		String typeName = null;
		if ( aType != null ) {
			aStruct = aType.toStruct();
			aType.getName();
		}
		else {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Unknown offer id: %s, skipping", id));
			throw new UnknownOfferId();
		}

		// create a Map of existing properties for delete loop (below)
		HashMap oldPropMap = new HashMap();
		if (oldProps.length > 0) {
			for (int i=0; i<oldProps.length; i++) {
				oldPropMap.put(oldProps[i].name, oldProps[i]);
			}
		}

		// create a Map of modified properties for merge loop (below)
		HashMap modMap = new HashMap();
		if ( modNeeded ) {
			for (int i=0; i<modify_list.length; i++) {
				modMap.put(modify_list[i].name, modify_list[i]);
			}
		}

		if ( delNeeded ) {
			// check that no mandatory properties are in delete_list
			ArrayList delList = (ArrayList)Arrays.asList(del_list);
			for ( int i=0; i<aStruct.props.length; i++ )
			{
				int aMode = aStruct.props[i].mode.value();
				if ( aMode == PropertyMode._PROP_MANDATORY || aMode == PropertyMode._PROP_MANDATORY_READONLY ) {
					String aName = aStruct.props[i].name;
					if ( delList.contains(aName) ) {
						Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Mandory property (%s,%s) still in delete list, skipping", typeName, aName));
						throw new MandatoryProperty(typeName, aName);
					}
				}
			}

			// now remove the properties from the old list
			for (int i=0; i<del_list.length; i++) {
				if ( oldPropMap.containsKey(del_list[i]) ) {
					oldPropMap.remove(del_list[i]);
				}
				else {
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Unknown property name, skipping"));
					throw new UnknownPropertyName(del_list[i]);
				}
			}
		}

		if ( modNeeded ) {
			// check modify_list property syntax and type definition
			ArrayList modList = new ArrayList();
			try{
				for (int i = 0; i < modify_list.length; i++)
				{
					checkPropertyNameSyntax(modify_list[i].name);

					// if it's already in the list, its a Dup
					if (modList.contains(modify_list[i].name))
					{
						Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Duplicate property name %s", modify_list[i].name));
						throw new DuplicatePropertyName(modify_list[i].name);
					}

					checkPropertyDeclaration(modify_list[i], aStruct.props);
					modList.add(modify_list[i].name);
				}
			}
			catch (IllegalPropertyName ipn)
			{
				/* catch log and release*/
				Logger.sysWarn(format(CLASS_ID, METHOD_ID),ipn);
				throw ipn;
			}
			catch (PropertyTypeMismatch ptm)
			{
				/* catch log and release*/
				Logger.sysWarn(format(CLASS_ID, METHOD_ID),ptm);
				throw ptm;
			}

			// check that no read-only properties are in the modify_list
			for ( int i=0; i<aStruct.props.length; i++ )
			{
				int aMode = aStruct.props[i].mode.value();
				if ( aMode == PropertyMode._PROP_READONLY || aMode == PropertyMode._PROP_MANDATORY_READONLY ) {
					String aName = aStruct.props[i].name;
					if ( modList.contains(aName) ) {
						Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Readonly property in delete list (%s,%s) still in delete list, skipping", typeName, aName));
						throw new ReadonlyProperty(typeName, aName);
					}
				}
			}
		}

		if ( delNeeded || modNeeded ) {

			// merge the property lists
			// first add the properties left over from the original list
			HashMap finalMap = new HashMap(oldPropMap);

			// now add the properties from the modify list
			Iterator keys = modMap.keySet().iterator();
			while ( keys.hasNext() ) {
				String aKey = (String)keys.next();
				finalMap.put(aKey, (Property)modMap.get(aKey) );
			}

			// pull out all the map entries & store in an array
			// it's possible to have no properties remaining, if del_list removes everything
			Set entries = finalMap.entrySet();
			Property[] finalEntries = new Property[entries.size()];
			if (entries.size() > 0) {
				entries.toArray(finalEntries);
			}

			try {
				dbUtil.modifyOfferWithID(id, finalEntries);
				Logger.sysNotify(format(CLASS_ID, METHOD_ID, "register description modified"));

			}
			catch(QueryException qe) {
				Logger.sysWarn(format(CLASS_ID, METHOD_ID), qe);
				shutdownIfFatal(qe);
				throw new org.omg.CORBA.UNKNOWN(qe.toString());
			}
			catch (UnknownOfferId uoi){
				/* catch log and release*/
				Logger.sysWarn(format(CLASS_ID, METHOD_ID),uoi);
				throw uoi;
			}
		}
		

		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

	}

	/**
	* Checks property name validity.
	* add more restrictions here if necessary
	* @param name a property name
	* @exception IllegalPropertyName if the name is null or has zero length
	*/
	private void checkPropertyNameSyntax(String name)
	throws IllegalPropertyName
	{
		final String METHOD_ID = "checkPropertyNameSyntax";
		if ( name == null || name.length() == 0) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "property name is null/zero length, skipping"));
			throw new IllegalPropertyName(name);
		}
	}

	/**
	* Checks a property against the Service Type declaration
	* @param p a Property object to be checked
	* @param props the Service Type Property struct sequence to check against
	* @exception PropertyTypeMismatch if the property type is not the same as the declared type
	*/
	private void checkPropertyDeclaration(Property aProperty, PropStruct[] props)
	throws PropertyTypeMismatch, IllegalPropertyName
	{
		final String METHOD_ID = "checkPropertyDeclaration";
		PropStruct aStruct = null;
		for (int i=0; i<props.length; i++)
		{
			if ( props[i].name.equalsIgnoreCase(aProperty.name) ) {
				aStruct = props[i];
				break;
			}
		}

		if ( aStruct == null ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Property %s is not defined in specified service type", aProperty.name));
			throw new IllegalPropertyName(aProperty.name);
		}

		if ( !aProperty.value.type().equal( aStruct.value_type ) ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Type of property %s does not match definition", aProperty.name));
			throw new PropertyTypeMismatch( aProperty.name, aProperty );
		}
	}

	/**
	* Validate the incoming IOR 
	* @param id IOR string
	* @return boolean true if valid, otherwise false
	*/
	private boolean validOfferId(String id)
	throws IllegalOfferId
	{
		final String METHOD_ID = "validOfferId";
		if ( (id == null) || id.length() < 40 ) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "IllegalOfferId %s", id));
			throw new IllegalOfferId(id);
		}

		return true;
	}

	/**
	* Create a new tie object for the Lookup interface if it does not already exist
	* @return the Lookup interface
	*/
	public Lookup lookup_if()
	{
		return lookup;
	}
	
	/**
	* Create a new tie object for the Register interface if it does not already exist
	* @return the Register interface
	*/
	public Register register_if()
	{
		return _this();
	}

	/**
	 * return the Link interface
	 * @return Link reference
	 */
	public org.omg.CosTrading.Link link_if() {
		return link;
	}

	/**
	 * set the Link interface
	 * @param aLink a Lookup object
	 */
	public void setLink_if(Link aLink)
	{
		link = aLink;
	}

	/**
	 * return the Proxy Interface (not implemented)
	 */
	public Proxy proxy_if()
	{
		Logger.sysWarn(format(CLASS_ID, "proxy_if", "no implement method"));
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}
	
	/**
	 * return the Register Interface (not implemented)
	 * @exception org.omg.CosTrading.RegisterPackage.RegisterNotSupported always thrown 
	 */
	public Register resolve(String[] name)
	throws IllegalTraderName, UnknownTraderName, RegisterNotSupported
	{
		Logger.sysWarn(format(CLASS_ID, "resolve", "Not supported method"));
		throw new RegisterNotSupported();
	}
	
	/**
	 * Return the object representing the Admin interface
	 * @return the Admin interface
	 */
	public Admin admin_if()
	{
		return admin;
	}
	
	/**
	 * set the Admin interface
	 * @param anAdmin Admin object
	 */
	public void setAdmin_if(Admin anAdmin)
	{
		admin = anAdmin;
	}
	
	/**
	 * Return true if we will support modifyable properties
	 * @return boolean true (implemented)
	 */
	public boolean supports_modifiable_properties()
	{
		return true;
	}
	
	/**
	 * Set the tie object representing the remote repository
	 * @return org.omg.CORBA.Object the ServiceTypeRepository reference
	 */
	public org.omg.CORBA.Object set_type_repos(org.omg.CORBA.Object repository)
	{
		serviceTypeRepository = (ServiceTypeRepository)repository;
		return repository;
	}
	
	/**
	 * Return the tie object representing the remote repository 
	 * it must be narrowed after calling this method
	 * @return org.omg.CORBA.Object the ServiceTypeRepository reference
	 */
	public org.omg.CORBA.Object type_repos()
	{
		return (org.omg.CORBA.Object)serviceTypeRepository;
	}

	/**
	 * Return true if we support dynamic properties.
	 * @return boolean false (not implemented)
	 */
	public boolean supports_dynamic_properties()
	{
		return false;
	}
	
	/**
	 * Return true if we support proxy offers.
	 * @return boolean false (not implemented)
	 */
	public boolean supports_proxy_offers()
	{	    
		return false;
	}
	
	/**
	 * common routine for the SQLException Helper
	 */
	private static void shutdownIfFatal(QueryException qe)
	{
		if (SQLExceptionHelper.RankSQLException((SQLException)qe.getOriginalException()) == SQLExceptionHelper.FATAL_SQL_ERROR) {
			
			Logger.sysAlarm( rb, InfraLoggingRb.DS_TS_FATAL_SQL_EXCEPTION, null, qe );  //keep in case alarms are triggered on this code
			Logger.sysAlarm(format(CLASS_ID, "shutdownIfFatal", "FATAL EXCEPTION! CALLING SYSTEM.EXIT()!"));
			System.exit(1);
		}
	}
	
	private String getPropertyNames(Property[] properties) {
		String str = "";
		if (properties != null ) {
			for (int i = 0; i < properties.length; i++) {
				str = str + properties[i].name + " ";
			}
		}
		return str;
	}
}
