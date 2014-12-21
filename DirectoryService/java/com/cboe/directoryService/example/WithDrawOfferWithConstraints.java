
//-----------------------------------------------------------------------
// FILE: WithDrawOfferWithConstraints.java
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
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;

// for testing
import org.omg.CosTradingRepos.*;
import com.cboe.directoryService.RegisterImpl;
import com.cboe.directoryService.ServiceTypeRepositoryImpl;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


// WithDraw_Offer

public class WithDrawOfferWithConstraints {

		public static Register regs= null;
		public static ORB orb = null;
		
        public static void main(String args[]) {
            ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
            Register regs  = exampleUtility.getRegister();
       
            String id = ServiceTypeRepositoryHelper.id();
            //id = "CHANNEL1";
	        System.out.println("Service name :" + id );

            try{ // newxt two lines for debugging
           // ServiceTypeRepository stRepos = new _tie_ServiceTypeRepository( new ServiceTypeRepositoryImpl(traderProperties) );
		   // RegisterImpl register = new RegisterImpl(stRepos, traderProperties);
			regs.withdraw_using_constraint(id, "channeldomain==domain1");
              
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}
           System.out.println("..... WithDrawOfferWithConstraints done");
           System.exit(0);
		}


}

//EOF
