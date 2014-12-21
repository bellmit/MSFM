/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 2, 2002
 * Time: 9:14:12 AM
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.idl.cmi.UserSessionManager;

import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPIHomeFactory;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketQueryAPI;
import com.cboe.interfaces.intermarketPresentation.api.NBBOAgentAPI;
import com.cboe.interfaces.presentation.permissionMatrix.Permission;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.permissionMatrix.PermissionMatrixFactory;
import com.cboe.presentation.userSession.UserSessionEvent;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.userSession.UserSessionListener;

public class IntermarketAPIHomeFactoryImpl implements IntermarketAPIHomeFactory, UserSessionListener
{
    private boolean isInitialized = false;
    private UserSessionManager userSessionManager;
    public IntermarketAPIHomeFactoryImpl()
    {
        super();
        initialize();
        userSessionManager = null;
    }
    private void initialize()
    {
        // register for user session events - login
        UserSessionFactory.findUserSession().addUserSessionListener(this);
    }

    public void userSessionChange(UserSessionEvent event)
    {
        if(event.getActionType() == event.LOGGED_IN_EVENT)
        {
            boolean allowIntermarketApiAccess = new Boolean(System.getProperty(IntermarketAPI.ALLOW_INTERMARKET_ACCESS_PROPERTY_NAME)).booleanValue();
            if(allowIntermarketApiAccess &&
               PermissionMatrixFactory.findUserPermissionMatrix().
                       isAllowed(Permission.REGISTER_NBBO_AGENT))
            {
                createIntermarketAPIFactory();
            }
        }
    }

    public void setUserSessionManager(UserSessionManager userSessionManager)
    {
        if(isInitialized())
        {
            throw new IllegalStateException("IntermarketAPIHomeFactory is already initialized.  UserSessionManager cannot be reset.");
        }
        this.userSessionManager = userSessionManager;
    }

    public IntermarketQueryAPI findIntermarketQueryAPI()
    {
        return findIntermarketAPI();
    }

    public NBBOAgentAPI findNBBOAgentAPI()
    {
        return findIntermarketAPI();
    }

    private void createIntermarketAPIFactory()
    {
        try
        {
            IntermarketAPIFactory.create(userSessionManager);
            isInitialized = true;
        }
        catch ( Exception e )
        {
            GUILoggerHome.find().exception("com.cboe.intermarketPresentation.api.IntermarketAPIHomeFactoryImpl.create()","",e);
        }
    }

    public IntermarketAPI findIntermarketAPI()
    {
        if( isInitialized == false)
        {
            throw new IllegalStateException("IntermarketAPI has not been initialized.");
        }
        return IntermarketAPIFactory.find();
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }
}
