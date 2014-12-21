
//-----------------------------------------------------------------------
// FILE: RemoveType.java
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
import javax.naming.*;
import javax.naming.directory.*;


// local packages
import com.cboe.directoryService.ServiceTypeRepositoryImpl;
import org.omg.CosTrading.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.*;


// RemoveType

public class RemoveType {

		public static ORB orb = null;

        public static void main(String args[]) {

            ExampleUtility exampleUtility = new ExampleUtility(args[0]); 
        
            ServiceTypeRepository stRepos  = exampleUtility.getServiceTypeRepository();

            orb = exampleUtility.getOrb();
		
			String id = ServiceTypeRepositoryHelper.id();
			System.out.println("Service name :" + id );
    
			/*
			    This expects that an add has been done using the ADD_TYPE example.
			    If not, the remove will fail.
			*/

            try{
		       //POA_ServiceTypeRepository_tie stReposTie = new POA_ServiceTypeRepository_tie(new ServiceTypeRepositoryImpl(traderProperties));
                //stReposTie.remove_type(id);
               stRepos.remove_type(id);
              
			}catch ( org.omg.CORBA.SystemException ex){
				System.out.println("Exception during RemoveType :" + ex.toString());
			}catch ( Exception ux){
			    System.out.println("Exception during RemoveType :" + ux.toString());
			    System.out.println("Message :" + ux.getMessage());
			}
			System.out.println("..... RemoveType done");
			System.exit(0);
		}
}

//EOF
