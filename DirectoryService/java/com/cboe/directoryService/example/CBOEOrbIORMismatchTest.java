
//-----------------------------------------------------------------------
// FILE: CBOEOrbIORMismatchTest.java
//
// PACKAGE: com.cboe.DirectoryService.example
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
import com.cboe.ORBInfra.ORB.*;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;
import com.cboe.ORBInfra.PortableServer.*;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

/** The purpose of this class is to test the occurance of a problem
that may exist while using the Trader with the Orbix ORB.  Mainly to 
make sure that this does not happen with the CBOE orb.

It was reported that when a Object was queried then
withdrawn using RegisterImpl.withdraw(ior) that the ior would 
have its port information altered.  Which resulted in
the withdraw operation to not find the object and hence not work.


The Result for the CBOE orb was Successful.  i.e, the object
was queried and withdrawn succesfully.

*/

public class CBOEOrbIORMismatchTest{

		public static Lookup lookup = null;
        private static org.omg.CORBA.ORB orb;
        static POA rootPOA;
        static ExampleUtility exampleUtility;
        static Register regs;
        
        public static void main(String args[]) {
            
            // start up
            
            exampleUtility = new ExampleUtility(args[0]); 
            Admin admin  = exampleUtility.getAdmin();
            regs = exampleUtility.getRegister();
            ServiceTypeRepository stRepos = exampleUtility.getServiceTypeRepository();
            Lookup lookup  = exampleUtility.getLookup();
            orb = exampleUtility.getOrb();   
   
            /*****************************************************************
            
                Section 1.
                Create an Implementation object
            
            ******************************************************************/
            // create POA
            rootPOA = createRootPOA();
            
                   
            ServiceTypeRepositoryImpl serviceTypeRepositoryImpl1 = new ServiceTypeRepositoryImpl();

			// Construct the Tie object
			POA_ServiceTypeRepository_tie stTie1 = new POA_ServiceTypeRepository_tie( serviceTypeRepositoryImpl1, rootPOA );
			
            /*****************************************************************
            
                Section 2.
                Connect(which roughly corresponds to an activate_object with the CBOE orb)
            
            ******************************************************************/
            String ior1 = null;
            String ior2 = null;
            try {
                // activate the new object
			    rootPOA.activate_object(stTie1);
			    rootPOA.the_POAManager().activate();
			    
			    // get offers
			    OfferSeqHolder osh = getOffers(ServiceTypeRepositoryHelper.id());
			    Offer[] offers = osh.value;
			    
			    
			   /*****************************************************************
            
                Section 3.
                Withdraw the object using registerImpl .withdraw
            
                ******************************************************************/  
			    if (  offers.length != 0 ) {
			        for(int i=0; i < offers.length; i++) {
			            String withdrawIOR = orb.object_to_string(offers[i].reference);
			             regs.withdraw(withdrawIOR);
			             
			              System.out.println("\n Successful Test \n\n");
			              System.out.println("\n Retrieved IOR from trader query and withdrawn \n\n");
			             
			        }
			       
			    } else {
			        System.out.println("Cannot find any offers to withdraw \n\n"
			                           +" Getting ready to export a new object for later withdrawal");
			        
			        System.out.println("Please run this test again \n");
			        System.out.println("There is now an object to withdraw \n");
			        
			        export(stTie1._this(orb));
			    }
			   
			  
			
			    
	        }catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive aae) {
                System.out.println("ServantAlreadyActive ..... shutting down test");
                System.exit (1);
	        }
	        
		      catch(AdapterInactive ai)
		    {
		        System.out.println("Adapter inactive ..... shutting down test");
			    System.exit(1);
             }
             catch(org.omg.CosTrading.UnknownOfferId uoi)
		    {
		       System.out.println("Some exception ..... shutting down test- exception =" +uoi);
			   
			   System.exit(1);
		    }
            catch(Exception ex)
		    {
		        System.out.println("Some exception ..... shutting down test- exception =" +ex);
			    System.exit(1);
		    }         
    
			 System.out.println(".....CBOEOrbIORMismatchTest done");
             System.exit(0);
		 }
	
		
		
    /**
	    This method creates a POA for this test
	*/
	public static POA createRootPOA () {
	    POA rootPOA = null;
	    try {
	        org.omg.CORBA.Object obj = exampleUtility.getOrb().resolve_initial_references("RootPOA");
	        rootPOA = POAHelper.narrow(obj);
	    
	        if (rootPOA == null) {
	          System.exit(1);
	        }
        
	    } catch (org.omg.CORBA.ORBPackage.InvalidName in) {
		    System.out.println("Adapter inactive ..... shutting down test");
	        System.exit (1);
	    }
	   /* }catch (org.omg.PortableServer.POAPackage.InvalidPolicy ip) {
            System.out.println("Adapter inactive ..... shutting down test");
            System.exit (1);
	    }catch (org.omg.PortableServer.POAPackage.AdapterAlreadyExists aae) {
            System.out.println("Adapter inactive ..... shutting down test");
            System.exit (1);
	    }
	    */
	    return rootPOA;
	}  
	
	
	public static void export (org.omg.CORBA.Object anObject) {
	    String id = ServiceTypeRepositoryHelper.id();
	    //id = "channel1";
	    System.out.println("Service name :" + id );
	    String newProperties [] = {"channelDomain", "channelId", "channelName"};

	    Property properties[] = new Property[newProperties.length];
	    org.omg.CORBA.Any a = orb.create_any();
	    a.insert_string("Domain1"); 
	    properties[0] = new Property(newProperties[0],a);
	    a = orb.create_any();
	    a.insert_string("channel444"); 
	    properties[1] = new Property(newProperties[1],a);
	    a = orb.create_any();
	    a.insert_string("myChannel");  
	    properties[2] = new Property(newProperties[2],a);
    
 
        String offerID = null;

        try{
	        //ServiceTypeRepository stRepos = new _tie_ServiceTypeRepository( new ServiceTypeRepositoryImpl(traderProperties) );
            //Register regs = new _tie_Register(new RegisterImpl(stRepos, traderProperties));
		    regs.export(anObject, id, properties);
        }catch (org.omg.CORBA.UserException ue) {
		    System.out.println("User exception :" + ue );
		    System.exit(1);
	    }catch (org.omg.CORBA.SystemException se ){
		    System.out.println("Some exception: " + se );
		    System.exit(1);
	    }
   
    }
    
    
    
     public static OfferSeqHolder getOffers(String serviceType) {
        
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


            int howMany = 20;
            try{
                    System.out.println(" Lookup obj: " +lookup.toString());
                    System.out.println(" Querying Service type: " + serviceType);
                    // next line for debugging
		            //LookupImpl lookUp = new LookupImpl(traderProperties);
                    lookup.query(serviceType, null,"dummyPref",policies, props, howMany, offers, offer_iter,
						limits);
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
				System.exit(1);
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
				System.exit(1);
			}
			
		
           return offers;
		 }
     
}
//EOF
