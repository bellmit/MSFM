// Source file: /vobs/dte/DirectoryService/java/com/cboe/directoryService/LookupImpl.java

package com.cboe.directoryService;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties; 
import java.util.ResourceBundle;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosTrading.Admin;
import org.omg.CosTrading.DuplicatePolicyName;
import org.omg.CosTrading.DuplicatePropertyName;
import org.omg.CosTrading.FollowOption;
import org.omg.CosTrading.IllegalConstraint;
import org.omg.CosTrading.IllegalPropertyName;
import org.omg.CosTrading.IllegalServiceType;
import org.omg.CosTrading.Link;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.LookupHelper;
import org.omg.CosTrading.LookupPOA;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.OfferIterator;
import org.omg.CosTrading.OfferIteratorHelper;
import org.omg.CosTrading.OfferIteratorHolder;
import org.omg.CosTrading.OfferIteratorPOATie;
import org.omg.CosTrading.OfferSeqHolder;
import org.omg.CosTrading.Policy;
import org.omg.CosTrading.PolicyNameSeqHolder;
import org.omg.CosTrading.PropertyTypeMismatch;
import org.omg.CosTrading.Proxy;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTrading.LinkPackage.IllegalLinkName;
import org.omg.CosTrading.LinkPackage.UnknownLinkName;
import org.omg.CosTrading.LookupPackage.HowManyProps;
import org.omg.CosTrading.LookupPackage.IllegalPolicyName;
import org.omg.CosTrading.LookupPackage.IllegalPreference;
import org.omg.CosTrading.LookupPackage.InvalidPolicyValue;
import org.omg.CosTrading.LookupPackage.PolicyTypeMismatch;
import org.omg.CosTrading.LookupPackage.SpecifiedProps;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.objectwave.persist.QueryException;

import com.cboe.ORBInfra.ORB.AnyImpl;
import com.cboe.ORBInfra.ORB.OrbAux;
import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.persist.TraderDBUtil;
import com.cboe.infrastructureUtility.SQLExceptionHelper;
 
import static com.cboe.directoryService.TraderLogBuilder.*;

/**
* This is the implementation class that provides the services to lookup offers.
* An Instance of this class is returned when clients use the resolve_initial_reference("TradingService");
* operation against an ORB. The primary method implemented here is the query
* operation. The query function provides allows a caller to customize the search behavior
* within by specifying a constraint.  Also, queries can specify how many offers are to be returned
* with the howMany parameter.
*
* There are additional stub methods included in this class in the event trader policies are to
* ever be implemented.
*
* @author             Judd Herman
*/
public class LookupImpl extends LookupPOA {
	
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = LookupImpl.class.getSimpleName();
			
	/** ServiceTypeRepository reference */
	private ServiceTypeRepository serviceTypeRepository;

	/** properties retrieved from file or system management */
	private Properties traderProperties;

	/** Register interface reference */
	private Register register;

	/** Admin interface reference */
	private Admin admin;

	/** Link interface reference */
	private Link link = null;

	/** ORB reference */
	private ORB orb;

	/** POA reference */
	private POA poa;

	/** used to give standardized names to log messages */
	private ResourceBundle rb = null;
	
	/** Trader Database utilities */
	private TraderDBUtil dbUtil;

	/** remote trader's lookup interface */
	Lookup[] linkedTraderLookup = null;

	/** default hop count for linked trader */
	private int defaultHopCount;

	/** maximum hop count for linked trader */
	private int maximumHopCount;

	/** default follow policy for linked trader */
	private org.omg.CosTrading.FollowOption defaultFollowPolicy;

	/** maximum follow policy for linked trader */
	private org.omg.CosTrading.FollowOption maximumFollowPolicy;

	/** number of Policies set while forwarding a Query */
	private int policyCount;

	/*
	* Constructor.
	*/
	public LookupImpl() {
		dbUtil = TraderDBUtil.getInstance();
	}
	
	/*
	* Constructor used to get around the problems with using _this
	*/
	public LookupImpl(Properties props, POA aPOA) {
		dbUtil = TraderDBUtil.getInstance();
		initialize(props);
		poa = aPOA;
	}

	public POA _default_POA() {
		return poa;
	}

	/**
	* Initialize variables
	*/
	private void initialize(Properties props) {
		final String METHOD_ID = "initialize";
		traderProperties = props;
		orb = com.cboe.ORBInfra.ORB.Orb.init();

		rb = TraderServer.initializeLoggingRb();

		// Initialize linked trader's properties
		if (Boolean.getBoolean("Trader.isLinkedTrader"))
		{
		    defaultHopCount = Integer.parseInt(props.getProperty("Trader.LinkedTrader.DefaultHopCount", "1"));
		    Logger.sysNotify(format(CLASS_ID, METHOD_ID, "defaultHopCount %s", defaultHopCount));
		
		    maximumHopCount = Integer.parseInt(props.getProperty("Trader.LinkedTrader.MaxHopCount", "1"));
		    Logger.sysNotify(format(CLASS_ID, METHOD_ID, "maximumHopCount %s", maximumHopCount));
	
		    String defFollowPolicy = props.getProperty("Trader.LinkedTrader.DefaultFollowPolicy", "always");
		    Logger.sysNotify(format(CLASS_ID, METHOD_ID, "defFollowPolicy %s", defFollowPolicy));


		    if (defFollowPolicy.compareTo("local_only") == 0) {
			defaultFollowPolicy = org.omg.CosTrading.FollowOption.local_only;
		    }
		    else if (defFollowPolicy.compareTo("if_no_local") == 0) {
			defaultFollowPolicy = org.omg.CosTrading.FollowOption.if_no_local;
		    }
		    else {
			defaultFollowPolicy = org.omg.CosTrading.FollowOption.always;
		    }
	
		    String maxFollowPolicy = props.getProperty("Trader.LinkedTrader.MaxFollowPolicy", "always");
		    Logger.sysNotify(format(CLASS_ID, METHOD_ID, "maxFollowPolicy %s", maxFollowPolicy));

		    if (maxFollowPolicy.compareTo("local_only") == 0) {
                    	maximumFollowPolicy = org.omg.CosTrading.FollowOption.local_only;
		    }
                    else if (maxFollowPolicy.compareTo("if_no_local") == 0) {
                        maximumFollowPolicy = org.omg.CosTrading.FollowOption.if_no_local;
                    }
                    else {
                        maximumFollowPolicy = org.omg.CosTrading.FollowOption.always;
                    }

		    String s = props.getProperty("Trader.LinkedTrader.PolicyCount", "1");
		    policyCount = Integer.parseInt(s);

		    Logger.sysNotify(format(CLASS_ID, METHOD_ID, "policyCount %s", policyCount));
		}

	}



	/**
	* The query operation allows an importer to obtain an advertised service through the trader.
	* @param type service type name
	* @param constr an optional field that contains a string of the properties that are to be used for query purposes.
	* @param pref used to order the results returned (not supported)
	* @param policies Policies are not supported
	* @param desiredProps this is not supported
	* @param how_many states the number of matching service offers that will be returned in the "offers" parameter.
	* Any remaining matches are available through the "offer_itr" parameter.
	* If there are fewer than "how_many" offers, then the "offer_ir" parameter's value is set to nil.
	* @param offers used to return the list of offers
	* @param offer_itr reference to an interface where the remainder of offers can be obtained.
	* @param limits_applied not supported
	* @exception IllegalServiceType schema violation is encountered
	* @exception UnknownServiceType the service type is not found
	* @exception IllegalConstraint constr is malformed
	* @exception IllegalPropertyName the attribute is in use
	*/
	public void query(String serviceType, 
						 String constr, 
						 String pref, 
						 Policy[] policies, 
						 SpecifiedProps desired_props, 
						 int how_many,
						 OfferSeqHolder offers, 
						 OfferIteratorHolder offer_itr, 
						 PolicyNameSeqHolder limits_applied)
	throws IllegalServiceType, UnknownServiceType, IllegalConstraint, IllegalPropertyName,
	       IllegalPreference, IllegalPolicyName, PolicyTypeMismatch, InvalidPolicyValue,
               IllegalPropertyName, DuplicatePropertyName, DuplicatePolicyName, org.omg.CORBA.COMM_FAILURE
	{
		final String METHOD_ID = "query";
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "Query=%s,%s,%s", serviceType, constr, pref));
		
		int howMany = how_many;
		if (howMany == 0) {
			howMany = Integer.MAX_VALUE;
		}
		
		limits_applied.value = new String[1];
		limits_applied.value[0] = new String();
		
		// this query will throw UnknownServiceType if the type is not found
		Offer[] tempOffers = null;
		try {
			tempOffers = dbUtil.getOffersForType(serviceType, constr);
			int offerLen = 0;
			if ( tempOffers != null ) 
			{
				offerLen = tempOffers.length;
				if (offerLen > howMany) {
					Offer[] part1 = new Offer[howMany];
					System.arraycopy(tempOffers, 0, part1, 0, howMany);
					Offer[] part2 = new Offer[offerLen - howMany];
					System.arraycopy(tempOffers, howMany, part2, 0, (offerLen-howMany));
					tempOffers = part1;

					offer_itr.value = createIterator(part2);
				}

				offers.value = tempOffers;
			}
			else {
				handleLocalNoEntry(serviceType, constr, pref, policies, desired_props, how_many, offers, offer_itr, limits_applied);
			}
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
		catch(UnknownServiceType uste) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Unknown service type trying now local"));
			handleLocalNoType(serviceType, constr, pref, policies, desired_props, how_many, offers, offer_itr, limits_applied);
		}
		catch(IllegalConstraint constraintErr) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), constraintErr);
			throw constraintErr;
		}
		
		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID, "Offers returned %s. Type %s. constraint %s", offers.value.length, serviceType, constr));
	}

	/**
	 * If local has no entry (it means the type is defined), 
	 * 1. if forwardable, forward request to other traders. Catch any exception while forwarding, and 
	 *    empty offers.
	 * 2. if not forwardable, just return empty offer.
	 */
	private void handleLocalNoEntry(String serviceType, 
			 String constr, 
			 String pref, 
			 Policy[] policies, 
			 SpecifiedProps desired_props, 
			 int how_many,
			 OfferSeqHolder offers, 
			 OfferIteratorHolder offer_itr, 
			 PolicyNameSeqHolder limits_applied) 
	{
		final String METHOD_ID = "handleLocalNoEntry";
		if (isForwardableQuery(policies)){
			Logger.sysNotify( "Forwarding request to Linked Trader as we have zero offers for ServiceType: "+ serviceType + " in current Trader");
            try {
            	forwardQuery(serviceType,
                         constr,
                         pref,
                         policies,
                         desired_props,
                         how_many,
                         offers,
                         offer_itr,
                         limits_applied);
            }
            catch(Exception e) { 
            	Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Forward Query failed, swallowing and returning empty offer"),e);
            	offers.value = new Offer[0];
            }	
		}
		else {
			offers.value = new Offer[0];
		}
	}
		
	/**
	 * If the type is locally defined, 
	 * 1. if forwardable, forward request to other traders, if the type is not defined globly, throw
	 *    UnknownServiceType. Catch all other exceptions and just return an empty offer.
	 * 2. if not forwardable, throw an UnknownServiceType;
	 */
	private void handleLocalNoType(String serviceType, 
			 String constr, 
			 String pref, 
			 Policy[] policies, 
			 SpecifiedProps desired_props, 
			 int how_many,
			 OfferSeqHolder offers, 
			 OfferIteratorHolder offer_itr, 
			 PolicyNameSeqHolder limits_applied) 
	throws UnknownServiceType
	{
		final String METHOD_ID = "handleLocalNoType";
		if (isForwardableQuery(policies)){
			Logger.sysNotify( "Forwarding request to Linked Trader as the service type is not defined locally: "+ serviceType);
            try {
            	forwardQuery(serviceType,
                        constr,
                        pref,
                        policies,
                        desired_props,
                        how_many,
                        offers,
                        offer_itr,
                        limits_applied);
           }
           catch(UnknownServiceType e1) {
        	   Logger.sysNotify(format(CLASS_ID, METHOD_ID, "The service type %s is not defined globablly", serviceType));
        	   throw e1;
           }
           catch(Exception e) { 
				Logger.sysWarn(format(CLASS_ID, METHOD_ID), e);
				offers.value = new Offer[0];
           }	
		}
		else {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Unknown service type %s, skipping", serviceType));
			throw new UnknownServiceType(serviceType);
		}
	}
	
	/**
	* return the Lookup interface
	* @return Lookup reference
	*/
	public Lookup lookup_if()
	{
		return _this();
	}

	/**
	* return the Register interface
	* @return Register reference
	*/
	public Register register_if()
	{
		return register;
	}

	/**
	* set the Register interface
	* @param aRegister a Register object
	*/
	public void setRegister_if(Register aRegister)
	{
		register = aRegister;
	}

	/**
	* return the Admin interface
	* @return Admin reference
	*/
	public Admin admin_if()
	{
		return admin;
	}

	/**
	* set the Admin interface
	* @param anAdmin an Admin object
	*/
	public void setAdmin_if(Admin anAdmin)
	{
		admin = anAdmin;
	}

	/**
	* set the Service Type Repository reference
	* @param aRepos a ServiceTypeRepository object
	*/
	public void type_repos(org.omg.CORBA.Object aRepos)
	{
		serviceTypeRepository = (ServiceTypeRepository)aRepos;
	}

	/**
	* Return true if modifyable properties are supported
	* @return boolean true, implemented
	*/
	public boolean supports_modifiable_properties()
	{
		return true;
	}

	/**
	* Return the object representing the remote repository
	* @return org.omg.CORBA.Object for Service Type Repository
	*/
	public org.omg.CORBA.Object type_repos()
	{
		return serviceTypeRepository;
	}

	/**
	* Return true if dynamic properties are supported
	* @return boolean false, not implemented
	* @roseuid 36C98EC001A5
	*/
	public boolean supports_dynamic_properties()
	{
		return false;
	}
	/**
	* Return true if proxy offers are supported
	* @return boolean false, not implemented
	* @roseuid 36C98EC001AE
	*/
	public boolean supports_proxy_offers()
	{
		return false;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int def_search_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int max_search_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int def_match_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int max_match_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int def_return_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int max_return_card() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public int max_list() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	/**	
	* return the Link interface
        * @return Link reference
	*/
	public Link link_if() {
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
	public Proxy proxy_if() {
		throw new org.omg.CORBA.NO_IMPLEMENT();
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
	* Stub code for later use (if needed)
	*/
	public FollowOption def_follow_policy() {
		return defaultFollowPolicy;
	}

	/**	
	* Stub code for later use (if needed)
	*/
	public FollowOption max_follow_policy() {
		return maximumFollowPolicy;
	}

	/**
	* Create a new OfferIterator object if there are more results
	* @param offerSeq offers to be passed to the Iterator
	* @return OfferIterator
	*/
	private  OfferIterator createIterator(Offer[] offerSeq)
	{
		final String METHOD_ID = "createIterator";
		OfferIterator returnImpl = null;
		try {
			OfferIteratorImpl newImpl = new OfferIteratorImpl(offerSeq, traderProperties);
			OfferIteratorPOATie newIter = new OfferIteratorPOATie(newImpl, poa);
			newImpl.setConnectedObject(newIter);
			String str = String.valueOf((new Date()).getTime());
			poa.activate_object_with_id(("TradingService.Lookup"+str).getBytes(), newIter); 
			return OfferIteratorHelper.narrow(poa.servant_to_reference(newIter));
		}

		// These POA exeptions are programming errors, and should not happen in production level.
		catch(WrongPolicy wp) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),wp);
		}
		catch(ServantAlreadyActive saa) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),saa);
		}
		catch(Throwable t) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),t);
		}

		return returnImpl;
	}

	private boolean hasLinks() {
		if (link_if().list_links().length != 0) {
			return true;
		}
		else {
			return false;
		}
	}

	private Lookup[] getLinkedTradersLookupIf() 
	{
		final String METHOD_ID = "getLinkedTradersLookupIf";
		if (linkedTraderLookup == null) {
			// get the link interface for this Trader
               		Link link = link_if();
	       		String[] listOfLinks = link.list_links();
	       		linkedTraderLookup = new Lookup[listOfLinks.length];

			try {
				for (int i=0; i<listOfLinks.length; i++)
				{
					linkedTraderLookup[i] = link.describe_link(listOfLinks[i]).target;
				}
			}
			catch(IllegalLinkName iln) {
				Logger.sysWarn(format(CLASS_ID,METHOD_ID, InfraLoggingRb.DS_TS_LOOKUP_ILLEGALLINKNAME_EXCEPTION), iln);
				return null;
			}
			catch(UnknownLinkName uln) {
				Logger.sysWarn(format(CLASS_ID,METHOD_ID, InfraLoggingRb.DS_TS_LOOKUP_UNKNOWNLINKNAME_EXCEPTION), uln);
				return null;
			}
			catch(org.omg.CORBA.TIMEOUT ex) {
				Logger.sysWarn(format(CLASS_ID,METHOD_ID, InfraLoggingRb.DS_TS_LOOKUP_TIMEOUT_EXCEPTION), ex);
				return null;
			}
			catch(org.omg.CORBA.OBJECT_NOT_EXIST one) {
				Logger.sysWarn(format(CLASS_ID,METHOD_ID, InfraLoggingRb.DS_TS_LOOKUP_OBJECTNOTEXISTS_EXCEPTION), one);
			}
			catch(Exception e) {
				Logger.sysWarn(format(CLASS_ID,METHOD_ID), e);
				return null;
			}
		}
		return linkedTraderLookup;
	}

	private boolean isForwardableQuery(Policy[] policies) 
	{
		if (Boolean.getBoolean("Trader.isLinkedTrader"))
		{
		    if (hasLinks()) {
			int hopCount = 0;
			//get the hop count passed on as part of Query operation
			if (policies.length > 0) {
			   for(int i=0; i<policies.length;i++) {
				if(policies[i].name.compareTo("hop_count") == 0)
				{
					hopCount = policies[i].value.extract_long();
				}
			   }
			}
			else {
				// default 
				hopCount = 1;
			}

			// TODO: add criteria if link follow policy is defined in the Query
			if (hopCount == 0 || max_follow_policy().value() == 0 || def_follow_policy().value() == 0) 
			{
				return false;
			}
		}
		}
		else
		{
			return false;
		}
		return true;
	}

	private Policy[] addPolicies(Policy[] policies) 
	{
		Policy tempPolicies[] = null;
		tempPolicies = new org.omg.CosTrading.Policy[policyCount];

		// only policy added currently is hop count
		// add more as needed 
                if (policies.length > 0) {
                	return policies;
                }
                else {
			// for now setting only hop_count policy
			for (int i=0; i<policyCount; i++)
			{
			   Any localVal = new AnyImpl((com.cboe.ORBInfra.ORB.Orb)orb);
			   localVal.insert_long(max_hop_count());
			   tempPolicies[i] = new Policy("hop_count", localVal);	
			}
			return tempPolicies;
                }
	}

	private org.omg.CORBA.Policy[] setOrbPolicies(String timeout)
	{
		final String METHOD_ID = "setOrbPolicies";
		org.omg.CORBA.Any timeout_as_any = orb.create_any();
		timeout_as_any.insert_long(Integer.parseInt(timeout));

		org.omg.CORBA.Policy[] policy_list = new org.omg.CORBA.Policy[2];
		try
		{
			policy_list[0] = OrbAux.create_policy(com.cboe.ORBInfra.PolicyAdministration.RELATIVE_RT_TIMEOUT_CO_POLICY_TYPE.value, timeout_as_any);
		}
		catch (org.omg.CORBA.PolicyError pe)
		{
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Caught PolicyError while trying to set RELATIVE_RT_TIMEOUT_CO_POLICY_TYPE"), pe);
		}

		org.omg.CORBA.Any anAny = orb.create_any();
		anAny.insert_short(org.omg.Messaging.NO_REBIND.value);
		try
		{
			policy_list[1] = OrbAux.create_policy(org.omg.Messaging.REBIND_POLICY_TYPE.value, anAny);
		}
		catch (org.omg.CORBA.PolicyError pe)
		{
			Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Caught PolicyError while trying to set REBIND_POLICY_TYPE"), pe);
		}

		return policy_list;
	}

	private void forwardQuery(String serviceType, 
					       String constr, 
					       String pref, 
					       Policy[] policies, 
					       SpecifiedProps desired_props, 
					       int how_many,
					       OfferSeqHolder offers, 
					       OfferIteratorHolder offer_itr, 
					       PolicyNameSeqHolder limits_applied)
	throws IllegalServiceType, UnknownServiceType, IllegalConstraint, IllegalPropertyName,
	       IllegalPreference, IllegalPolicyName, PolicyTypeMismatch, InvalidPolicyValue,
	       IllegalPropertyName, DuplicatePropertyName, DuplicatePolicyName 
	{
		final String METHOD_ID = "forwardQuery";
		// add/modify new query policies before passing on the query to linked trader
		Policy[] localPolicies = addPolicies(policies);	

		if ((linkedTraderLookup = getLinkedTradersLookupIf()) != null)
		{
			if (Logger.isLoggable(rb, InfraLoggingRb.DS_TS_LOOKUP_INFO, Logger.DEBUG)) {

				Logger.debug(rb, InfraLoggingRb.DS_TS_LOOKUP_INFO, 
						new java.lang.Object[]{
						CLASS_ID, 
						METHOD_ID, 
						"Count of Trader Connections to this Trader: ", 
						new Integer(linkedTraderLookup.length)}
				);
			}
			for (int i=0; i<linkedTraderLookup.length; i++) 
			{
			
			    // desiredProps is not supported so passing default
			    SpecifiedProps specProps = new SpecifiedProps(HowManyProps.all);

			    try {

			       String s = System.getProperty("Trader.LinkedTrader.RTTimeout", "2000");

			       org.omg.CORBA.Policy[]  policy_list = setOrbPolicies(s);
			       org.omg.CORBA.Object newRef = null;
			       Lookup newLookup = null;
			       try {
				     newRef = OrbAux.set_policy_overides(policy_list, SetOverrideType.SET_OVERRIDE, linkedTraderLookup[i]);
			       }
			       catch(org.omg.CORBA.INV_POLICY inv) {
			    	   Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Failed while trying to set Orb policy overrides with exception"), inv);
			       }

			       if (newRef != null) {
					
				   newLookup = LookupHelper.narrow(newRef);
			           newLookup.query(serviceType,
					         	constr,
					         	pref,
					         	localPolicies,
					         	specProps,
					         	how_many,
					         	offers,
					         	offer_itr,
					         	limits_applied);

						if (Logger.isLoggable(rb, InfraLoggingRb.DS_TS_LOOKUP_INFO, Logger.DEBUG)) {

							Logger.debug(rb, InfraLoggingRb.DS_TS_LOOKUP_INFO, new java.lang.Object[]
							                                                                        {CLASS_ID, METHOD_ID, "Count of offers returned from Linked Trader: ", new Integer(offers.value.length)});
						}

			    // return on finding any offers from any linked traders
			    if (offers.value.length > 0) {
				return;
			    }
			}
			    
			  }
			  catch (org.omg.CORBA.COMM_FAILURE cf)
				{
					// ignore and continue to next Trader
				  Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Failed to Communicate with Connected Trader %s", i));
				}
				catch (org.omg.CORBA.TIMEOUT to)
				{
					// ignore and continue to next Trader
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Timed out connecting to the Trader %s", i));
				}
				catch (org.omg.CORBA.TRANSIENT tr)
				{
					// ignore and continue to next Trader
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Caught TRANSIENT while connecting to the Trader %s", i));
				}
				catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
				{
					// ignore and continue to next Trader
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Caught OBJECT_NOT_EXIST while connecting to the Trader %s", i));
				}
				catch (UnknownServiceType ust)
				{
					Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Unknown service type"),ust);
					// skip until you hit the final linked Trader
					if (i == (linkedTraderLookup.length - 1))
					{
						throw ust;
					}
				}
				
			}
		}
		else
		{
			Logger.sysNotify(format(CLASS_ID,METHOD_ID, "No linked Traders connected to this local Trader to retrieve service type %s", serviceType));
		}
	}
	
	/**
	 * common routine for the SQLException Helper
	 */
	private static void shutdownIfFatal(QueryException qe)
	{
		if (SQLExceptionHelper.RankSQLException((SQLException)qe.getOriginalException()) == SQLExceptionHelper.FATAL_SQL_ERROR) {
			
			Logger.sysAlarm(format(CLASS_ID, "shutdownIfFatal", "FATAL EXCEPTION! CALLING SYSTEM.EXIT()!"));
			System.exit(1);
		}
	}

}
