//-----------------------------------------------------------------------
// FILE: AdminProxyImpl.java
//
// PACKAGE: com.cboe.directoryService.proxy
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------
package com.cboe.directoryService.proxy;

// local packages
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


// local packages
import org.omg.CORBA.ORB;
import org.omg.CosTrading.*;
import org.omg.CosTrading.RegisterPackage.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.PortableServer.*;
import com.cboe.ORBInfra.PortableServer.*;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.CORBA.ORBPackage.InvalidName;
import com.cboe.loggingService.*;

import com.sun.jaw.reference.common.Debug;



//AdminProxyImpl
/**
* This is a proxy object for the Admin object in the TraderServer
*   This is the implementation class that provides the services for Administrative functions
*   to the Trader.
*
* @author             Murali Yellepeddy
 */
public class AdminProxyImpl extends POA_Admin{

	/*
	* The reference to the actual Admin object
	*/

	private Admin actual_Admin;

    	/**
    	*  Holder for properties retrieved from file or system management
	*/
	private Properties traderProperties;

	/**
    	*  The reference to the Lookup interface
	*/
	private Lookup lookup;

	/**
    	*  The reference to the Register interface
	*/
	private Register register;

	/**
	*  reference to the ORB
	*/
	private ORB orb;
	/**
	*  reference to the persistent poa used under the rootPOA
	*/
	private POA poa;
    
	/**
	*  The ServiceTypeRepository reference
	*/
	private ServiceTypeRepository serviceTypeRepository;

	public AdminProxyImpl() { }
    /**
    
     Constructor

    @param props - An instance of a Properties object
    */
	public AdminProxyImpl(Properties props, POA aPOA, Admin aAdmin)
	{
	   System.out.println("Starting admin impl poa:"+aPOA);
	   initialize(props);
	   poa = aPOA;
	   actual_Admin = aAdmin;	
	   
	}
	
	public POA _default_POA()
    	{
     		return poa;
    	}
	
	private void initialize(Properties props) {
	    
		traderProperties = props;
		orb = com.cboe.ORBInfra.ORB.Orb.init();
		
    }

   
    /**	
        Stub code for later use (if needed) 
    */
    public int set_def_search_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_max_search_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_def_match_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_max_match_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_def_return_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_max_return_card(int value) {
		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
    public int set_max_list(int value) {
		return 0;
	}
    /**	
        Stub code for later use (if needed) 
    */
    public boolean set_supports_dynamic_properties(boolean value) {
        return false;
        
    }
    /**	
        Stub code for later use (if needed) 
    */
    public boolean set_supports_modifiable_properties(boolean value) {
		return false;
	}
	
    /**	
        Stub code for later use (if needed) 
    */
    public boolean set_supports_proxy_offers(boolean value) {
		return false;
	}
	
    /**	
        Stub code for later use (if needed) 
    */
	public int set_def_hop_count(int value) {
		return 0;
	}
	
    /**	
        Stub code for later use (if needed) 
    */
	public int set_max_hop_count(int value) {
		return 0;
	}
	
    /**	
        Stub code for later use (if needed) 
    */
	 public FollowOption set_def_follow_policy(FollowOption opt) {
	    FollowOption followOption = null;
        return followOption;
    }
    
    /**	
        Stub code for later use (if needed) 
    */
	public FollowOption set_max_follow_policy(FollowOption opt) {
	    FollowOption followOption = null;
        return followOption;
    }
    
    /**	
        Stub code for later use (if needed) 
    */
    public FollowOption set_max_link_follow_policy(FollowOption opt) {
	    FollowOption followOption = null;
        return followOption;
    }
       
    /**	
        Stub code for later use (if needed) 
    */
	public FollowOption max_link_follow_policy() {
	    FollowOption followOption = null;
        return followOption;
    }
    
    /**	
        Stub code for later use (if needed) 
    */
    public FollowOption max_follow_policy() {
	    FollowOption followOption = null;
        return followOption;
    }
    /**	
        Stub code for later use (if needed) 
    */
    public FollowOption def_follow_policy() {
	    FollowOption followOption = null;
        return followOption;
    }
    /**	
        Stub code for later use (if needed) 
    */
    public int def_hop_count() {
	    return 0;
    }
    
    /**	
        Stub code for later use (if needed) 
    */
    public int max_hop_count() {
	    return 0;
    }
    
    /**
       set the object representing the remote repository
     */
	public void setType_repos(org.omg.CORBA.Object repository){
		serviceTypeRepository = (ServiceTypeRepository)repository;
	}
	
	 /**
       Set the  tie object representing the remote repository
     */
	public org.omg.CORBA.Object type_repos(org.omg.CORBA.Object repository){
		serviceTypeRepository = (ServiceTypeRepository)repository;
		return repository;
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
     */
    public org.omg.CORBA.Object type_repos() {
        return serviceTypeRepository;
    }
	
    /**
    The list_offers operation is an administrative function for listing out any number
    of offer entries.  This function only returns the offer id value as a String.
    @param how_many - the selected number of offers to place 
    in the ids variable(the OfferIdSeqHolder)
    @param ids - Holder of a list of String offers
    @param id_itr - Holder of an OfferIdIterator ( a remote OfferIdIteratorImpl object)
    */
    public void list_offers(int how_many, OfferIdSeqHolder ids, OfferIdIteratorHolder id_itr)
    throws NotImplemented{
		actual_Admin.list_offers(how_many, ids, id_itr);     
   }
      
    
	/**
    Return the Lookup interface
    */
	public Lookup lookup_if()
	{
		return lookup;
	}
	
	/**
       set the Lookup interface
       @param aLookup - a Lookup object
    */
    public void setLookup_if(Lookup aLookup) {
		lookup = aLookup;
    }
    
    /**
       return the Register interface
       @roseuid 36C98EC00190
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
    */
	public Admin admin_if(){
	    return _this();
	}
	
	/**
    return the Link interface
    */
    public org.omg.CosTrading.Link link_if() {
        org.omg.CosTrading.Link linkRef = null;
        return linkRef;
        
    }
    
    /**	
        Stub code for later use (if needed) 
    */
    public org.omg.CosTrading.Proxy proxy_if() {
        org.omg.CosTrading.Proxy proxyRef = null;
        return proxyRef;
        
    }
    /**	
        Stub code for later use (if needed) 
    */
    
	public boolean supports_modifiable_properties(){

		return false;
	}
	
    /**	
        Stub code for later use (if needed) 
    */
	public boolean supports_dynamic_properties(){

		return false;

	}
    /**	
        Stub code for later use (if needed) 
    */
	public boolean supports_proxy_offers(){

		return false;
	}
    /**	
        Stub code for later use (if needed) 
    */
   	public int def_search_card(){

		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
   public int max_search_card(){

		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
	public int def_match_card(){

		return 0;

	}

    /**	
        Stub code for later use (if needed) 
    */
	public int max_match_card(){

		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
	public int def_return_card(){

		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
	public int max_return_card(){

		return 0;
	}

    /**	
        Stub code for later use (if needed) 
    */
	public int max_list(){

		return 0;
	}
    /**	
        Stub code for later use (if needed) 
    */
	 public byte[] set_request_id_stem(byte[] aByte)
    {
         return new byte[0];        
        
    }
    /**	
        Stub code for later use (if needed) 
    */
	 public byte[] request_id_stem()
    {
         return new byte[0];        
    }
	
    /**	
        Stub code for later use (if needed) 
    */
	public void list_proxies(int how_many,org.omg.CosTrading.OfferIdSeqHolder ids,org.omg.CosTrading.OfferIdIteratorHolder id_itr) 
        throws org.omg.CosTrading.NotImplemented {
    }

	
// DEBUG METHODS
//--------------
 
   /**
    * Displays the debug message.
    */ 
   private static void trace(String message) {
      debug("TraderServerProxy.AdminProxyImpl::" + message);
   }

   /**
    * Displays the debug message.
    */ 
   private static void lntrace(String message) {
      debug("\nTraderServerProxy.AdminProxyImpl::" + message);
   }

   /**
    * Displays the debug message.
    */ 
   private static  void traceln(String message) {
      debug("TraderServerProxy.AdminProxyImpl::" + message + "\n");
   }

  /**
    * Displays the debug message.
    */ 
   private static  void debug(String message) {
      Debug.print(Debug.TRACE_DEBUG, message);
   }
  	
}// EOF
