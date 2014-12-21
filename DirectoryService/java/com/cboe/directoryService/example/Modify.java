//-----------------------------------------------------------------------
// FILE: Modify.java
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
import org.omg.CosTrading.RegisterPackage.*;

// for testing
import org.omg.CosTradingRepos.*;
import com.cboe.directoryService.RegisterImpl;
import com.cboe.directoryService.ServiceTypeRepositoryImpl;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;



class Modify {
    
		
  public static void main(String args[]) {    
    
 
        String id = null;
        ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
        Register regs  = exampleUtility.getRegister();
        ORB orb =exampleUtility.getOrb();
   
	    String offerId = null;
	    
		try {
		    // next two lines for debugging
			//ServiceTypeRepository stRepos = new POA_ServiceTypeRepository_tie( (ServiceTypeRepository)new ServiceTypeRepositoryImpl(traderProperties) );
		    //RegisterImpl register = new RegisterImpl( traderProperties);
		    // create del_list
            String[] delList = new String[0];          
           // delList[0] = "channelName";
            
            // create mod_list
            Property[] modProperties = new Property[1];
            // modify channelId to have the value 77
            org.omg.CORBA.Any a = orb.create_any();
            a.insert_string("77");
            // will throw MandatoryProperty Exception
            //a.insert_string(null);
            
            modProperties[0] = new Property("channelId",a);
           
             try{
			    FileInputStream ip = new FileInputStream("Export.offer");
			    StringBuffer sBuff = new StringBuffer(ip.available());
			    int available = ip.available();
			    System.out.println(sBuff.toString());
			    for(int i = 0;i<available;i++){
			        sBuff.append((char)ip.read());
			    }
			    System.out.println("offer :"+sBuff.toString());
			    //regs.modify(sBuff.toString(),new String[0], properties);
			    
			    // to test deletes and modifies
			    regs.modify(sBuff.toString(),delList, modProperties);
			    
			    // to test deletes
			    //register.modify(sBuff.toString(),delList, new Property[0]);
                ip.close();
			}catch (org.omg.CORBA.UserException ue) {
				System.out.println("User exception :" + ue );
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}catch(IOException iox){
			    System.out.println("exception :"+iox.toString());
			}
           System.out.println("..... Modify done");
           System.exit(0);
            
     
		} catch (Exception se) {
		    
			System.out.println("The exception is : " + se);
			System.out.println("message : " + se.getMessage());
			
		}

    System.out.println(".....Modify done");
    System.exit(0);
	}

    public static void printUsage() {
	    System.out.println( "Usage: java Modify <properties file> \n" 
			+ "\t example NT - java Modify D:\\topdir\\input.properties \n" 
			+ "\t example UNIX - java Modify D:/topdir/input.properties" );
			    
        System.exit(1);
    }
}

