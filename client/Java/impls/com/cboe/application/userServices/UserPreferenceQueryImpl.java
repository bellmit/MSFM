// $Workfile$ com.cboe.application.userService.UserPreferenceQueryImpl.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial Version         3/29/99      Derek T. Chambers-Boucher
*   Implementation          3/30/99      Connie Feng
*   Implementation          9/08/99      Derek T. Chambers-Boucher
*   Implementation          11/18/99     Michael Pyatetsky
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.userServices;

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;

import com.cboe.application.shared.*;
import com.cboe.application.shared.consumer.*;

import com.cboe.domain.util.UserPreferenceCache;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.util.ExceptionBuilder;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

import com.cboe.util.event.*;

/**
 * UserPreferenceQuery implementation
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/29/1999
 */
public class UserPreferenceQueryImpl extends BObject implements UserPreferenceQuery, UserSessionLogoutCollector
{
    ///////////////////////// instance variables //////////////////
    /** UserService reference */
    private UserService userService;

    /** SessionManager reference */
    private SessionManager sessionManager;

    private UserSessionLogoutProcessor logoutProcessor;

    /** User Preferences collection storage */
    private UserPreferenceCache userPrefs;
    private UserPreferenceCache systemPrefs;

    /** User structure */
    private SessionProfileUserStruct userStruct;

    /** Indicator on if the preferences needs to be cached*/
    private boolean isCached = true;

    /**
     * UserPreferencesQueryImpl constructor.
     */
    public UserPreferenceQueryImpl()
    {
        super();
    }// end of constructor

    public void setSessionManager(SessionManager theSession)
    {
        sessionManager = theSession;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
        LogoutServiceFactory.find().addLogoutListener(theSession, this);
        try
        {
            userStruct = theSession.getValidSessionProfileUser();
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }

    }// end of setSessionManager

    /**
     * This method preloads User Preferences and System Preferences cache
     * form the server with given userName
     */
    private void preloadCache()
    {
        try
        {
            PreferenceStruct [] userPreferences    = getUserService().getAllUserPreferences( userStruct.userId );
            for( int i = 0; i < userPreferences.length; i++ )
            {
                userPrefs.setPreference( userStruct.userId, userPreferences[i] );
            }

            PreferenceStruct [] systemPreferences  = getUserService().getAllSystemPreferences( userStruct.userId );
            for( int i = 0; i < systemPreferences.length; i++ )
            {
                systemPrefs.setPreference( userStruct.userId, systemPreferences[i] );
            }
            setCached( true );
        } catch ( Exception e )
        {
            Log.exception(this, "session : " + sessionManager, e);
        }

    }
    private boolean isCached()
    {
        return isCached;
    }// end of isCached

    private void setCached(boolean cached)
    {
        isCached = cached;
    }// end of setCached

    /////////////////// Inherited methods //////////////////////////
    /**
     * BObject.create overridden method.
     */
    public void create(String name)
    {
        super.create(name);

        userPrefs = new UserPreferenceCache();
        systemPrefs = new UserPreferenceCache();
        preloadCache();

    }

    /////////////////// Exported methods ///////////////////////////
    /**
     * Sets a sequence of user preferences.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param preferenceSequence a sequence of preferences to set.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void setUserPreferences(PreferenceStruct[] preferenceSequence)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling setUserPreferences for " + sessionManager);
        }
        StringBuilder msgBuffer = new StringBuilder(100);
        boolean thrownException = false;

        // First set the preference on the server
        getUserService().setUserPreferences(userStruct.userId, preferenceSequence);

        // Now set the preference locally if the server accepted it and a cache is available.
        if (isCached)
        {
            for (int i = preferenceSequence.length - 1; i >= 0; i--)
            {
                try
                {
                    userPrefs.setPreference(userStruct.userId, preferenceSequence[i]);
                }
                catch (DataValidationException e)
                {
                    // A DataValidationException occurred.  Maintain "Best Effort",
                    // buffer the details message and then throw a composite
                    // DataValidationException when finished processing.
                    msgBuffer.append(e.details.message).append("\n");
                    thrownException = true;
                }
            }
        }

        if (thrownException == true)
        {
            throw ExceptionBuilder.dataValidationException(msgBuffer.toString(), DataValidationCodes.PREFERENCE_PATH_MISMATCH);
        }
    }

    /**
     * Removes a sequence of user preferences.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param preferenceSequence a sequence of preferences to set.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreference(PreferenceStruct[] preferenceSequence)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling removeUserPreference for " + sessionManager);
        }
        getUserService().removeUserPreference( userStruct.userId, preferenceSequence );
        for (int i = preferenceSequence.length - 1; i >= 0; i--)
        {
            userPrefs.removePreference(userStruct.userId, preferenceSequence[i].name);
        }
   }

    /**
     * Gets all user preferences from the system.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllUserPreferences()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAllUserPreferences for " + sessionManager);
        }
        return userPrefs.getPreferences(userStruct.userId, "", true);
    }

    /**
     * Gets all user preferences that match the specified prefix.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param prefix
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getUserPreferencesByPrefix(String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getUserPreferencesByPrefix for " + sessionManager);
        }
        return userPrefs.getPreferences(userStruct.userId, prefix, true);
    }

    /**
     * Removes all user preferences that match the specified prefix.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param prefix
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreferencesByPrefix(String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling removeUserPreferencesByPrefix for " + sessionManager);
        }
        getUserService().removeUserPreferencesByPrefix( userStruct.userId, prefix );
        userPrefs.removePreference(userStruct.userId, prefix);
    }

    /**
     * Gets all of a users system preference.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllSystemPreferences()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAllSystemPreferences for " + sessionManager);
        }
        return systemPrefs.getPreferences(userStruct.userId, "", true);
    }

    /**
     * Gets all of a users system preferences that match the specified prefix..
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param prefix
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getSystemPreferencesByPrefix(String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getSystemPreferencesByPrefix for " + sessionManager);
        }
        return systemPrefs.getPreferences(userStruct.userId, prefix, true);
    }

    //////////////// private methods ////////////////////////////
    /**
     * Returns reference to user service.
     *
     * @author Connie Feng
     */
    private UserService getUserService()
    {
        if (userService == null )
        {
            userService = ServicesHelper.getUserService();
        }

        return userService;
    }// end of getUserService

    /**
     * gets the current user information
     *
     * @author Connie Feng
     */
    private SessionProfileUserStruct getUserStruct()
    {
        if ( userStruct == null )
        {
           try
           {
              userStruct = getSessionManager().getValidSessionProfileUser();
           }
           catch(Exception e)
           {
               Log.exception(this, "session : " + sessionManager, e);
           }
        }
        return userStruct;
    }// end of getUserStruct

    /**
    * get the session manager reference
    */
    private SessionManager getSessionManager()
    {
        return sessionManager;
    }// end of getSessionManager

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        userService = null;
        sessionManager = null;
        userPrefs = null;
        systemPrefs = null;
    }
}// EOF
