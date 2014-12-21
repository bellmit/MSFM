//-----------------------------------------------------------------------
// FILE: ExportOffer.java
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

// Local packages
import com.cboe.directoryService.RegisterImpl;
import com.cboe.directoryService.ServiceTypeRepositoryImpl;
import org.omg.CORBA.*;
import org.omg.CosTrading.*;
import org.omg.CosTrading.RegisterPackage.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


// Java packages
import java.util.*;
import java.io.*;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
//import javax.naming.directory.*;
import com.cboe.directoryService.*;

class ExportOffer {
    
	    public static Register regs= null;
		public static ORB orb = null;
		
		
public static void main(String[] args) {

    ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
    Register regs  = exampleUtility.getRegister();
    ServiceTypeRepository stRepos = exampleUtility.getServiceTypeRepository();
    orb = exampleUtility.getOrb();
    String id = new String(ServiceTypeRepositoryHelper.id());
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
		offerID = regs.export(stRepos, id, properties);
    }catch (org.omg.CORBA.UserException ue) {
		System.out.println("User exception :" + ue );
		System.exit(1);
	}catch (org.omg.CORBA.SystemException se ){
		System.out.println("Some exception: " + se );
		System.exit(1);
	}
	try{
	    FileOutputStream op = new FileOutputStream("Export.offer");
    	for(int o = 0;o<offerID.length();o++){
	        op.write(offerID.charAt(o));
		}
		    op.close();
		}catch(IOException iox){
			System.out.println("exception :"+iox.toString());
			System.exit(1);
	    }
        System.out.println("..... Export_Offer done");
        System.exit(0);
  }

}

