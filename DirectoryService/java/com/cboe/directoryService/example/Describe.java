
//-----------------------------------------------------------------------
// FILE: Describe.java
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
import javax.naming.directory.*;

// local packages
import com.cboe.directoryService.TraderUtility;
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;
import org.omg.CosTrading.RegisterPackage.OfferInfo;

// for debugging
import org.omg.CosTradingRepos.*;
import com.cboe.directoryService.RegisterImpl;
import com.cboe.directoryService.ServiceTypeRepositoryImpl;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


// WithDraw_Offer

public class Describe {

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
			    //System.out.println("offer :"+sBuff.toString());
			    // next two lines for debugging
			    //ServiceTypeRepository stRepos = new _tie_ServiceTypeRepository( new ServiceTypeRepositoryImpl(traderProperties) );
		        //RegisterImpl register = new RegisterImpl(stRepos, traderProperties);
			    OfferInfo offerInfo = regs.describe(sBuff.toString()); 
			    System.out.println("Describe Results:");
			    System.out.println("type :" +offerInfo.type);
			    for(int i=0; i < offerInfo.properties.length; i++) {
			        System.out.println("property =" + TraderUtility.createAttributeFromProperty(offerInfo.properties[i]).toString());
                }   
                ip.close();
            }catch (InvalidAttributeValueException iae) {
				System.out.println("JAVAX exception :" + iae );
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}catch(IOException iox){
			    System.out.println("exception :"+iox.toString());
			}
           System.out.println("..... Describe done");
           System.exit(1);
		}


}

//EOF
