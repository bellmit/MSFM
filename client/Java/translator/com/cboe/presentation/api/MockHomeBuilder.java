//
// -----------------------------------------------------------------------------------
// Source file: MockHomeBuilder.java
//
// PACKAGE: com.cboe.presentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

//import com.cboe.presentation.api.APIFactoryImpl;
//import com.cboe.presentation.api.APIHome;
//import com.cboe.presentation.guiLogger.GUILogger;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.MockGUILoggerImpl;
//import com.cboe.presentation.qrm.GUIUserTradingParametersAPIFactory;
//import com.cboe.presentation.qrm.GUIUserTradingParametersAPIHome;
//import com.cboe.presentation.bookDepth.OrderBookAPIHome;
//import com.cboe.presentation.bookDepth.OrderBookAPIFactoryImpl;
/*
import com.cboe.presentation.status.StatusListModelFactory;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.userSession.TranslatorInitializer;
import com.cboe.presentation.userSession.UserSessionImpl;
import com.cboe.presentation.windowManagement.WindowManagerFactory;
import com.cboe.presentation.windowManagement.WindowManagerImpl;
import com.cboe.presentation.textMessage.MessageProcessorFactoryImpl;
import com.cboe.presentation.textMessage.MessageProcessorHome;
import com.cboe.presentation.alert.AudibleAlertGeneratorFactory;
import com.cboe.presentation.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.exceptionHandling.DefaultExceptionHandlerImpl;
import com.cboe.presentation.alert.CBOEReportGeneratorFactory;
*/

/**
 *  Responsible for building homes for home patterns for the client
 *  presentation.
 */
public class MockHomeBuilder
{
    /**
     *  Initializes the homes for factories.
     */
    public static void initialize()
    {
        //DefaultExceptionHandlerHome.create(DefaultExceptionHandlerImpl.class);
        //AppPropertiesFileFactory.create(AppPropertiesFileImpl.class);
        com.cboe.presentation.common.logging.GUILoggerHome.create(com.cboe.presentation.common.logging.MockGUILoggerImpl.class);
        APIHome.create(APIFactoryImpl.class);
        //UserSessionFactory.create(UserSessionImpl.class);

        //WindowManagerFactory.create(WindowManagerImpl.class);
        //MessageProcessorHome.create(MessageProcessorFactoryImpl.class);
        //OrderBookAPIHome.create(OrderBookAPIFactoryImpl.class);
        //GUIUserTradingParametersAPIHome.create(GUIUserTradingParametersAPIFactory.class);

        //CBOEReportGeneratorFactory.initialize();
        //AudibleAlertGeneratorFactory.initialize();
        //StatusListModelFactory.initialize();
        //TranslatorInitializer.initialize();
    }
}
