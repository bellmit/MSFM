//
// -----------------------------------------------------------------------------------
// Source file: UserAccessPCQSFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.pcqs.UserAccessPCQSHelper;
import com.cboe.idl.pcqs.UserAccessPCQS;
import com.cboe.idl.pcqs.PCQSSessionManager;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.SystemException;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;

public class UserAccessPCQSFactory
{
    private static UserAccessPCQS pcqsUserAccess;

    public UserAccessPCQSFactory()
    {
        super();
    }

    public static UserAccessPCQS find()
    {
        if(pcqsUserAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_PCQS_object();
                pcqsUserAccess = UserAccessPCQSHelper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch(Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessPCQSFactory.find()",
                                               "UserAccessPCQS remote object connection exception", e);
            }
        }
        return pcqsUserAccess;
    }

    public static PCQSSessionManager getPCQSUserSessionManager(UserSessionManager userSessionManager)
            throws AuthorizationException, CommunicationException, SystemException, NotFoundException
    {
        UserAccessPCQS userAccess = find();
        PCQSSessionManager pcqsSessionManager = null;
        if(userAccess != null)
        {
            pcqsSessionManager = userAccess.getPCQSUserSessionManager(userSessionManager);
        }
        return pcqsSessionManager;
    }
}
