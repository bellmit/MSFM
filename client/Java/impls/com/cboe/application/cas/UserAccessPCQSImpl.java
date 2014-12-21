//
// -----------------------------------------------------------------------------------
// Source file: UserAccessPCQSImpl.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.pcqs.PCQSSessionManagerHelper;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.application.UserAccessPCQS;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.PCQSSessionManager;

import com.cboe.util.ExceptionBuilder;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.PCQSSessionManagerDelegate;

public class UserAccessPCQSImpl extends BObject implements UserAccessPCQS
{
    public UserAccessPCQSImpl()
    {
        super();
    }

    protected com.cboe.interfaces.application.PCQSSessionManager getPCQSUserSession(
            com.cboe.idl.cmi.UserSessionManager manager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        PCQSSessionManager pcqsSession;

        SessionManager session = ServicesHelper.getRemoteSessionManagerHome()
                .findRemoteSession(manager.getValidSessionProfileUser().userId, manager);

        if(session == null)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Could not find existing User Session.");
            }
            throw ExceptionBuilder.notFoundException("existing user session not found", 0);
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating PCQSSessionManager for User ID "+manager.getValidUser().userId);
            }
            pcqsSession = ServicesHelper.createPCQSSessionManager(session);
            return pcqsSession;
        }
    }

    /**
     * build a PCQSUserSessionManager corba object given an existing com.cboe.idl.cmi.UserSessionManager
     */
    public com.cboe.idl.pcqs.PCQSSessionManager getPCQSUserSessionManager(com.cboe.idl.cmi.UserSessionManager manager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        String poaName = POANameHelper.getPOAName((BOHome) ServicesHelper.getPCQSSessionManagerHome());
        PCQSSessionManager pcqsSession = getPCQSUserSession(manager);
        PCQSSessionManagerDelegate delegate = new PCQSSessionManagerDelegate(pcqsSession);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        com.cboe.idl.pcqs.PCQSSessionManager corbaObj = PCQSSessionManagerHelper.narrow(obj);
        pcqsSession.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}
