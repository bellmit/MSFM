//-------------------------------------------------------------------------
//FILE: LinkProxyImpl.java
//
// PACKAGE: com.cboe.directoryService.gcproxy
//
//-----------------------------------------------------------------------
//
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------

package com.cboe.directoryService.gcproxy;


import org.omg.CosTrading.*;
import org.omg.CosTrading.LinkPackage.*;


/**
This is the stubbed implementation class that provides the services
for Linking traders.
*/
public class LinkProxyImpl extends POA_Link {
    
    LinkProxyImpl() {
    }
    /**
       @roseuid 36D1968C0032
     */
    public Lookup lookup_if() {
		Lookup lookRef = null;
		return lookRef;
	}
    
    /**
       @roseuid 36D1968C006F
     */
    public Register register_if() {
        Register registerRef = null;
        return registerRef;
        
    }
    
    /**
       @roseuid 36D1968C00AB
     */
    public Link link_if() {
        Link linkRef = null;
        return linkRef;
        
    }
    
    /**
       @roseuid 36D1968C00F1
     */
    public Proxy proxy_if() {
        Proxy proxyRef = null;
        return proxyRef;
        
    }
    
    /**
       @roseuid 36D1968C0137
     */
    public Admin admin_if() {
	   Admin adminRef = null;
       return adminRef;
        
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
       @roseuid 36D1968C023B
     */
    public org.omg.CORBA.Object type_repos() {
        return null;
        
    }
    
    /**
       @roseuid 36D1968C0277
     */
    public FollowOption max_link_follow_policy() {
        return null;
    }
     /**
       add link method
     */
    public void add_link(String name, Lookup target, FollowOption def_pass_on_follow_rule, FollowOption limiting_follow_rule){
    };

      /**
       modify link method
     */
    public void modify_link(String name, FollowOption def_pass_on_follow_rule, FollowOption limiting_follow_rule) {
        
    }
    
     /**
       describe_link method
     */
    public LinkInfo describe_link(String name) {
        return null;
    }
      /**
        list_links method
     */
    public String[] list_links() {
        return null;
    }
      /**
       remove_link method
     */
    public void remove_link(String name) {
        
    }
}
