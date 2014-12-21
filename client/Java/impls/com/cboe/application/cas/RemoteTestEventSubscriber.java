
/**
 * Title:        Client Application Server<p>
 * Description:  Your description<p>
 * Copyright:    Copyright (c) 1998<p>
 * Company:      Your Company<p>
 * @author Your Name
 * @version
 */
package com.cboe.application.cas;

import java.util.*;

import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmi.*;

public class RemoteTestEventSubscriber extends TestEventSubscriber {

//    private static final String USER_ID="sbtUser";
    private static final String USER_ID="CCC";
    public RemoteTestEventSubscriber() {
        super();
    }

     /** Override base method */
    protected  void initFFEnv() throws Exception
    {
    }

    protected  void initUserSession()
    {
        try {
            if ( session == null )
            {
                UserAccess userAccess =  TestUserAccessFactory.find();
                UserLogonStruct logonStruct = new UserLogonStruct(USER_ID, "", "", LoginSessionModes.STAND_ALONE_TEST);
                System.out.println("Logging onto CAS");
                session = userAccess.logon(logonStruct, LoginSessionTypes.PRIMARY, userSessionListener, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * initializes the callback consumer objects
     */
     protected  void initializeCallbacks()
    {
        super.initializeCallbacks();
   }

       public static void main(String[] args)
    {
        boolean unsubscribe = false;
        java.util.Properties prop = System.getProperties();
        String unsubscribeStr =(String)prop.get("UNSUBSCRIBE");

        if (unsubscribeStr != null && unsubscribeStr.equalsIgnoreCase("U"))
        {
            System.out.println("============> unsubcribe is requested");
            unsubscribe = true;
        }

        try
        {
            RemoteTestEventSubscriber test = new RemoteTestEventSubscriber();
            test.initialize(args);
            test.runTestCases(unsubscribe);
            try {
                java.lang.Object waiter = new java.lang.Object();
                synchronized(waiter)
                {
                    waiter.wait();
                }
                test.testLogOut();
            }
            catch(Exception e)
            {
            }

        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}
