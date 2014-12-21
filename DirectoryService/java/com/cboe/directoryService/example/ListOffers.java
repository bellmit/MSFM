
//-----------------------------------------------------------------------
// FILE: ListOffers.java
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
import org.omg.CORBA.*;
import org.omg.CosTrading.*;
import org.omg.CosTrading.RegisterPackage.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;
   
import org.omg.PortableServer.*;
import com.cboe.ORBInfra.PortableServer.*;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;





// WithDraw_Offer

public class ListOffers {

		public static Lookup lookup = null;
		public static Admin admin= null;
		public static String trader = null;
		public static String host = null;
		private static org.omg.CORBA.ORB orb;
        private static org.omg.PortableServer.POA rootPOA;


        public static void main(String args[]) {
            
            ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
            Admin admin  = exampleUtility.getAdmin();
		
            int howMany = 1;
	        System.out.println("Listing Offers, quantity of :" + howMany );

            try{ // next line for debugging
		    //AdminImpl admin = new AdminImpl(traderProperties);
		    OfferIdSeqHolder idHolder = new OfferIdSeqHolder();
			OfferIdIteratorHolder idIteratorHolder = new OfferIdIteratorHolder();
			admin.list_offers(howMany, idHolder, idIteratorHolder);
			System.out.println("maxleft starting" );
			int maxLeft = 0;
			try {
			    maxLeft = idIteratorHolder.value.max_left();
			    System.out.println("maxleft = " + maxLeft);
			}   catch (Throwable t)
			    {System.out.println("Throwable = " +t);
			}
			
            printOffers(idHolder, 1);  

            if (!(idIteratorHolder.value == null)){ 
                if(idIteratorHolder.value.next_n((maxLeft -1), idHolder)) {
                //if(idIteratorHolder.value.next_n( 1, idHolder)) {
                     // print next group of offers
                     printOffers(idHolder, 2);
                    System.out.println("max left in iterator = " + idIteratorHolder.value.max_left());
                    idIteratorHolder.value.destroy();
                }
            } else {
              System.out.println("There are no more offers left ");
            }
            
        }catch (UnknownMaxLeft se ){
		       System.out.println("UnknownMaxLeft exception in max_left operation: " + se );
		
		}catch (org.omg.CosTrading.NotImplemented se ){
				System.out.println("Not implemented exception: " + se );
			
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
				
				admin.admin_if();
			}
           System.out.println("..... ListOffers done");
           System.exit(0);
		}
		
		private static void printOffers(OfferIdSeqHolder idHolder, int groupNum){
		    System.out.println("Printing from Group:" +groupNum);
            for(int i=0; i < idHolder.value.length; i++) {
                    System.out.println( idHolder.value[i]);
            } 
            
        }
             
       
        
		private static ORB getORB(){
		    
	    return orb;
	}
	
		
}

//EOF
