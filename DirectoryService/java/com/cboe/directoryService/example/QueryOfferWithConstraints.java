//-----------------------------------------------------------------------
// FILE: QueryOfferWithConstraints.java
//
// PACKAGE: com.cboe.directoryService.example
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------


package com.cboe.directoryService.example;  

// Java packages
import java.util.*;
import java.io.*;
import javax.naming.Context;
import javax.naming.directory.*;

// Local packages
import com.cboe.directoryService.*;
import org.omg.CORBA.*;
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


 

class QueryOfferWithConstraints {
    
	    public static Lookup lookup= null;
		public static String trader = null;
		public static String host = null;
		public static String id = null;
		public static ORB orb = null;
		
  public static void main(String args[]) {    
    
    ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
    Lookup lookup  = exampleUtility.getLookup();

    orb = exampleUtility.getOrb();
    
    String props_value[] = new String[1];
	Any any = orb.create_any();
    any.insert_string("test");

	org.omg.CosTrading.Policy policies[] = new org.omg.CosTrading.Policy[1];

	policies[0] = new org.omg.CosTrading.Policy("dummypolicy",any);

	props_value[0] = "default_props";

	org.omg.CosTrading.LookupPackage.SpecifiedProps props = new
		org.omg.CosTrading.LookupPackage.SpecifiedProps();

    Offer[] offer = new Offer[1];
	offer[0] = new Offer((org.omg.CORBA.Object)null, new Property[0]);

	OfferSeqHolder offers = new OfferSeqHolder(offer);

   // OfferIteratorHolder offer_iter = new OfferIteratorHolder(new _tie_OfferIterator(new OfferIteratorImpl()));
    OfferIteratorHolder offer_iter = new OfferIteratorHolder();
	PolicyNameSeqHolder limits = new PolicyNameSeqHolder(new String[0]);

	limits.value = new String[0];

 	String id = null;
 	
 	
 	try{
		props.prop_names(props_value);
	}catch ( org.omg.CORBA.BAD_PARAM pe ) {
		System.out.println("Exception during props :" + pe);
		System.exit(1);
	}
	id = ServiceTypeRepositoryHelper.id();
    //id = "CHANNEL1";
	System.out.println("Service name :" + id );

     try{
	   lookup.query(id, "channelName == myChannel ","dummyPref", policies, props, 0, offers, offer_iter, limits);

    }catch (IllegalConstraint ue) {
		System.out.println(" Illegal Constraint :" + ue );
		System.exit(1);
	
	}catch (org.omg.CORBA.UserException ue) {
		System.out.println("User exception :" + ue );
		System.exit(1);
	}catch (org.omg.CORBA.SystemException se ){
		System.out.println("Some exception: " + se );
		System.exit(1);
	
	}catch (Exception se ){
		System.out.println("Some exception: " + se );
		System.exit(1);
	}
	
	System.out.println("..offers length = " +offers.value.length);
	if (offers.value.length == 0)  {
	    System.out.println("..no offers found with the supplied constraints");
	} else {
	printOffers(offers, 1);
        for(int i =0;i<offers.value.length;i++){
		    for(int p=0; p < offers.value[i].properties.length;p++){
		    System.out.println("...offer id  :"+ offers.value[i].reference.toString());
		    System.out.println("...property  :"+ offers.value[i].properties[p].name);
	    }
	}
  }

    
    System.out.println(".....QueryOfferWithConstraints done");
    System.exit(0);
	}
    private static void printOffers(OfferSeqHolder offers, int groupNum){
		    System.out.println("Printing from Iterator Group:" +groupNum);
            for(int i=0; i < offers.value.length; i++) {
                    System.out.println( offers.value[i]);
            }
        } 
    public static void printUsage() {
	    System.out.println( "Usage: java QueryOfferWithConstraints <properties file> \n" 
			+ "\t example NT - java QueryOfferWithConstraints D:\\topdir\\input.properties \n" 
			+ "\t example UNIX - java QueryOfferWithConstraints D:/topdir/input.properties" );
			    
        System.exit(1);
    }
}

