//-----------------------------------------------------------------------
// FILE: DescribeType.java
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


 

class DescribeType {
    

		public static String trader = null;
		public static String host = null;
		public static String id = null;
		public static Properties traderProperties = null;
		public static ORB orb = null;
		
    public static void main(String args[]) {
       
       
        ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
        ServiceTypeRepository stRepos  = exampleUtility.getServiceTypeRepository();

        Property properties[] = null; 
 	    String id = null; 
      
        id = new String(ServiceTypeRepositoryHelper.id());
        id = "CHANNEL1";
      
	
        try { // next  line for debugging
		    //stRepos = new _tie_ServiceTypeRepository( new ServiceTypeRepositoryImpl(traderProperties) );
            TypeStruct struct = stRepos.describe_type((String)id);
            
            System.out.println("if_name = " + struct.if_name.toString());
            for(int i=0; i < struct.props.length; i++) {
                System.out.println("value type = " + struct.props[i].name.toString());
                System.out.println("value type = " + struct.props[i].value_type.toString());
                System.out.println("mode = " + struct.props[i].mode.toString());
            }
        }catch ( IllegalServiceType ex){
		    System.out.println("User Exception during DescribeType :" + ex.toString());
	    }catch ( UnknownServiceType ex){
		    System.out.println("User Exception during DescribeType :" + ex.toString()); 
	    }catch ( org.omg.CORBA.SystemException ex){
	        ex.printStackTrace();
		    System.out.println("System Exception during DescribeType :" + ex.toString());
	    }catch ( org.omg.CORBA.UserException ux){
		    System.out.println("User Exception during DescribeType :" + ux.toString());
	    }
        catch (Exception ex) {
           System.out.println("Exception during DescribeType :" + ex.toString());
        }
        
      System.out.println("Describe Type done..");
      System.exit(1);
      		
    }
        public static void printUsage() {
	        System.out.println( "Usage: java DescribeType <properties file> \n" 
			    + "\t example NT - java DescribeType D:\\topdir\\input.properties \n" 
			    + "\t example UNIX - java DescribeType D:/topdir/input.properties" );
			    
            System.exit(1);
        }
}

