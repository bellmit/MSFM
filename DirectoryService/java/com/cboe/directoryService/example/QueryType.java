
//-----------------------------------------------------------------------
// FILE: QueryType.java
//
// PACKAGE: com.cboe.DirectoryService
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------
package com.cboe.directoryService.example;

// java packages

import java.util.*;
import java.io.*;

// local packages
import com.cboe.directoryService.*;
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;
   
   
import org.omg.PortableServer.*;
import com.cboe.ORBInfra.PortableServer.*;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;


// QueryType

public class QueryType{

		public static Lookup lookup = null;
        private static org.omg.CORBA.ORB orb;

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

            OfferIteratorHolder offer_iter = new OfferIteratorHolder();
			
			//POA_OfferIterator offer_iter = new OfferIteratorHolder(new POA_OfferIterator_tie(new OfferIteratorImpl()));
			PolicyNameSeqHolder limits = new PolicyNameSeqHolder(new String[0]);

			limits.value = new String[0];

			try{
				props.prop_names(props_value);
			}catch ( org.omg.CORBA.BAD_PARAM pe ) {
				System.out.println("Exception during props :" + pe);
				System.exit(1);
			}

			String id = ServiceTypeRepositoryHelper.id();
			//id = "CHANNEL1";
			//id = "LoggingRepository";
            int howMany = 1;
            try{
                    System.out.println(" Querying Service type: " + id);
                    // next line for debugging
		            //LookupImpl lookUp = new LookupImpl(traderProperties);
                    lookup.query(id, null,"dummyPref",policies, props, howMany, offers, offer_iter,
						limits);
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
				System.exit(1);
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
				System.exit(1);
			}
			
			
	        System.out.println("Listing Types, quantity of :" + howMany );
			
			try {
                
			// print first group of offers
			printOffers(offers, 1);
			if (offer_iter.value != null){
			    int maxLeft = offer_iter.value.max_left();
                System.out.println("max left in iterator = " + maxLeft);       
            
                if(offer_iter.value.next_n(maxLeft, offers)) {
                   
                    System.out.println("max left in iterator = " + offer_iter.value.max_left());
                
                    // destroy the iterator,  It will no longer be usable
                    offer_iter.value.destroy();
                }
                if (offers.value != null) {
                     printOffers(offers, 2);
                }
            }
            
            } catch (UnknownMaxLeft uml) {
                    System.out.println("Exception :" + uml);
            }
			
           System.out.println(".....QueryType done");
           System.exit(0);
		 }
		
        private static void printOffers(OfferSeqHolder offers, int groupNum){
		    System.out.println("Printing from Group:" +groupNum);
            for(int i=0; i < offers.value.length; i++) {
                    System.out.println( offers.value[i]);
            }
        } 
        
        
        private static String getLookupServerString() {
          StringBuffer sBuff = null;
          try{
			    FileInputStream ip = new FileInputStream("D:\\DirectoryService\\classes\\Lookup.ior");
			    sBuff = new StringBuffer(ip.available());
			    int available = ip.available();
			    System.out.println(sBuff.toString());
			    for(int i = 0;i<available;i++){
			        sBuff.append((char)ip.read());
			    }
			    System.out.println("obj :"+sBuff.toString());
			    			    
                ip.close();
			
			//}catch (org.omg.CORBA.UserException ue) {
			//	System.out.println("User exception :" + ue );
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}catch(IOException iox){
			    System.out.println("exception :"+iox.toString());
			}
                return sBuff.toString();
			
        }
}

//EOF
