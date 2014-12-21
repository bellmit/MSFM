
//-----------------------------------------------------------------------
// FILE: WithDrawOffer.java
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


// WithDraw_Offer

public class WithDrawOffer {

		public static Register regs= null;
		public static ORB orb = null;

        public static void main(String args[]) {
            ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
            Register regs  = exampleUtility.getRegister();

  
             
			/*
			    This expects that the Export_Offer example has already registered the
			    offer information and created a file with the offer id
			*/


            try{

			    FileInputStream ip = new FileInputStream("Export.offer");
			    StringBuffer sBuff = new StringBuffer(ip.available());
			    int available = ip.available();
			    System.out.println(sBuff.toString());
			    for(int i = 0;i<available;i++){
			        sBuff.append((char)ip.read());
			    }
			    System.out.println("offer :"+sBuff.toString());
			    regs.withdraw(sBuff.toString());
                ip.close();
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}catch(IOException iox){
			    System.out.println("exception :"+iox.toString());
			}
           System.out.println("..... WithDraw_Offer done");
           System.exit(0);
		}


}

//EOF
