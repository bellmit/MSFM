package com.cboe.testDrive;


import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
/**
 *
 */
public class CMIUserSessionAdmin extends com.cboe.idl.cmiCallback.CMIUserSessionAdminPOA
{
    com.cboe.idl.cmi.UserSessionManager userSessionManager=null;
    com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct=null;

    /**
     */
        public void acceptCallbackRemoval(CallbackInformationStruct p0, String p1, int p2)
        {
            System.out.println("Callback is removed from CAS ...");
        }
    public HeartBeatStruct acceptHeartBeat( HeartBeatStruct heartbeat)
    {
        return heartbeat;
    }
    public void acceptLogout (String reason )
    {
        System.out.println("You have been logged out of the CAS for the following reason: "+reason);
        System.exit(1);
    }

    public void acceptTextMessage(MessageStruct msg)
    {
        System.out.println("Message from SBT System:");
        System.out.println(msg);
    }

    /**
    // This setter was added to allow an application to set the
    // UserLogonStruct reference in this call back object.
    // The UserLogonStruct is used to re-authenticate the client application
    // to the CAS when acceptAuthenticationNotice() is invoked by the CAS.
     */
    public void setUserLogonStruct(com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct ) {
          this.userLogonStruct = userLogonStruct;
    }

    /**
    // This setter was added to allow an application to set the
    // UserSessionManager reference in this call back object.
    // The UserSessionManager reference is not available when this
    // object is constructed, so it must be assigned later.
     */
    public void setUserSessionManager(com.cboe.idl.cmi.UserSessionManager userSessionManager) {
          this.userSessionManager = userSessionManager;
    }

    /**
     // When an accept authentication notice is received - you must
     // re-authenticate your application. It is strongly, strongly
     // recommended that this be done via a pop up window to the user
     // asking them to re-login for interactive applications.
     // For automated trading or "black box" systems - you can
     // reauthenticate within this callback operation.
     */
    public void acceptAuthenticationNotice()
    {
        System.out.println("acceptAuthenticationNotice callback invoked");

        if (userSessionManager != null && userLogonStruct != null   )
        {
            try
            {
                 userSessionManager.authenticate(userLogonStruct);
            }
            catch (Exception e)
            {
                     System.out.println(e);
                                  e.printStackTrace() ;
                    // System.exit(e.details.error);
            }

        }

    }
}
