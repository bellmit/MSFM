//-------------------------------------------------------------------------
//FILE: LinkImpl.java
//
// PACKAGE: com.cboe.directoryService
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------

package com.cboe.directoryService;

import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTrading.LinkPackage.*;
import org.omg.PortableServer.*;

import com.cboe.common.log.Logger;

import java.util.HashMap;
import java.util.Vector;
import java.util.Properties;

import static com.cboe.directoryService.TraderLogBuilder.*;

/**
This is the stubbed implementation class that provides the services
for Linking traders.
*/
public class LinkImpl extends POA_Link {

	/** for logging, common name to give all log messages*/
    private static final String CLASS_ID = LinkImpl.class.getSimpleName();
	
    /** Holder for properties retrieved from file or system management */
    private Properties traderProperties;

    /** ServiceTypeRepository reference */
    private ServiceTypeRepository serviceTypeRepository;

    /** Lookup interface reference */
    private Lookup lookup;
    
    /** Register interface reference */
    private Register register;

    /** Admin interface reference */
    private Admin admin;

    /** reference to the persistent poa used under the rootPOA */
    private POA poa;

    /** Collection of Link Names */
    private Vector links;

    /** HashMap of LinkInfo objects */
    private HashMap linkInfoObjs;

    /** maximum link follow policy for a specific link */
    private org.omg.CosTrading.FollowOption maximumLinkFollowPolicy;

    /**
    * Constructor
    */
    LinkImpl() {

    }
   
    /**
    * Constructor
    * @param props instance of a Properties object
    * @param aPOA POA reference
    */
    LinkImpl(Properties props, POA aPOA) {
	
	initialize(props);
        poa = aPOA; 
         
        // During this phase each trader will atmost have one link so a Vector isn't really necessary
	links = new Vector();

        // During this phase each trader will atmost have one link so HashMap isn't really necessary

	linkInfoObjs = new HashMap();
    }

    /**
    * Accessor for the POA reference
    * @return the POA reference
    */
    public POA _default_POA() {
                return poa;
    }


    private void initialize(Properties props) {
        final String METHOD_ID = "initialize";

        traderProperties = props;

        String maxLinkFollowPolicy = props.getProperty("Trader.LinkedTrader.MaxLinkFollowPolicy", "always");
        Logger.sysNotify(format(CLASS_ID, METHOD_ID, "maxLinkFollowPolicy: %s", maxLinkFollowPolicy));

        if (maxLinkFollowPolicy.compareTo("local_only") == 0) {
            maximumLinkFollowPolicy = org.omg.CosTrading.FollowOption.local_only;
        }else if (maxLinkFollowPolicy.compareTo("if_no_local") == 0) {
            maximumLinkFollowPolicy = org.omg.CosTrading.FollowOption.if_no_local;
        }else {
            maximumLinkFollowPolicy = org.omg.CosTrading.FollowOption.always;
        }
    }

    /**
     * return the Lookup interface      
     * @return Lookup reference
     */
    public Lookup lookup_if() {
	return lookup;
    }

    /**
     * set the Lookup interface
     * @param aLookup a Lookup object
    */
    public void setLookup_if(Lookup aLookup)
    {
	lookup = aLookup;
    }
    
    /**
     * return the Register interface
     * @return Register reference
     */
    public Register register_if() {
        return register; 
    }

    /**
     * set the Register interface
     * @param aRegister  a Register object
    */
    public void setRegister_if(Register aRegister)
    {
        register = aRegister;
    }

    /**
     * return the Link interface
     * @return Link reference
     */
    public Link link_if() {
	return _this();
    }
    
    /**
       @roseuid 36D1968C00F1
     */
    public Proxy proxy_if() {
        Proxy proxyRef = null;
        return proxyRef;
        
    }
    
    /**
     * return the Admin interface
     * @return Admin reference 
     */
    public Admin admin_if() {
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
       @roseuid 36D1968C0173
     */
    public boolean supports_modifiable_properties() {
        return false;
    }
    
    /**
       @roseuid 36D1968C01B9
     */
    public boolean supports_dynamic_properties() {
        return false;
        
    }
    
    /**
       @roseuid 36D1968C01FF
     */
    public boolean supports_proxy_offers() {
        return false;
        
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
    * Return the object representing the remote repository
    * @return org.omg.CORBA.Object for Service Type Repository
    */
    public org.omg.CORBA.Object type_repos() 
    {
        return serviceTypeRepository;
    }
    
    /**
       @roseuid 36D1968C0277
     */
    public FollowOption max_link_follow_policy() {
        return maximumLinkFollowPolicy;
    }
     /**
       add link method
     */
    public void add_link(String name, Lookup target, FollowOption def_pass_on_follow_rule, FollowOption limiting_follow_rule) 
    throws IllegalLinkName, 
           DuplicateLinkName, 
           InvalidLookupRef, 
           DefaultFollowTooPermissive, 
           LimitingFollowTooPermissive
    {
    	final String METHOD_ID = "add_link"; 
    	Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "adding link %s", name));
    	
    	try {
            // Ensure the link name is valid.
            checkLinkNameSyntax(name);

            // Ensure this isn't a duplicate link name.
            checkForDuplicateLink(name);
        }
        catch (DuplicateLinkName dln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Duplicate name error, throwing back to client %s", name), dln);
            throw dln;
        }
        catch (IllegalLinkName iln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Illegal link name, throwing back to client %s", name), iln);
            throw iln;
        }

	// Ensure the lookup isn't null
	if (target == null) {
		Logger.sysWarn(format(CLASS_ID,METHOD_ID,"Lookup is invalid, skipping" ));
		throw new InvalidLookupRef();
	}

	// Ensure that the default link behavior isn't stronger than the
        // limiting link behavior.
	if (def_pass_on_follow_rule.value() > limiting_follow_rule.value()) {
		Logger.sysWarn(format(CLASS_ID,METHOD_ID,"Link behavior is stronger than limiting factor, skipping"));
		throw new DefaultFollowTooPermissive(def_pass_on_follow_rule, limiting_follow_rule);
	}
	
	// Ensure that the limiting link behavior for this link doesn't
  	// exceed the maximum allowed for a link.	
	FollowOption follow_policy = max_link_follow_policy();		
	if (limiting_follow_rule.value() < follow_policy.value()) {
		Logger.sysWarn(format(CLASS_ID,METHOD_ID,"limiting link behavior exceeds maximum allowed for a link, skipping"));
		throw new LimitingFollowTooPermissive(limiting_follow_rule, follow_policy);
	}

	// Create a link info structure for this link of the federation.
        LinkInfo link_info = new LinkInfo();
        link_info.target = target;
        link_info.def_pass_on_follow_rule = def_pass_on_follow_rule;
        link_info.limiting_follow_rule = limiting_follow_rule;

        // Insert this link into the collection of links.
        getLinkInfoObjs().put(name, link_info);
        getLinks().add(name);

    	Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
    }

    /**
     *  modify link method
    */
    public void modify_link(String name, FollowOption def_pass_on_follow_rule, FollowOption limiting_follow_rule) 
    throws IllegalLinkName, 
           UnknownLinkName, 
           DefaultFollowTooPermissive, 
           LimitingFollowTooPermissive
    {
    	final String METHOD_ID = "modify_link";
    	Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "Modify Link %s", name));
    	
		try {
            // Ensure the link name is valid.
            checkLinkNameSyntax(name);

            // Ensure this link is present in list
            checkForLinkPresence(name);
        }
        catch (UnknownLinkName uln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unknown link name, throwing back to client %s", name), uln);
            throw uln;
        }
        catch (IllegalLinkName iln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Illegal link name, throwing back to client %s", name), iln);
            throw iln;
        }

        // Ensure that the default link behavior isn't stronger than the
        // limiting link behavior.
        if (def_pass_on_follow_rule.value() > limiting_follow_rule.value()) {
            Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Default link behavior is stronger than limiting link behavior, skipping"));
            throw new DefaultFollowTooPermissive(def_pass_on_follow_rule, limiting_follow_rule);
        }

        // Ensure that the limiting link behavior for this link doesn't
        // exceed the maximum allowed for a link.
        FollowOption follow_policy = max_link_follow_policy();
        if (limiting_follow_rule.value() < follow_policy.value()) {
            Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Limiting link behavior exceeds maximum allowed for this link, skipping"));
            throw new LimitingFollowTooPermissive(limiting_follow_rule, follow_policy);
        }

        // Get the LinkInfo Object
        LinkInfo link_info = (LinkInfo) (getLinkInfoObjs().get(name));

        // Adjust the link settings
        link_info.def_pass_on_follow_rule = def_pass_on_follow_rule;
        link_info.limiting_follow_rule = limiting_follow_rule;

        // Remove the old LinkInfo object and add the updated new copy
        if (getLinkInfoObjs().remove(name) == null) {
            Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Link Info object removal failed for %s", name));
        }

        getLinkInfoObjs().put(name, link_info);
        Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
    }
    
     /**
       describe_link method
     */
    public LinkInfo describe_link(String name)
    throws  IllegalLinkName, UnknownLinkName 
    {
    	final String METHOD_ID = "describe_link";
        Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "Describe %s", name));

        try {
            // Ensure the link name is valid.
            checkLinkNameSyntax(name);

            // Ensure this link is present in list
            checkForLinkPresence(name);
        }
        catch (UnknownLinkName uln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unknown link name, throwing back to client"), uln);
            throw uln;
        }
        catch (IllegalLinkName iln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Illegal link name, throwing back to client"), iln);
            throw iln;
        }

        // return the link information for this link name.
        LinkInfo info = ((LinkInfo) (getLinkInfoObjs().get(name)));

        Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

        return info;
    }
    
    /**
     * list_links method
     */
    public String[] list_links() 
    {
        final String METHOD_ID = "list_links";
        Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "Number of links within the Federation are " + getLinks().size()));

        String[] linkList = new String[getLinks().size()];
        for (int i = 0; i < getLinks().size(); i++) {
            linkList[i] = (String)(getLinks().get(i));
        }
        Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));

        return linkList;
    }

    /**
    *   remove_link method
    */
    public void remove_link(String name) 
    throws  IllegalLinkName, UnknownLinkName
    {
    	final String METHOD_ID = "remove_link";
        Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID, "remove link %s", name));

        try {
            // Ensure the link name is valid.
            checkLinkNameSyntax(name);

            // Ensure this link is present in list
            checkForLinkPresence(name);
        }
        catch (IllegalLinkName iln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Illegal link name, throwing back to client %s", name), iln);
            throw iln;
        }
        catch (UnknownLinkName uln) {
            Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unknown link name, throwing back to client %s", name), uln);
            throw uln;

        }

        // Erase the link state from the Vector and linkinfo Map.
        if (getLinkInfoObjs().remove(name) == null) {
            Logger.sysWarn(format(CLASS_ID, METHOD_ID, "LinkInfo Object Removal failed for %s", name));
        }

        if (!(getLinks().remove(name))) {
            Logger.sysWarn(format(CLASS_ID, METHOD_ID, "Specified element was not found for removal %s", name));
        }

        Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID));
    }
    
   

    /**
     * Checks link name validity.
     * add more restrictions here if necessary
     * @param name a link name
     * @exception IllegalLinkName if the name is null or has zero length
    */
    private void checkLinkNameSyntax(String name)
    throws IllegalLinkName 
    {
         if ( name == null || name.length() == 0) {
        	 Logger.sysWarn(format(CLASS_ID,"checkLinkNameSyntax", "illegal link name"));
             throw new IllegalLinkName(name);
         }
    }

    /**
     * Checks for duplicate link name
     * @param name a link name
     * @exception DuplicateLinkName if the name already exists
    */
    private void checkForDuplicateLink(String name)
    throws DuplicateLinkName
    {
	Vector links = getLinks();
	if (links.contains(name)) {
		Logger.sysWarn(format(CLASS_ID,"checkForDuplicateLink", "duplicate link name"));
		throw new DuplicateLinkName(name);
	}
    }

    /**
     * Checks for presence of a link name
     * @param name a link name
     * @exception UnknownLinkName if the name is not present
    */
    private void  checkForLinkPresence(String name)
    throws UnknownLinkName
    {
        Vector links = getLinks();
        if (!(links.contains(name))) {
    		Logger.sysWarn(format(CLASS_ID,"checkForLinkPresence", "Unknown link name"));
    		throw new UnknownLinkName(name);
        }
    }

    /**
     * @return Vector of all the links 
    */
    private Vector getLinks() 
    {
	return links;
    }

    /**
     * @return HashMap of all the linkInfo objects
    */
    public HashMap getLinkInfoObjs()
    {
	return linkInfoObjs;
    }
}
