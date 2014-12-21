package com.cboe.cfix.cas.userAccess;

/**
 * This type was created in VisualAge.
 */

import com.cboe.domain.startup.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public class CfixUserAccessHomeImpl extends ClientBOHome implements CfixUserAccessHome
{
    /**
     * MarketDataFactory constructor comment.
     */
    public final static String SESSION_MODE = "session_mode";

    private CfixUserAccessHomeImpl instance;
    private CfixUserAccess userAccess;
    private String sessionId;
    private char sessionMode;

    public CfixUserAccess create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessImpl");
        }
        if (userAccess == null)
        {
            CfixUserAccessImpl bo = new CfixUserAccessImpl(sessionMode);
            bo.create(String.valueOf(bo.hashCode()));
            // Every BObject must be added to the container.
            addToContainer(bo);
            // Every BObject create MUST have a name...if the object is to be a managed object.
            userAccess = bo;
        }
        return userAccess;
    }

    public CfixUserAccess find()
    {
        return create();
    }


    public void clientStart()
            throws Exception
    {
        CfixUserAccess userAccess = find();
    }

    public void clientInitialize()
            throws Exception
    {
        sessionMode = getProperty(SESSION_MODE).charAt(0);
        if (Log.isDebugOn())
        {
            Log.debug(this, "LoginSessionMode = " + sessionMode);
        }
        create();
    }
}
