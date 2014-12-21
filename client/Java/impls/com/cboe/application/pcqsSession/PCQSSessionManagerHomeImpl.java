//
// -----------------------------------------------------------------------------------
// Source file: PCQSSessionManagerHomeImpl.java
//
// PACKAGE: com.cboe.application.pcqsSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.pcqsSession;

import com.cboe.interfaces.application.PCQSSessionManagerHome;
import com.cboe.interfaces.application.PCQSSessionManager;
import com.cboe.interfaces.application.SessionManager;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class PCQSSessionManagerHomeImpl extends ClientBOHome implements PCQSSessionManagerHome
{

    public PCQSSessionManagerHomeImpl()
    {
        super();
    }

    public PCQSSessionManager create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating PCQSSessionManagerImpl for " + sessionManager);
        }
        PCQSSessionManagerImpl bo = new PCQSSessionManagerImpl();
        bo.create(String.valueOf(bo.hashCode()));
        bo.setSessionManager(sessionManager);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        PCQSSessionManagerInterceptor boi;
        try
        {
            bo.initialize();
            boi = (PCQSSessionManagerInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
            return null;
        }
        return boi;
    }

    public void clientInitialize()
            throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

}
