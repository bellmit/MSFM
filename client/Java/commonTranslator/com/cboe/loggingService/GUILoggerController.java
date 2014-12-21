//
// -----------------------------------------------------------------------------------
// Source file: GUILoggerController.java
//
// PACKAGE: com.cboe.loggingService;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.loggingService;

import java.util.*;

/**
 *  Used for adding new clients to a logger has already been initialized.
 *
 *@author     Martha Fourt
 *@created    March 8, 2007
 */
public class GUILoggerController
{
    private LoggingServiceInterface defaultLogger = null;
	
	public GUILoggerController(LoggingServiceInterface aLogger) {
		defaultLogger = aLogger;
	} 

    /**
        Configure new agents for the (already initialized) logging service.
    *
    *  @param LoggingDispatcher logger (a logging dispatcher that has already been initiallized.)
    *  @param Properties  (a properties file containing the logging agent(s) to be added.)
    *  @param MsgFilter   (a filter determining which types of messages will be logged by these agent.) 	
    */
    public void initializeLoggingAgents(LoggingDispatcher logger, Properties properties, MsgFilter filter) 
            throws InitializationFailedException {

        defaultLogger.log(MsgPriority.low, MsgCategory.audit, "initializeAgents()" );

        String loggingAgentNames = properties.getProperty(  "LoggingService.loggingAgentNames" );
        if( loggingAgentNames == null ) 
        {
            throw new InitializationFailedException( "Could not find logging agent names." );
        }
        StringTokenizer agentNameTokenizer = new StringTokenizer( loggingAgentNames, ",", false );

        while( agentNameTokenizer.hasMoreTokens() ) 
        {
            defaultLogger.log(MsgPriority.low, MsgCategory.audit, "Initializing next agent." );

            String nextAgentName = agentNameTokenizer.nextToken().trim();
            defaultLogger.log(MsgPriority.low, MsgCategory.debug, nextAgentName );

            // Get the type of the agent and instantiate the agent.
            String nextAgentType = null;
            LoggingAgent agent = null;
            try 
            {
                defaultLogger.log(MsgPriority.low, MsgCategory.audit, "Checking the agent type." );
                nextAgentType = properties.getProperty(  "LoggingService.loggingAgent." + nextAgentName + ".type" );
                if( nextAgentType == null ) 
                {
                    throw new InitializationFailedException( "Could not find type for logging agent: " + nextAgentName);
                }
	            defaultLogger.log( nextAgentType );
                Object agentObject = Class.forName( nextAgentType ).newInstance();
                if( ! ( agentObject instanceof LoggingAgent ) ) 
                {
                    defaultLogger.log(MsgPriority.critical, MsgCategory.systemAlarm, "Unidentified logging agent type. Failed to initialize logging agent." );
                    throw new InitializationFailedException( "Agent named \"" + nextAgentName + "\" is not a valid logging agent. It does not subclass LoggingAgent." );
                }
                agent = (LoggingAgent) agentObject;

                defaultLogger.log(MsgPriority.low, MsgCategory.audit, "Created new agent of type \"" + nextAgentType + "\"." );
            }
            catch( ClassNotFoundException e ) 
            {
                defaultLogger.log(MsgPriority.critical, MsgCategory.systemAlarm,
                    "Failed to add logging agent named \"" + nextAgentName + "\". Could not find the class \"" + nextAgentType + "\" in the classpath." );
                throw new InitializationFailedException( "Failed to add logging agent named \"" + nextAgentName + "\". Could not find the class \"" + nextAgentType + "\" in the classpath." );
            }
            catch( InstantiationException e ) 
            {
                defaultLogger.log(MsgPriority.critical, MsgCategory.systemAlarm,
                    "Failed to add logging agent named \"" + nextAgentName + "\". Could not instantiate the class \"" + nextAgentType + "\" using its default public constructor." );
                throw new InitializationFailedException( "Failed to add logging agent named \"" + nextAgentName + "\". Could not instantiate the class \"" + nextAgentType + "\" using its default public constructor." );
            }
            catch( IllegalAccessException e ) 
            {
                defaultLogger.log(MsgPriority.critical, MsgCategory.systemAlarm,
                    "Failed to add logging agent named \"" + nextAgentName + "\". Could not instantiate the class \"" + nextAgentType + "\" using its default public constructor. Make sure both the class and its default constructor are public." );
                throw new InitializationFailedException( "Failed to add logging agent named \"" + nextAgentName + "\". Could not instantiate the class \"" + nextAgentType + "\" using its default public constructor. Make sure both the class and its default constructor are public." );
            }

            // Initialize the agent and add it to the logging service and to the central logging component.
            try {
                defaultLogger.log(MsgPriority.low, MsgCategory.audit, "Initializing, starting and adding logging agent." );
                agent.setUniqueName( nextAgentName );
                agent.setInitProperties( properties );
                agent.initialize();
                Thread agentThread = new Thread( LoggingServiceImpl.getLoggingThreadGroup(), agent, agent.getThreadName() );
                agent.setThread( agentThread );

                // Note that starting logging agents here with JDK1.1 causes the JIT to hang.
                // See the startLoggingAgents() method.
                // agentThread.start();

                LoggingServiceImpl.addLoggingAgent( agent );
                logger.insertMsgVector(new MsgVector(filter, agent), 0);
            	agent.startAgent();
            }
            catch( LoggingAgentAlreadyExistsException e ) 
            {
                defaultLogger.log(MsgPriority.low, MsgCategory.information,
                    "Failed to add logging agent. An equivalent agent already exists." );
                throw new InitializationFailedException( "Could  not add the logging agent named \"" + agent.getUniqueName() + "\". An agent by the same name, or equivalent agent already exists." );
            }
        }
    }
}