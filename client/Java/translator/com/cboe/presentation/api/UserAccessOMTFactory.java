//
// -----------------------------------------------------------------------------------
// Source file: \client\Java\translator\com\cboe\presentation\api\UserAccessOMTFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.omt.OMTSessionManager;
import com.cboe.idl.omt.UserAccessOMT;
import com.cboe.idl.omt.UserAccessOMTHelper;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;

/**
 * Factory for gaining access to the OMTSessionManager, which in turn gives us access to the OMT service.
 * @author Shawn Khosravani
 * @since May 22, 2007
 */
public class UserAccessOMTFactory
{
    private static UserAccessOMT userAccessOMT;

    public UserAccessOMTFactory()
    {
    }

    public static UserAccessOMT find()
    {
        if(userAccessOMT == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_OMT_object();
                userAccessOMT = UserAccessOMTHelper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch(Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessOMTFactory.find()",
                                               "UserAccessOMT remote object connection exception", e);
            }
        }
        return userAccessOMT;
    }

    public static OMTSessionManager getOMTSUserSessionManager(UserSessionManager userSessionManager)
           throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        UserAccessOMT userAccess = find();
        OMTSessionManager omtSessionManager = null;
        if(userAccess != null)
        {
            omtSessionManager = userAccess.getOMTUserSessionManager(userSessionManager);
        }
        return omtSessionManager;
    }
}
