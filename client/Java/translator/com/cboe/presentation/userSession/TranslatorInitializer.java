//
// -----------------------------------------------------------------------------------
// Source file: TranslatorInitializer.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import org.omg.CORBA.UserException;

import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;

/**
 * Takes care of registering with the Translator to initialize it after login.
 */
public class TranslatorInitializer implements UserSessionListener
{
    private static TranslatorInitializer myself = null;

    /**
     * Should not be instantiated by anyone other than self
     */
    private TranslatorInitializer()
    {}

    /**
     * creates instance of itself
     */
    public static synchronized void initialize()
    {
        if(myself == null)
        {
            myself = new TranslatorInitializer();

            UserSessionFactory.findUserSession().addUserSessionListener(myself);
        }
    }

    public void userSessionChange(UserSessionEvent event)
    {
        final UserSessionEvent localEvent = event;

        GUIWorkerImpl worker = new GUIWorkerImpl()
        {
            @SuppressWarnings({"ProhibitedExceptionDeclared"})
            public void execute() throws Exception
            {
                process(localEvent);
            }

            public void handleException(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Error trying to start Translator " +
                                                              "Initializer");
            }
        };
        APIWorkerImpl.run(worker);
    }

    public void process(UserSessionEvent event)
    {
        if(event.getActionType() == UserSessionEvent.LOGGED_IN_EVENT)
        {
            GUILoggerHome.find().debug(getClass().getName() + ":process(UserSessionEvent event) " +
                                       "LOGGED_IN_EVENT",
                                       GUILoggerBusinessProperty.COMMON,
                                       "Got logged in event from UserSession. " +
                                       "Initializing Translator");

            try
            {
                APIHome.findOrderQueryAPI().initializeOrderCallbackListener();
                APIHome.findQuoteAPI().initializeQuoteV2CallbackListener();
                APIHome.findOrderManagementTerminalAPI().subscribeOrdersForManualHandling();
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Error trying to start " +
                                                              "Translator Initializer");
            }
        }
    }
}
