// Source file: L:/DirectoryService/java/com/cboe/directoryService/proxy/LookupProxyImpl.java

package com.cboe.directoryService.proxy;

// java packages
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.Admin;
import org.omg.CosTrading.DuplicatePolicyName;
import org.omg.CosTrading.DuplicatePropertyName;
import org.omg.CosTrading.FollowOption;
import org.omg.CosTrading.IllegalConstraint;
import org.omg.CosTrading.IllegalPropertyName;
import org.omg.CosTrading.IllegalServiceType;
import org.omg.CosTrading.Link;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.OfferIteratorHolder;
import org.omg.CosTrading.OfferSeqHolder;
import org.omg.CosTrading.POA_Lookup;
import org.omg.CosTrading.Policy;
import org.omg.CosTrading.PolicyNameSeqHolder;
import org.omg.CosTrading.Proxy;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTrading.LookupPackage.HowManyProps;
import org.omg.CosTrading.LookupPackage.IllegalPolicyName;
import org.omg.CosTrading.LookupPackage.IllegalPreference;
import org.omg.CosTrading.LookupPackage.InvalidPolicyValue;
import org.omg.CosTrading.LookupPackage.PolicyTypeMismatch;
import org.omg.CosTrading.LookupPackage.SpecifiedProps;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.PortableServer.POA;

import com.cboe.common.log.Logger;
 
/**
* This is a proxy servent which in turn calls the actual implementation
*  This is the implementation class that provides the services to lookup
*   offers.  An Instance of this class is returned when clients use the resolve_initial_reference("TradingSErvice");
*   operation against an ORB.  The primary method implemented here is the 
*  query operation
*
*
* @author             Murali Yellepeddy
*/

   
//public class LookupImpl implements _LookupOperations, _TraderComponentsOperations, _SupportAttributesOperations, _ImportAttributesOperations  {
public class LookupProxyImpl extends  POA_Lookup{

	/*
	* Reference to the actual Lookup object
	*/
	private Lookup actual_Lookup;
  
    /**
	*  The ServiceTypeRepository reference
	*/
	private ServiceTypeRepository serviceTypeRepository;
	
	/**
    	*  Holder for properties retrieved from file or system management
	*/
	private Properties traderProperties;
	
	/**
    	*  The reference to the Register interface
	*/
	private Register register;
	
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
    
	
	public LookupProxyImpl() {
	 }

    
    	// Constructor used to get around the problems with using _this
    	public LookupProxyImpl(java.util.Properties props, POA aPOA,Lookup aLookup)
	{
	   initialize(props);
	   poa = aPOA;
	   actual_Lookup = aLookup;	
	}
	
	public POA _default_POA()
    	{
     		return poa;
    	}
 
   /**
    * Initialize variables
    */
    private void initialize(Properties props) {
        
        	traderProperties = props;
		orb = com.cboe.ORBInfra.ORB.Orb.init();
    }

    /**
       The query operation allows an importer to obtain an advertised service through the trader.
       @param type - This is the name of the service type examples are "EventChannel2", "OrderHandlingService4"
       @param constr - This is an optional field that contains a string of the properties that are to be used for query purposes.
       @param pref - The "pref" parameter is used to order the results returned.
       @param policies - Policies are not implemented, this parm has no effect
       @param desiredProps - this parm has no effect
       @param how_many - The "how_many" parameter states the number of matching service offers that will be returned in the "offers" parameter. Any remaining matches are available through the "offer_itr" parameter. If there are fewer than "how_many" offers, then the "offer_ir" parameter's value is set to nil.
       @param offers - The "offers" parameter is used to return the list of offers.
       @param offer_itr -  The "offer_itr" parameter is a reference to an interface where the offers can be obtained.
       @param limits_applied - this parm has no effect.
       @exception org.omg.CosTrading.IllegalServiceType - thrown if a schema violation is encountered
       @exception org.omg.CosTrading.UnknownServiceType - thrown if the service type is not found
       @exception org.omg.CosTrading.IllegalConstraint - thrown if constr is malformed
       @exception org.omg.CosTrading.IllegalPropertyName - thrown if the attribute is in use
     */

  public synchronized void query(String serviceType, String constr, String pref, Policy[] policies, SpecifiedProps desired_props,
		int how_many, OfferSeqHolder offers, OfferIteratorHolder offer_itr, PolicyNameSeqHolder limits_applied)
	throws IllegalServiceType, UnknownServiceType, IllegalConstraint, IllegalPropertyName, IllegalPolicyName,PolicyTypeMismatch,DuplicatePolicyName,DuplicatePropertyName,InvalidPolicyValue,IllegalPreference
	{
        Logger.sysNotify(this.getClass().getName() + " in the query method of LookupImpl of the proxy");
		SpecifiedProps specProps = new SpecifiedProps(HowManyProps.all);
		actual_Lookup.query(serviceType, constr, pref, policies, specProps, how_many, offers, offer_itr, limits_applied);	
	}

    
    /**
       return the Lookup interface
       @return Lookup
     */
    public Lookup lookup_if() {
       return _this();
    }

    /**
       return the Register interface
       @return Register
    */
    public Register register_if() {
		return register;
    }
	/**
       set the Register interface
       @param aRegister - a Register object
    */
    public void setRegister_if(Register aRegister) {
		register = aRegister;
    }
    
    /**
       return the Admin interface
       @return Admin
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
       set the repository variable
       @param aRepos - a ServiceTypeRepository object
     */
    public void type_repos(org.omg.CORBA.Object aRepos) {
        serviceTypeRepository = (ServiceTypeRepository)aRepos;
        
    }

    /**
       Return true if modifyable properties are supported
       @return boolean
    */
    public boolean supports_modifiable_properties() {
        return true;
    }

    /**
       Return the tie object representing the remote repository
       @return org.omg.CORBA.Object
    */
    public org.omg.CORBA.Object type_repos() {
       return serviceTypeRepository;
    }

    /**
       Since we will not support dynamic properties always return false.
       @return boolean
       @roseuid 36C98EC001A5
     */
    public boolean supports_dynamic_properties() {
		return false;
    }

    /**
       Return true if proxy offers are supported.
       @return boolean
       @roseuid 36C98EC001AE
     */
    public boolean supports_proxy_offers() {
		return false;
    }

    
    /**	
        Stub code for later use (if needed) 
    */
    public int def_search_card() {
		System.out.println("def_search_card not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int max_search_card() {
		System.out.println("max_search_card not implemented");
		return 0;

    }

    /**	
        Stub code for later use (if needed) 
    */
    public int def_match_card() {
		System.out.println("def_match_card not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int max_match_card() {
		System.out.println("max_match_card not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int def_return_card() {
        System.out.println("def_return_card not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int max_return_card() {
		System.out.println("max_return_card not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int max_list() {
        System.out.println("max_list not implemented");
		return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public Link link_if() {
        System.out.println("link_if not implemented");
        Link linkRef = null;
        return linkRef;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public Proxy proxy_if() {
        System.out.println("proxy_if not implemented");
        Proxy proxyRef = null;
        return proxyRef;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int def_hop_count() {
        System.out.println("def_hop_count not implemented");
        return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public int max_hop_count() {
        System.out.println("max_hop_count not implemented");
        return 0;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public FollowOption def_follow_policy() {
        System.out.println("def_follow_policy not implemented");
        FollowOption followOption = null;
        return followOption;
    }

    /**	
        Stub code for later use (if needed) 
    */
    public FollowOption max_follow_policy() {
        System.out.println("max_follow_policy not implemented");
	    FollowOption followOption = null;
        return followOption;

    }

}
