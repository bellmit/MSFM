//
// -----------------------------------------------------------------------------------
// Source file: ARCommandServiceFactory.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import com.cboe.interfaces.instrumentation.adminRequest.ExecuteCommandService;
import com.cboe.interfaces.instrumentation.adminRequest.GetAllCommandsService;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.properties.PropertiesFile;

/**
 * Implements a Factory to get service interfaces from for executing Admin Request commands.
 */
public class ARCommandServiceFactory
{
    private static final String PROP_SECTION = "Adapters";
    private static final String GET_ALL_SERVICE_PROP_NAME = "GetAllCommandsService.Class";
    private static final String EXECUTE_COMMAND_SERVICE_PROP_NAME = "ExecuteCommandService.Class";

    private ARCommandServiceFactory() {}

    /**
     * Provides a service interface for obtaining all Commands from an ORB.
     * @return service to use to query for available AR Commands from an ORB.
     */
    public static GetAllCommandsService getAllCommandsService()
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            PropertiesFile propFile = AppPropertiesFileFactory.find();
            String className = propFile.getValue(PROP_SECTION, GET_ALL_SERVICE_PROP_NAME);
            if(className != null)
            {
                try
                {
                    //noinspection RawUseOfParameterizedType
                    Class theClass = Class.forName(className);
                    Class<GetAllCommandsService> interfaceClass = GetAllCommandsService.class;

                    Object newOBJ = theClass.newInstance();

                    if(interfaceClass.isInstance(newOBJ))
                    {
                        return (GetAllCommandsService) newOBJ;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                ARCommandServiceFactory.class.getName() +
                                ": Does not support interface GetAllCommandsService. className=" +
                                theClass.getName());
                    }
                }
                catch(ClassNotFoundException e)
                {
                    throw new IllegalArgumentException(
                            "GetAllCommandsService could not be initialized.", e);
                }
                catch(IllegalAccessException e)
                {
                    throw new IllegalArgumentException(
                            "GetAllCommandsService could not be initialized.", e);
                }
                catch(InstantiationException e)
                {
                    throw new IllegalArgumentException(
                            "GetAllCommandsService could not be initialized.", e);
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "GetAllCommandsService could not be initialized. " +
                        "Properties File setting " + PROP_SECTION + '.' +
                        GET_ALL_SERVICE_PROP_NAME + " was not set.");
            }
        }
        else
        {
            throw new IllegalArgumentException("GetAllCommandsService could not be initialized. " +
                    "Properties File was not available.");
        }
    }

    /**
     * Provides a service interface for executing AR Commands to an ORB.
     * @return service to use to execute AR Commands to an ORB.
     */
    public static ExecuteCommandService getExecuteCommandService()
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            PropertiesFile propFile = AppPropertiesFileFactory.find();
            String className = propFile.getValue(PROP_SECTION, EXECUTE_COMMAND_SERVICE_PROP_NAME);
            if(className != null)
            {
                try
                {
                    //noinspection RawUseOfParameterizedType
                    Class theClass = Class.forName(className);
                    Class<ExecuteCommandService> interfaceClass = ExecuteCommandService.class;

                    Object newOBJ = theClass.newInstance();

                    if(interfaceClass.isInstance(newOBJ))
                    {
                        return (ExecuteCommandService) newOBJ;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                ARCommandServiceFactory.class.getName() +
                                ": Does not support interface ExecuteCommandService. className=" +
                                theClass.getName());
                    }
                }
                catch(ClassNotFoundException e)
                {
                    throw new IllegalArgumentException(
                            "ExecuteCommandService could not be initialized.", e);
                }
                catch(IllegalAccessException e)
                {
                    throw new IllegalArgumentException(
                            "ExecuteCommandService could not be initialized.", e);
                }
                catch(InstantiationException e)
                {
                    throw new IllegalArgumentException(
                            "ExecuteCommandService could not be initialized.", e);
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "ExecuteCommandService could not be initialized. " +
                        "Properties File setting " + PROP_SECTION + '.' +
                        EXECUTE_COMMAND_SERVICE_PROP_NAME + " was not set.");
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "ExecuteCommandService could not be initialized. " +
                    "Properties File was not available.");
        }
    }
}
