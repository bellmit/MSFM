//-------------------------------------------------------------------------
//FILE: ProxyImpl.java
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
import org.omg.CosTrading.ProxyPackage.*;

/**
This is the stubbed implementation class that provides the services
for Proxies.
*/
public class ProxyImpl extends POA_Proxy {
    
    ProxyImpl() {
    }
    /**
       @roseuid 36D7263503C8
     */
    public Lookup lookup_if() {
        Lookup lookupRef = null;
        return lookupRef;
    }
    
    /**
       @roseuid 36D726360095
     */
    public Register register_if() {
        Register registerRef = null;
        return registerRef;
    }
    
    /**
       @roseuid 36D726360149
     */
    public Link link_if() {
        Link linkRef = null;
        return linkRef;
    }
    
    /**
       @roseuid 36D726360207
     */
    public Proxy proxy_if() {
        Proxy proxyRef = null;
        return proxyRef;
    }
    
    /**
       @roseuid 36D7263602B1
     */
    public Admin admin_if() {
        Admin adminRef = null;
        return adminRef;
    }
    
    /**
       Return true if modifyable properties are supported
       @roseuid 36D72636035C
     */
    public boolean supports_modifiable_properties() {
        return false;       
    }
 
       
    /**
       Since we will not support dynamic properties always return false.
       @return boolean
       @roseuid 36D726370032
     */
    public boolean supports_dynamic_properties() {
		return false;
    }
    
    /**
       Return true if proxy offers are supported.
       @return boolean
       @roseuid 36D7263700D2
     */
    public boolean supports_proxy_offers() {
		return false;
    }

    /**
       Return the tie object representing the remote repository
       @roseuid 36D726370186
     */
    public org.omg.CORBA.Object type_repos() {
        return null;
    }
     /**
       Export the proxy for the Lookup
     */
    public String export_proxy(Lookup target, String type, Property[] properties, boolean if_match_all, String recipe, Policy[] policies_to_pass_on) {
        return null;
    }
     /**
       Withdraw the proxy from use
     */ 
     public void withdraw_proxy(String id) {
     }
     /**
       describe the proxy
     */ 
     public ProxyInfo describe_proxy(String id) {
        return null;
     }
    
}

