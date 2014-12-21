package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.exceptions.*;

public class CASTestLogin extends Thread
{
    protected String UserName;
    protected TestParameter testParm = null;
    protected UserAccessV2 userAccess = null;
    protected UserSessionManagerV2 userSessionManagerV2 = null;
    protected UserSessionManager userSessionManager = null;
    protected SessionManagerStructV2 sessionManagerV2;

    public CASTestLogin(TestParameter parm, String userID, UserAccessV2 userAccess) throws Exception
    {
        this.UserName = userID;
        this.testParm = parm;
        this.userAccess = userAccess;
        System.out.println("Created Login Thread for user = " + UserName);
    }

    public  void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }

        try
        {
            UserLogonStruct logonStruct = new UserLogonStruct(UserName, UserName, Version.CMI_VERSION, testParm.mode);
            com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
            org.omg.CORBA.Object userSessionObject =
                    (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(userSessionAdminCallback);
            com.cboe.idl.cmiCallback.CMIUserSessionAdmin clientListener = CMIUserSessionAdminHelper.narrow(userSessionObject);
            System.out.println("Logging on to CAS as " + UserName + " password " + UserName);


            try
            {
                long startTime = System.currentTimeMillis( );

                SessionManagerStructV2 sessionManagerV2 = userAccess.logon(logonStruct, testParm.sessionMode, clientListener, testParm.gmdText);
                long endTime = System.currentTimeMillis( );

                userSessionManagerV2 = sessionManagerV2.sessionManagerV2;
                userSessionManager = sessionManagerV2.sessionManager;
                userSessionAdminCallback.setUserSessionManager(userSessionManager);
                userSessionAdminCallback.setUserLogonStruct(logonStruct);



                System.out.println(testParm.host + ":" + UserName + ":" + endTime + ":" + startTime + ":" + (endTime-startTime) + ":ms");
            } catch (Exception e)
            {
                System.out.println(this.UserName + " could not log into CAS : ");
            }


        }
         catch (Exception e)
        {
            System.out.println("Login thread failed ");
            e.printStackTrace();
            return;
        }

        try {
            Thread.currentThread().sleep(120000);
        } catch (Exception e)
        {
        }
    }
}
