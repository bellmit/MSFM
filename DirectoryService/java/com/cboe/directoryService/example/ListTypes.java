//-----------------------------------------------------------------------
// FILE: ListTypes.java
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
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


class ListTypes {
    
		public static String trader = null;
		public static String host = null;
		public static String id = null;
		public static Properties traderProperties = null;
		public static ORB orb = null;
		
    public static void main(String[] args) {
 
        ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
        ServiceTypeRepository stRepos  = exampleUtility.getServiceTypeRepository();
        ListOption opt = ListOption.from_int(ListOption._all);
        SpecifiedServiceTypes specifiedServiceTypes = new SpecifiedServiceTypes(opt);   

        try {
            // next line for debugging
		    //POA_ServiceTypeRepository_tie stReposTie = new POA_ServiceTypeRepository_tie(new ServiceTypeRepositoryImpl(traderProperties));
            //String[] newStrings = (stReposTie.list_types(specifiedServiceTypes)); 
            String[] newStrings = (stRepos.list_types(specifiedServiceTypes)); 
            for(int i=0; i < newStrings.length; i++) {
                System.out.println("Listed types returned = " + newStrings[i]);
            }
            System.out.println("List Types done..");
            System.exit(1);
        }
        catch(org.omg.CORBA.SystemException se)
	    {
			se.printStackTrace();
			System.exit(1);
		}
        
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
      
      }
        
       
        public static void printUsage() {
	        System.out.println( "Usage: java ListTypes <properties file> \n" 
			    + "\t example NT - java ListTypes D:\\topdir\\input.properties \n" 
			    + "\t example UNIX - java ListTypes D:/topdir/input.properties" );
			    
            System.exit(1);
        }
}

