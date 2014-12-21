//
// -----------------------------------------------------------------------------------
// Source file: UserOrderEntryHomeImpl.java
//
// PACKAGE: com.cboe.application.orderEntry;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.orderEntry;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserOrderEntryHome;
import com.cboe.interfaces.application.OrderEntryV9;

public class UserOrderEntryHomeImpl extends ClientBOHome implements UserOrderEntryHome
{
    /**
     * UserOrderEntryHomeImpl constructor.
     */
    public UserOrderEntryHomeImpl()
    {
        super();
    }

    /**
     * Creates a new user instance of a OrderEntry Service.
     */
    public OrderEntryV9 create(SessionManager sessionMgr)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserOrderEntryImpl for " + sessionMgr);
        }

        UserOrderEntryImpl bo = new UserOrderEntryImpl();
        bo.setSessionManager(sessionMgr);

        addToContainer(bo);

        bo.create(String.valueOf(bo.hashCode()));
        //add the bo to the container.

        UserOrderEntryInterceptor boi = null;
        try
        {
            boi = (UserOrderEntryInterceptor)this.createInterceptor(bo);
            boi.setSessionManager(sessionMgr);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
            Log.alarm(this, "Failed to create interceptor");
        }

        return boi;
    }
}