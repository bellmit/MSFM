
//-----------------------------------------------------------------------
// FILE: AddType.java
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

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;


// AddType

public class AddType {
		public static ORB orb = null;
		
 	public static void main(String args[]) {
            if (args.length < 1) {
                printUsage();
            }
            ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
            ServiceTypeRepository stRepos  = exampleUtility.getServiceTypeRepository();

            orb = exampleUtility.getOrb();
			String id = ServiceTypeRepositoryHelper.id();
           			
			//String id = "CHANNEL1";
			System.out.println("Service name :" + id );
			String newProperties [] = {"channelDomain", "channelId", "channelName"};	

			PropStruct[] propst = new PropStruct[3];
			propst[0] = new
			org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct(newProperties[0],orb.create_string_tc(0), PropertyMode.PROP_MANDATORY);
			propst[1] = new
			org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct(newProperties[1],orb.create_string_tc(0), PropertyMode.PROP_MANDATORY);
			propst[2] = new
			org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct(newProperties[2],orb.create_string_tc(0), PropertyMode.PROP_MANDATORY);		  
	
             String[] superTypes = new String[0];
   
			try{ // next line for debugging
		      // POA_ServiceTypeRepository_tie stReposTie = new POA_ServiceTypeRepository_tie(new ServiceTypeRepositoryImpl(traderProperties));
			    stRepos.add_type(id,id,propst,superTypes);
			}catch ( ServiceTypeExists ex) {
			    System.out.println("Service Type Exists Exception during add_type :" + ex.toString());
			    System.out.println("Service type: " + id + " exists");			    
			}catch ( org.omg.CORBA.SystemException ex){
				System.out.println("System Exception during add_type :" + ex.toString());
			}catch ( org.omg.CORBA.UserException ux){
			    System.out.println("User Exception during add_type :" + ux.toString());
			}
			System.out.println(".... AddType done");
			System.exit(0);
        }
    
        
    public static void printUsage() {
	    System.out.println( "Usage: java AddType <properties file> [servicetype properties file]\n" 
			    + "\t example NT - java AddType D:\\topdir\\input.properties \n" 
			    + "\t example UNIX - java AddType D:/topdir/input.properties" );
			    
        System.exit(1);
    }
    
}


//EOF
