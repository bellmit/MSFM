package com.cboe.testDrive;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiCallback.CMIUserSessionAdminHelper;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV2.*;
import com.cboe.idl.cmiV3.*;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiV4.UserAccessV4Helper;
import com.cboe.idl.cmiV4.UserAccessV4;

import org.omg.PortableServer.POA;

public class CASLogon
{
    private static UserAccessV2 userAccessV2 = null;
    private static UserAccessV3 userAccessV3 = null;
    private static UserAccessV4 userAccessV4 = null;

    protected static POA poaReference = null;

    public static synchronized SessionManagerStructV2 logonToCAS(String userName, String password, String host, int port, char mode, boolean gmdText, short sessionMode)
            throws org.omg.CORBA.UserException
    {
        UserLogonStruct logonStruct = new UserLogonStruct(userName, password, Version.CMI_VERSION, mode);
        com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
        org.omg.CORBA.Object userSessionObject =
                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(userSessionAdminCallback);
        com.cboe.idl.cmiCallback.CMIUserSessionAdmin clientListener = CMIUserSessionAdminHelper.narrow(userSessionObject);
        System.out.println("Logging on to CAS as " + userName + " password " + password);

        if (userAccessV2 == null)
        {
            try
            {
                Object userObject = RemoteConnectionFactory.find().find_initial_object("http://" + host + ":" + port, "/UserAccessV2.ior");

                userAccessV2 = UserAccessV2Helper.narrow((org.omg.CORBA.Object) userObject);
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
        SessionManagerStructV2 sessionManagerV2 = null;
        try
        {
            sessionManagerV2 = userAccessV2.logon(logonStruct, sessionMode, clientListener, gmdText);
            userSessionAdminCallback.setUserSessionManager(sessionManagerV2.sessionManager);
            userSessionAdminCallback.setUserLogonStruct(logonStruct);

        } catch (Exception e)
        {
            System.out.println(logonStruct.userId + " could not log into CAS : ");
            e.printStackTrace();
        }
        return sessionManagerV2;
    }

    public static synchronized UserSessionManagerV3 logonToCASV3(String userName, String password, String host, int port, char mode, boolean gmdText, short sessionMode)
            throws org.omg.CORBA.UserException
    {
        UserLogonStruct logonStruct = new UserLogonStruct(userName, password, Version.CMI_VERSION, mode);
        com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
        org.omg.CORBA.Object userSessionObject =
                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(userSessionAdminCallback);
        com.cboe.idl.cmiCallback.CMIUserSessionAdmin clientListener = CMIUserSessionAdminHelper.narrow(userSessionObject);
        System.out.println("Logging on to CAS as " + userName + " password " + password);

        if (userAccessV3 == null)
        {
            try
            {
                Object userObject = RemoteConnectionFactory.find().find_initial_object("http://" + host + ":" + port, "/UserAccessV3.ior");

                userAccessV3 = UserAccessV3Helper.narrow((org.omg.CORBA.Object) userObject);
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
        UserSessionManagerV3 userSessionManagerV3 = null;
        try
        {
            userSessionManagerV3 = userAccessV3.logon(logonStruct, sessionMode, clientListener, gmdText);
            userSessionAdminCallback.setUserSessionManager(userSessionManagerV3);
            userSessionAdminCallback.setUserLogonStruct(logonStruct);

        } catch (Exception e)
        {
            System.out.println(logonStruct.userId + "could not log into CAS : ");
            e.printStackTrace();
        }
        return userSessionManagerV3;
    }

    public static UserSessionManagerV4 logonToCASV4(String userName, String password, String host,
                                                    int port, char mode, boolean gmdText,
                                                    short sessionMode)
            throws org.omg.CORBA.UserException
    {
        UserLogonStruct logonStruct = new UserLogonStruct(userName, password, Version.CMI_VERSION, mode);
        com.cboe.testDrive.CMIUserSessionAdmin userSessionAdminCallback = new com.cboe.testDrive.CMIUserSessionAdmin();
        org.omg.CORBA.Object userSessionObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(userSessionAdminCallback);
        com.cboe.idl.cmiCallback.CMIUserSessionAdmin clientListener = CMIUserSessionAdminHelper.narrow(userSessionObject);
        System.out.println("Logging on to CAS V4 as " + userName + " password " + password);

        UserSessionManagerV4 userSessionManagerV4 = getUserAccessV4(host, port).logon(logonStruct, sessionMode, clientListener, gmdText);
        userSessionAdminCallback.setUserSessionManager(userSessionManagerV4);
        userSessionAdminCallback.setUserLogonStruct(logonStruct);
        return userSessionManagerV4;
    }

    private static synchronized UserAccessV4 getUserAccessV4(String host, int port)
    {
        if(userAccessV4 == null)
        {
            Object userObject = RemoteConnectionFactory.find()
                    .find_initial_object("http://" + host + ":" + port, "/UserAccessV4.ior");
            userAccessV4 = UserAccessV4Helper.narrow((org.omg.CORBA.Object) userObject);
        }
        return userAccessV4;
    }
}
