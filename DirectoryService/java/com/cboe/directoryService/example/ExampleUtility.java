
//-----------------------------------------------------------------------
// FILE: ExampleUtility.java
//
// PACKAGE: com.cboe.DirectoryService.example
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------
package com.cboe.directoryService.example;

// java packages

import java.util.*;
import java.io.*;

// Local packages
import com.cboe.directoryService.*;
import org.omg.CORBA.*;
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepository.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;


public class ExampleUtility{
    
    public org.omg.CORBA.ORB orb = null;
	public static Properties traderProperties = null;
	
       
    public ExampleUtility( String propFileName)
	{

      orb = null;
      
      traderProperties = readProperties(propFileName);
        
    }
		 
        public String getLookupServerString() {
          StringBuffer sBuff = null;
          try{
			    FileInputStream ip = new FileInputStream("D:\\DirectoryService\\classes\\Lookup.ior");
			    sBuff = new StringBuffer(ip.available());
			    int available = ip.available();
			    System.out.println(sBuff.toString());
			    for(int i = 0;i<available;i++){
			        sBuff.append((char)ip.read());
			    }
			    System.out.println("obj :"+sBuff.toString());
			    			    
                ip.close();
			
			}catch (org.omg.CORBA.SystemException se ){
				System.out.println("Some exception: " + se );
			}catch(IOException iox){
			    System.out.println("exception :"+iox.toString());
			}
                return sBuff.toString();
			
        }
     public Properties getProperties() {

       return traderProperties;
     }
    		  
    /**
    read the properties 
    from the file name
    **/
    private Properties readProperties(String filename) {

        Properties props = new Properties(System.getProperties());

		try{
    			props.load(new BufferedInputStream(new FileInputStream(filename)));
    		}catch(FileNotFoundException fnf){
    		    System.out.println("unable to locate file :"+fnf.toString());
    		    System.exit(1);
    		}catch(IOException ioe){
    		    System.out.println("unable to locate file :"+ioe.toString());
    		    System.exit(1);
    		}
        return props;
     }
    //System.getProperties().put("ORB.InitRefURL", "file:///D://DirectoryService//classes//Lookup.ior");
     
     
     
     public org.omg.CORBA.Object getInitialReference() {
        org.omg.CORBA.Object obj = null;
        try {
		    obj =  getOrb().resolve_initial_references ("TradingService"); 
		}
		    catch (org.omg.CORBA.ORBPackage.InvalidName in) {
                System.out.println ("Exception  " + in.toString());
                in.printStackTrace();
                System.exit(1);
        }
        return obj;
     }
    
    
    public Lookup getLookup(){
        Lookup lookup = null;
        try {
		    lookup = LookupHelper.narrow(getInitialReference());	
	    }
	    catch ( org.omg.CORBA.SystemException cx ) {
	        System.out.println("Exception during narrow of type :" + cx.toString());
        }catch (Throwable in) {
            System.out.println ("Exception  " + in.toString());
	        System.exit (1);
        }
        return lookup;

      }
    
    public Admin getAdmin(){
        Admin admin = null;
        try {
		    Lookup lookup = LookupHelper.narrow(getInitialReference());	
		    admin = lookup.admin_if();
	    }
	    catch ( org.omg.CORBA.SystemException cx ) {
	        System.out.println("Exception during narrow of type :" + cx.toString());
        }catch (Throwable in) {
            System.out.println ("Exception  " + in.toString());
	        System.exit (1);
        }
        return admin;

      }
 
     public Register getRegister(){
        Register register = null;
        try {
		    Lookup lookup = LookupHelper.narrow(getInitialReference());	
		    register = lookup.register_if();
	    }
	    catch ( org.omg.CORBA.SystemException cx ) {
	        System.out.println("Exception during narrow of type :" + cx.toString());
        }catch (Throwable in) {
            System.out.println ("Exception  " + in.toString());
	        System.exit (1);
        }
        return register;

      }
      
     public ServiceTypeRepository getServiceTypeRepository(){
        ServiceTypeRepository stRepos = null;
        try {
		    Lookup lookup = LookupHelper.narrow(getInitialReference());	
		    stRepos = ServiceTypeRepositoryHelper.narrow(lookup.type_repos());
	    }
	    catch ( org.omg.CORBA.SystemException cx ) {
	        System.out.println("Exception during narrow of type :" + cx.toString());
        }catch (Throwable in) {
            System.out.println ("Exception  " + in.toString());
	        System.exit (1);
        }
        return stRepos;

      }
    
    
    
    public ORB getOrb() {
        
        if (orb == null) {
            //System.getProperties().put("ORB.InitRefURL", "file:///D://DirectoryService//classes//Lookup.ior");
	       System.getProperties().put("ORB.InitRefURL", traderProperties.getProperty("TraderService.LOOKUPIORURL"));
	        orb =  com.cboe.ORBInfra.ORB.Orb.init(new String[0], null);
	    }
	    return orb;
	    
	}
	
	  
}

//EOF
