//-----------------------------------------------------------------------
// FILE: AdminImpl.java
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

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import org.omg.CosTrading.Admin;
import org.omg.CosTrading.AdminPOA;
import org.omg.CosTrading.FollowOption;
import org.omg.CosTrading.Link;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.NotImplemented;
import org.omg.CosTrading.OfferIdIterator;
import org.omg.CosTrading.OfferIdIteratorHelper;
import org.omg.CosTrading.OfferIdIteratorHolder;
import org.omg.CosTrading.OfferIdIteratorPOATie;
import org.omg.CosTrading.OfferIdSeqHolder;
import org.omg.CosTrading.Register;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.PortableServer.POA;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.infrastructureUtility.SQLExceptionHelper;
import com.objectwave.persist.QueryException;

import static com.cboe.directoryService.TraderLogBuilder.*;

/**
*   This is the implementation class that provides the services for Administrative functions
*   to the Trader.
*
* @author             Judd Herman
*/
public class AdminImpl extends AdminPOA {
	
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = AdminImpl.class.getSimpleName();
	
	/** Holder for properties retrieved from file or system management */
	private Properties traderProperties;
	
	/** Database utilities */
	private TraderDBUtil dbUtil;

	/** Lookup interface reference */
	private Lookup lookup;

	/** Register interface reference */
	private Register register;

	/** Link interface reference */
	private Link link;

	/** ServiceTypeRepository reference */
	private ServiceTypeRepository serviceTypeRepository;

	/** persistent POA reference used under the rootPOA */
	private POA poa;

	/** used to give common names to log messages */
	private ResourceBundle rb = null;

	/** default hop count for linked trader */
	private int defaultHopCount;

	/** maximum hop count for linked trader */
	private int maximumHopCount;

	/** default follow policy for linked trader */
	private org.omg.CosTrading.FollowOption defaultFollowPolicy;

	/** maximum follow policy for linked trader */
	private org.omg.CosTrading.FollowOption maximumFollowPolicy;


	/**
	* Constructor
	*/
	public AdminImpl()
	{
		dbUtil = TraderDBUtil.getInstance();
	}

	/**
	* Constructor
	* @param props instance of a Properties object
	* @param aPOA POA reference
	*/
	public AdminImpl(Properties props, POA aPOA)
	{
		dbUtil = TraderDBUtil.getInstance();
		initialize(props);
		poa = aPOA;
	}

	/**
	* Accessor for the POA reference
	* @return the POA reference
	*/
	public POA _default_POA() {
		return poa;
	}

	private void initialize(Properties props)
	{
		final String METHOD_ID = "initialize";
		traderProperties = props;
		rb = TraderServer.initializeLoggingRb();

		// Initialize linked trader's properties
		if (Boolean.getBoolean("Trader.isLinkedTrader"))
		{
			defaultHopCount = Integer.parseInt(props.getProperty("Trader.LinkedTrader.DefaultHopCount", "1"));
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "Using defaultHopCount %s", defaultHopCount));

			maximumHopCount = Integer.parseInt(props.getProperty("Trader.LinkedTrader.MaxHopCount", "1"));
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "Using maximumHopCount %s", maximumHopCount));
			
			String defFollowPolicy = props.getProperty("Trader.LinkedTrader.DefaultFollowPolicy", "always");
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "Using defaultFollowPolicy %s", defFollowPolicy));

			if (defFollowPolicy.compareTo("local_only") == 0)
			{
				defaultFollowPolicy = org.omg.CosTrading.FollowOption.local_only;
			}
			else if (defFollowPolicy.compareTo("if_no_local") == 0)
			{
				defaultFollowPolicy = org.omg.CosTrading.FollowOption.if_no_local;
			}
			else
			{
				defaultFollowPolicy = org.omg.CosTrading.FollowOption.always;
			}

			String maxFollowPolicy = props.getProperty("Trader.LinkedTrader.MaxFollowPolicy", "always");
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "Using maxFollowPolicy %s", maxFollowPolicy));

			if (maxFollowPolicy.compareTo("local_only") == 0)
			{
				maximumFollowPolicy = org.omg.CosTrading.FollowOption.local_only;
			}
			else if (maxFollowPolicy.compareTo("if_no_local") == 0)
			{
				maximumFollowPolicy = org.omg.CosTrading.FollowOption.if_no_local;
			}
			else
			{
				maximumFollowPolicy = org.omg.CosTrading.FollowOption.always;
			}
		}
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_def_search_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_max_search_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_def_match_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_max_match_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_def_return_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_max_return_card(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_max_list(int value) {
		return 0;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public boolean set_supports_dynamic_properties(boolean value) {
		return false;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public boolean set_supports_modifiable_properties(boolean value) {
		return false;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public boolean set_supports_proxy_offers(boolean value) {
		return false;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_def_hop_count(int value) {
		defaultHopCount = value;
		return defaultHopCount;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int set_max_hop_count(int value) {
		maximumHopCount = value;
		return maximumHopCount;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption set_def_follow_policy(FollowOption opt) {
		defaultFollowPolicy = opt;
		return defaultFollowPolicy;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption set_max_follow_policy(FollowOption opt) {
		maximumFollowPolicy = opt;
		return maximumFollowPolicy;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption set_max_link_follow_policy(FollowOption opt) {
		return null;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption max_link_follow_policy() {
		return null;
	}
    
	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption max_follow_policy() {
		return maximumFollowPolicy;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption def_follow_policy() {
		return defaultFollowPolicy;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int def_hop_count() {
		return defaultHopCount;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int max_hop_count() {
		return maximumHopCount;
	}

	/**
	* Set the object representing the remote repository
	* @param repository Repository reference
	* @return Service Type Repository reference
	*/
	public void setType_repos(org.omg.CORBA.Object repository)
	{
		serviceTypeRepository = (ServiceTypeRepository)repository;
	}
	
	/**
	* Set the object representing the remote repository
	* @param repository Repository reference
	* @return Service Type Repository reference
	*/
	public org.omg.CORBA.Object type_repos(org.omg.CORBA.Object repository)
	{
		serviceTypeRepository = (ServiceTypeRepository)repository;
		return repository;
	}

	/**
	* Set the object representing the remote repository
	* @param repository Repository reference
	* @return Service Type Repository reference
	*/
	public org.omg.CORBA.Object set_type_repos(org.omg.CORBA.Object repository) {
		serviceTypeRepository = (ServiceTypeRepository)repository;
		return repository;
	}

	/**
	* Return the object representing the remote repository
	* @return Service Type Repository reference
	*/
	public org.omg.CORBA.Object type_repos() {
		return serviceTypeRepository;
	}

	/**
	* The list_offers operation is an administrative function for listing out any number
	* of offer entries. This function only returns the offer id value as a String.
	* @param how_many the selected number of offers to place in the ids variable (the OfferIdSeqHolder)
	* @param ids Holder of a list of String offers
	* @param id_itr Holder of an OfferIdIterator (a remote OfferIdIteratorImpl object)
	*/
	public void list_offers(int how_many, OfferIdSeqHolder ids, OfferIdIteratorHolder id_itr)
		throws NotImplemented {
		final String METHOD_ID = "list_offers";
		int howMany = how_many;
		if (howMany == 0) { // return 10 offers as a default if how_many is 0
			howMany = 10;
		}

		String[] allOffers = null;
		try {
			allOffers = dbUtil.getAllOfferIDs();
		}
		catch(QueryException qe) {
			
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),qe);
			
			if (SQLExceptionHelper.RankSQLException((SQLException) qe.getOriginalException()) == SQLExceptionHelper.FATAL_SQL_ERROR){
				Logger.sysAlarm(rb, InfraLoggingRb.DS_TS_FATAL_SQL_EXCEPTION, new java.lang.Object[] {CLASS_ID, METHOD_ID}, qe);
				System.exit(1);
			}
		}

		String[] idStrings = null;
		OfferIdIterator offerIter = null;
		if ( allOffers != null ) {
			int offerLen = allOffers.length;
			int returnLen = Math.min(howMany, offerLen);
			idStrings = new String[returnLen];
			System.arraycopy(allOffers, 0, idStrings, 0, returnLen);

			if (offerLen > howMany) {
				id_itr.value = createIterator(allOffers, howMany);
			}
		}

		ids.value = idStrings;
		id_itr.value = offerIter;
	}

	/**
	* Create a new OfferIdIterator object if there are more results from list_offers
	* @param offerIDs offer name list
	* @param count "next to be used" index into offerIDs
	* @return new OfferIdIterator
	*/
	private OfferIdIterator createIterator(String[] offerIDs, int count) {
		
		
		OfferIdIterator retVal = null;
		try {
			OfferIdIteratorImpl newImpl = new OfferIdIteratorImpl(offerIDs, count);
			OfferIdIteratorPOATie newIter = new OfferIdIteratorPOATie(newImpl, poa);
			newImpl.setConnectedObject(newIter);
			String str = String.valueOf((new Date()).getTime());
			poa.activate_object_with_id(("TradingService.Admin" + str).getBytes(), newIter);                
			retVal = OfferIdIteratorHelper.narrow(poa.servant_to_reference(newIter));
		}
		catch(Exception e) {
			Logger.sysAlarm(format(CLASS_ID, "createIterator"),e);
		}


		return retVal;
	}

	/**
	* Accessor for the Lookup interface
	* @return Lookup interface
	*/
	public Lookup lookup_if() {
		return lookup;
	}

	/**
	* Mutator for the Lookup interface
	* @param aLookup a Lookup object
	*/
	public void setLookup_if(Lookup aLookup) {
		lookup = aLookup;
	}

	/**
	* Accessor for the Register interface
	* @return Register interface
	* @roseuid 36C98EC00190
	*/
	public Register register_if() {
		return register;
	}

	/**
	* Mutator for the Register interface
	* @param aRegister a Register object
	*/
	public void setRegister_if(Register aRegister) {
		register = aRegister;
	}

	/**
	* Accessor for the Admin interface
	* @return Admin interface
	*/
	public Admin admin_if() {
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
	* Stub code for later use (if needed) 
	*/
	public org.omg.CosTrading.Proxy proxy_if() {
		return null;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public boolean supports_modifiable_properties() {
		return false;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public boolean supports_dynamic_properties() {
		return false;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public boolean supports_proxy_offers() {
		return false;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int def_search_card() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int max_search_card() {

		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int def_match_card() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int max_match_card() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int def_return_card() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int max_return_card() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public int max_list() {
		return 0;
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public byte[] set_request_id_stem(byte[] aByte) {
		return new byte[0];
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public byte[] request_id_stem() {
		return new byte[0];
	}

	/**	
	* Stub code for later use (if needed) 
	*/
	public void list_proxies(int how_many, org.omg.CosTrading.OfferIdSeqHolder ids, org.omg.CosTrading.OfferIdIteratorHolder id_itr)
	throws org.omg.CosTrading.NotImplemented
	{ }
}
