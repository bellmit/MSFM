//
// -----------------------------------------------------------------------------------
// Source file: UserAccessPCQSHomeImpl.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.pcqs.UserAccessPCQSHelper;

import com.cboe.interfaces.application.UserAccessPCQS;
import com.cboe.interfaces.application.UserAccessPCQSHome;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessPCQSDelegate;

public class UserAccessPCQSHomeImpl extends ClientBOHome implements UserAccessPCQSHome
{
    private UserAccessPCQS userAccess;
    private com.cboe.idl.pcqs.UserAccessPCQS userAccessCorba;

    public UserAccessPCQS create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessPCQS");
        }
        if(userAccess == null)
        {
            UserAccessPCQSImpl bo = new UserAccessPCQSImpl();
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessPCQSInterceptor boi;
            try
            {
                boi = (UserAccessPCQSInterceptor) this.createInterceptor(bo);
                if(getInstrumentationEnablementProperty())
                {
                    boi.startInstrumentation(getInstrumentationProperty());
                }
            }
            catch(Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessPCQS", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
    }

    public UserAccessPCQS find()
    {
        return create();
    }

    public String objectToString()
    {
        try
        {
            if(userAccessCorba != null)
            {
                FoundationFramework ff = FoundationFramework.getInstance();
                return ff.getOrbService().getOrb().object_to_string(userAccessCorba);
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            Log.exception(this, "Could not stringify the userAccess object", e);
            return null;
        }
    }

    public void clientStart()
            throws Exception
    {
        userAccess = find();

        String poaName = POANameHelper.getPOAName(this);

        UserAccessPCQSDelegate delegate = new UserAccessPCQSDelegate(userAccess);

        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessPCQSHelper.narrow(obj);
    }

    public void clientInitialize()
            throws Exception
    {
        create();
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientShutdown()
    {
    }
}
