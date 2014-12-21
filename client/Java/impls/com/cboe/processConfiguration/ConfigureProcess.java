package com.cboe.processConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.jaw.reference.common.Debug;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.ArrayList;
import java.text.*;
import java.io.File;

import com.cboe.systemsManagementService.commandLine.*;
import com.cboe.loggingService.*;
import com.cboe.idl.infrastructureServices.loggingService.corba.*;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.*;

public class ConfigureProcess extends Command
{

    private static class poArrayList extends java.util.ArrayList
    {
        public poArrayList()
        {
            super();
        }

        public poArrayList( poArrayList arrayList )
        {
            super( (ArrayList)arrayList );
        }

        public String toString()
        {
            String  commStr = "";
            for ( int i = 0; i < size(); i++ )
            {
                commStr = commStr + get( i ) + " ";
            }
            return commStr;
        }
    }

    private static class LocalLogger
    {
        RepositoryLoggingAgent  repositoryagent;
        FileLoggingAgent        fileagent;
        boolean                 debugOn;
        boolean                 alarmOn;
        boolean                 notifyOn;
        boolean                 exceptionOn;

        public LocalLogger( )
        {
            repositoryagent = null;
            fileagent       = null;
            debugOn         = false;
            alarmOn         = false;
            notifyOn        = false;
            exceptionOn     = false;
        }

        public void setAgent( RepositoryLoggingAgent repositoryAgent )
        {
            repositoryagent = repositoryAgent;
        }

        public void setAgent( FileLoggingAgent fileAgent )
        {
            fileagent = fileAgent;
        }

        public void debug( boolean  enable )
        {
            debugOn = enable;
        }
        public void alarm( boolean  enable )
        {
            alarmOn = enable;
        }
        public void notify( boolean  enable )
        {
            notifyOn = enable;
        }
        public void exception( boolean  enable )
        {
            exceptionOn = enable;
        }

        public void alarm( String msg )
        {
            if ( alarmOn )
            {
                localLog( new com.cboe.loggingService.Message( 0
                                                                , MsgPriority.critical
                                                                , MsgCategory.systemAlarm
                                                                , msg
                                                                , new MsgParameter[0]
                                                                , "ConfigureProcess process"
                                                                , "ConfigureProcess process"
                                                                , ""  /* Message Tag    */
                                                                , StdMsgType.NonStd
                                                                ));
            }
        }

        public void debug( String msg )
        {
            if ( debugOn )
            {
                localLog( new com.cboe.loggingService.Message( 0
                                                                , MsgPriority.low
                                                                , MsgCategory.debug
                                                                , msg
                                                                , new MsgParameter[0]
                                                                , "ConfigureProcess process"
                                                                , "ConfigureProcess process"
                                                                , ""  /* Message Tag    */
                                                                , StdMsgType.NonStd
                                                                ));
            }
        }

        public void exception( String msg )
        {
            if ( exceptionOn )
            {
                localLog( new com.cboe.loggingService.Message( 0
                                                                , MsgPriority.critical
                                                                , MsgCategory.systemAlarm
                                                                , msg
                                                                , new MsgParameter[0]
                                                                , "ConfigureProcess process"
                                                                , "ConfigureProcess process"
                                                                , ""  /* Message Tag    */
                                                                , StdMsgType.NonStd
                                                                ));
            }
        }

        public void notify( String msg )
        {
            if ( notifyOn )
            {
                localLog( new com.cboe.loggingService.Message( 0
                                                                , MsgPriority.high
                                                                , MsgCategory.systemNotification
                                                                , msg
                                                                , new MsgParameter[0]
                                                                , "ConfigureProcess process"
                                                                , "ConfigureProcess process"
                                                                , ""  /* Message Tag    */
                                                                , StdMsgType.NonStd
                                                                ));
            }
        }

        public void localLog( com.cboe.loggingService.Message msg )
        {
            if ( repositoryagent != null )
            {
                try
                {
                    repositoryagent.log( msg );
                }
                catch( LoggingFailedException lfe )
                {
                    System.err.print( "!!! Repository logging failed exceptions !!!" );
                    lfe.printStackTrace( System.err );
                    System.err.flush();
                    repositoryagent = null;
                }
            }
            if ( fileagent != null )
            {
                try
                {
                    fileagent.log( msg );
                }
                catch( LoggingFailedException lfe )
                {
                    System.err.print( "!!! File logging failed exceptions !!!" );
                    lfe.printStackTrace( System.err );
                    System.err.flush();
                    fileagent = null;
                }
            }
        }

    }


    private static final String         DISABLE_CENTRAL_LOGGING             = "DisableCentralLogging";
    private static final String         CENTRAL_REPOSITORY_LOGGING_NAME     = "CentralLoggingRepositoryName";

//    private static String               FILE_LOGGING_NAME                   = "log" + File.separator + "configure.process.log";
    private static final String         LOGGING_DIR                         = "LoggingDir";

    private static final String         LOG_DEBUG_ENABLE                    = "LogDebug";
    private static final String         LOG_ALARM_ENABLE                    = "LogAlarm";
    private static final String         LOG_NOTIFY_ENABLE                   = "LogNotify";

    private static final String         CONFIGURE_SETPROPERTYVALUE          = "SetPropertyValue";
    private static final String         CONFIGURE_SETOPTIONALPROPERTYVALUE  = "SetOptionalPropertyValue";

    private static final String         COMMAND_PROCESSNAME                 = "ConfigureProcessName";
    private static final String         COMMAND_TARGETRESOURCE              = "TargetResource";
    private static final String         COMMAND_GETPROPERTYVALUE            = "GetPropertyValue";
    private static final String         COMMAND_GETCOMPONENTS               = "GetComponents";
    private static final String         COMMAND_SETPROPERTYVALUE            = "SetPropertyValue";

    private static final String         FIELD_ORBNAME                       = "Orbname";
    private static final String         FIELD_SYSTEMOPTIONALPROPERTY        = "SystemOptionalProperty";

    private static final String         BEAN_NAME_GLOBALFOUNDATIONFRAMEWORK = "GlobalFoundationFramework";
    private static final String         BEAN_NAME_FOUNDATIONFRAMEWORK       = "FoundationFramework";
    private static final String         BEAN_FIELD_FFCONFIGURED             = BEAN_NAME_FOUNDATIONFRAMEWORK + "({0}).Application(ApplicationNameNotUsed).configured";

    private static final String         BASE_PROCESSNAME                    = "processes:Processes_ScreenBasedTrading.Process_";
    private static final String         BASE_HOMENAME                       = "";

    private static CommandLine          commandLine                         = null;
    private static String[]             processComponents                   = null;
    private static String               processName                         = null;

    private static RepositoryLoggingAgent   repositoryAgent                 = null;
    private static FileLoggingAgent     fileAgent                           = null;

    private static boolean              centralLoggingDisabled              = false;
    private static LocalLogger          Log                                 = null;
    private static String               centralRepositoryLoggingName        = "";
    private static String               loggingDir                          = "";

    public ConfigureProcess()
    { }

    public static void main(String[] args)
    {
        String      currentParm = null;
        boolean     buildingComm = false;
        int         argc;
        int         i;
        poArrayList commandArgs = new poArrayList();

        System.out.println("ConfigureProcess Running...");

        centralLoggingDisabled = systemPropertyDefined( DISABLE_CENTRAL_LOGGING );

        loggingDir = System.getProperty(LOGGING_DIR, "run_dir");
        String logFile = ".." + File.separator + loggingDir + File.separator + "log" + File.separator + "configure.process.log";

        centralRepositoryLoggingName = System.getProperty( CENTRAL_REPOSITORY_LOGGING_NAME, "SwitchNotDefined" );


            try
            {
                fileAgent = initializeFileLogging( "FileAgent", logFile );
                Log = new LocalLogger();
                Log.setAgent( fileAgent );
                Log.exception( true );
                Log.debug( systemPropertyDefined( LOG_DEBUG_ENABLE ) );
                Log.notify( systemPropertyDefined( LOG_NOTIFY_ENABLE ) );
                Log.alarm( systemPropertyDefined( LOG_ALARM_ENABLE ) );

                if ( !centralLoggingDisabled )
                {
                    if ( centralRepositoryLoggingName.compareTo( "SwitchNotDefined" ) != 0 )
                    {
                        try
                        {
                            repositoryAgent = initializeRepositoryLogging( "RepositoryAgent", centralRepositoryLoggingName );
                            Log.debug( "Connecting to Central Logging...connected." );
                        }
                        catch( Exception ife )
                        {
                            Log.alarm( "... WARNING ..." );
                            Log.alarm( "Central logging repository, " + centralRepositoryLoggingName + ", not available" );
                            Log.alarm( "Disabling logging to central repository" );
                            Log.alarm( "" );
                            repositoryAgent = null;
                        }
                    }
                    else
                    {
                        configurationFailure( "Central Logging Repository name NOT defined and NOT disabled" );
                    }
                }
                else
                {
                    repositoryAgent = null;
                    Log.debug( "Central Logging disabled" );
                }
                Log.setAgent( repositoryAgent );
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                System.exit(-1);
            }

        commandLine = new CommandLine();
        argc = args.length;

        // first arg MUST be -ConfigureProccessName (processName)
        //
        if (    ( argc > 1 )
            &&  ( args[ 0 ].compareToIgnoreCase( "-" + COMMAND_PROCESSNAME ) == 0 )
            &&  ( verifyProcessName( ( processName = args[ 1 ] ) ) == true )
            )
        {

            setProcessConfiguredStatus( processName, false );
            processComponents = getProcessComponents( processName );

            Log.debug( "START OF FOR LOOP [" + argc + "]" );

            for ( i = 2; i < argc; i++ )
            {
                currentParm = args[ i ];
                Log.debug( "currentParm (" + i + ") [" + currentParm + "]" );
                Log.debug( "commandArgs (" + i + ") [" + commandArgs.toString() + "]" );
                if ( currentParm.charAt(0) == '-' )
                {
                    if ( ! buildingComm )
                    {
                        commandArgs.add( currentParm.substring(1) );
                        buildingComm = true;
                    }
                    else
                    {
                        //execute
                        if (    ! ( validConfigurationCommand( commandArgs ) )
                            ||  ! ( executeConfiguration( commandArgs ) )
                            )
                        {
                            configurationFailure( "executeConfiguration" );
                        }
                        commandArgs.clear();
                        commandArgs.add( currentParm.substring(1) );
                        buildingComm = true;
                    }
                }
                else
                {
                    if ( buildingComm )
                    {
                        commandArgs.add( currentParm );
                        Log.debug( "commandArgs.add :: (" + i + ") [" + commandArgs.toString() + "]" );
                    }
                    else
                    {
                        System.out.println( "Unrecognized command line argument:" + currentParm );
                        Log.alarm( "Unrecognized command line argument:" + currentParm );
                    }
                }
            }

            Log.debug( "OUT OF FOR LOOP" );

            Log.debug( "currentParm (" + i + ") [" + currentParm + "]" );
            Log.debug( "commandArgs (" + i + ") [" + commandArgs.toString() + "]" );

            if ( buildingComm )
            {
                if (    ! ( validConfigurationCommand( commandArgs ) )
                    ||  ! ( executeConfiguration( commandArgs ) )
                    )
                {
                    configurationFailure( "executeConfiguration" );
                }
            }
        }
        else
        {
            configurationFailure( "verifyProcessName" );
        }

        configurationSuccess( "ConfigureProcess" );

    }

    private static boolean executeConfiguration( poArrayList command )
    {
        boolean         success = true;  // be optimistic

        Log.debug( "Executing [" + command.toString() + "] ..." );

        String function = (String)command.get( 0 );
/* for testing
*/
        // must construct this command to match CommandLine format
        //
        if ( CONFIGURE_SETOPTIONALPROPERTYVALUE.compareToIgnoreCase( function ) == 0 )
        {
            command = buildOptionPropertyCommand( command );
        }

        try
        {
            if (commandLine.parse( command.toString() ) )
            {
                commandLine.executeCurrentCommand();
                String[] aResult = commandLine.getCurrentResult();
                if ( aResult == null || aResult.length < 1 )
                {
//                    System.out.println("\n\n Command Execution Complete\n");
                    Log.debug("Command Execution Complete");
                }
                else
                {
                    System.out.println();
                    for (int i = 0; i < aResult.length; i++)
                    {
//                        System.out.println("\t" + (i+1) + ") " + aResult[i]);
                        Log.debug("\t" + (i+1) + ") " + aResult[i]);
                    }
                    System.out.println();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();    // but continue
            Log.exception( "!!! executeConfiguration failure !!!" );
            Log.exception( e.toString() );
            Log.exception( "Configuration command: [" + command.toString() + "]" );
            Log.exception( "" );
            success = false;
        }

        return success;
/*
*/
    }

    private static poArrayList buildOptionPropertyCommand( poArrayList origCommand )
    {
        String[]            results;
        String              comm;
        int                 i;
        String              function    = null;
        String              beanname    = null;
        String              propertyname= null;
        String              newvalue    = null;
        poArrayList         newCommand  = new poArrayList( origCommand );

        function        = (String)origCommand.get(0);
        propertyname    = (String)origCommand.get(1);
        newvalue        = (String)origCommand.get(2);

        comm = buildGetPropertyValues( BASE_PROCESSNAME + processName, FIELD_SYSTEMOPTIONALPROPERTY );
        try
        {
            if (commandLine.parse( comm ) )
            {
                commandLine.executeCurrentCommand();
                results = commandLine.getCurrentResult();

                // loop through result array looking for 'propertyname='
                for( i = 0; i < results.length; i++ )
                {
                    Log.debug( "buildOptionalPropertyCommand::" + results[ i ] + " == " + propertyname + "=" );
                    if ( results[ i ].startsWith( propertyname + "=" ) )
                    {
                        newCommand.clear();
                        newCommand.add( COMMAND_SETPROPERTYVALUE );
                        newCommand.add( BASE_PROCESSNAME + processName );
                        newCommand.add( FIELD_SYSTEMOPTIONALPROPERTY );
                        newCommand.add( "" + i );
                        newCommand.add( propertyname + "=" + newvalue );
                        break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();    // but continue
            Log.exception( "!!! buildOptionPropertyCommand failure !!!" );
            Log.exception( "Configuration command: [" + origCommand.toString() + "]" );
            Log.exception( "" );
        }

        return newCommand;
    }

    private static boolean verifyProcessName( String    procName )
    {
        boolean     processValid = false;
        String      comm = buildGetPropertyValue( BASE_PROCESSNAME + procName, FIELD_ORBNAME );

        Log.debug( "verifyProcessName [" + procName + "] ..." );
        try
        {
            if (commandLine.parse( comm ) )
            {
                commandLine.executeCurrentCommand();
                commandLine.getCurrentResult();
                processValid = true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();    // but continue
            Log.exception( "!!! verifyProcessName (" + procName + ") failed !!!" );
            Log.exception( "" );
            processValid = false;
        }

        return processValid;
    }

    private static String[] getProcessComponents( String    procName )
    {
        String[]    procComponents = null;
        String      comm = buildGetComponents( BASE_PROCESSNAME + procName );

        Log.debug( "getProcessComponents [" + procName + "] ..." );
        Log.debug( "... " + comm );
        try
        {
            if (commandLine.parse( comm ) )
            {
                commandLine.executeCurrentCommand();
                procComponents = commandLine.getCurrentResult();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();    // but continue
            Log.exception( "!!! getProcessComponents (" + procName + ") failed !!!" );
            Log.exception( "" );
        }

        return procComponents;
    }

    private static boolean validConfigurationCommand( poArrayList command )
    {
        boolean     configurationValid = false;

        if ( ((String)command.get( 0 )).startsWith( CONFIGURE_SETPROPERTYVALUE ) )
        {
            if ( ! ( configurationValid = validSetPropertyValue( command ) ) )
            {
                Log.alarm( "Invalid configuration command:[" + command.toString() + "]" );
            }
        }
        else if ( ((String)command.get( 0 )).startsWith( CONFIGURE_SETOPTIONALPROPERTYVALUE ) )
        {
            if ( ! ( configurationValid = validSetOptionalPropertyValue( command ) ) )
            {
                Log.alarm( "Invalid configuration command:[" + command.toString() + "]" );
            }
        }
        else
        {
            Log.debug( "!!! FAILURE !!!" );
            Log.debug( "Configuration command:[" + command.toString() + "] not valid or supported." );
            Log.debug( "" );
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "Configuration command:[" + command.toString() + "] not valid or supported." );
            Log.exception( "" );
            configurationValid = false;
        }

        return configurationValid;
    }

    private static boolean validSetPropertyValue( poArrayList command )
    {
        boolean     valid       = false;
        String      function    = null;
        String      beanname    = null;
        String      propertyname= null;
        String      newvalue    = null;

        if (    command.size() < 4
            ||  (function = (String)command.get(0)) == null
            ||  (beanname = (String)command.get(1)) == null
            ||  (propertyname = (String)command.get(2)) == null
            ||  (newvalue = (String)command.get(3)) == null
            )
        {
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "validSetPropertyValue::bad syntax::" + function + "." + beanname + "." + propertyname + "." + newvalue );
            Log.exception( "" );
            valid = false;
        }
        else
        {
            if ( validBeanProperty( beanname, propertyname ) )
            {
                valid = true;
            }
            else
            {
                Log.exception( "!!! FAILURE !!!" );
                Log.exception( "validSetPropertyValue::invalid HomeName and/or FieldName" );
                Log.exception( "Configuration command: [" + command.toString() + "]" );
                Log.exception( "" );
                valid = false;
            }
        }

        return valid;
    }

    private static boolean validBeanProperty( String beanName, String propertyName )
    {
        boolean     valid = false;
        String      comm = buildGetPropertyValue( beanName, propertyName );

        Log.debug( "validBeanProperty [" + beanName + "." + propertyName + "] ..." );
        try
        {
            if (commandLine.parse( comm ) )
            {
                commandLine.executeCurrentCommand();
                commandLine.getCurrentResult();
                valid = true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();    // but continue
            Log.exception( "!!! verifyBeanProperty (" + beanName + "." + propertyName + ") failed !!!" );
            Log.exception( "" );
            valid = false;
        }

        return valid;
    }

    private static boolean validSetOptionalPropertyValue( poArrayList command )
    {
        boolean             valid = false;
        String[]            results;
        String              comm;
        int                 i;
        String              function    = null;
        String              propertyname= null;
        String              newvalue    = null;

        if (    command.size() < 3
            ||  (function = (String)command.get(0)) == null
            ||  (propertyname = (String)command.get(1)) == null
            ||  (newvalue = (String)command.get(2)) == null
            )
        {
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "validSetOptionalPropertyValue::bad syntax::" + function + "." + propertyname + "." + newvalue );
            Log.exception( "" );
            valid = false;
        }
        else
        {
            comm = buildGetPropertyValues( BASE_PROCESSNAME + processName, FIELD_SYSTEMOPTIONALPROPERTY );
            try
            {
                if (commandLine.parse( comm ) )
                {
                    valid = false;
                    commandLine.executeCurrentCommand();
                    results = commandLine.getCurrentResult();

                    // loop through result array looking for 'propertyname='
                    for( i = 0; i < results.length; i++ )
                    {
                        Log.debug( "validSetOptionalPropertyValue::" + results[ i ] + " == " + propertyname + "=" );
                        if ( results[ i ].startsWith( propertyname + "=" ) )
                        {
                            valid = true;
                            Log.debug( "validSetOptionalPropertyValue::" + results[ i ] + " == " + propertyname + "=" );
                            break;
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();    // but continue
                Log.exception( "!!! validSetOptionalPropertyValue failed !!!" );
                Log.exception( "Configuration command: [" + command.toString() + "]" );
                Log.exception( "" );
                valid = false;
            }
        }

        if ( ! valid )
        {
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "validSetOptionalPropertyValue::invalid PropertyName (" + propertyname + ")" );
            Log.exception( "" );
        }

        return valid;
    }


    private static void logConfigurationFailure( String failMsg )
    {
        Log.exception( "!!! FAILURE !!!" );
        Log.exception( failMsg );
        Log.exception( "Configuration process halted." );
        Log.exception( "" );
    }

    private static void configurationFailure( String failMsg )
    {
        logConfigurationFailure( failMsg );
        setProcessConfiguredStatus( processName, false );
        System.exit( -1 );
    }

    private static void configurationSuccess( String okMsg )
    {
        Log.debug( "Success" );
        Log.debug( okMsg );
        Log.debug( "Configuration process completed." );
        setProcessConfiguredStatus( processName, true );
        System.exit(0);
    }

    private static RepositoryLoggingAgent initializeRepositoryLogging( String agentName, String serverName )
        throws InitializationFailedException
    {
        Properties                  agentProperties  = new Properties();
        RepositoryLoggingAgent      localAgent;

        agentProperties.put( "LoggingService.loggingAgent." + agentName + ".traderOfferName", serverName );
        agentProperties.put( "LoggingService.loggingAgent." + agentName + ".inProcess", "false" );
        try
        {
            localAgent = new RepositoryLoggingAgent( agentName );
            localAgent.setInitProperties( agentProperties );
            localAgent.initialize();
        }
        catch ( InitializationFailedException ife )
        {
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "Failed to initialize logging agent: " + ife );
            Log.exception( ife.toString() );
            Log.exception( "" );
            throw ife;
        }

        return localAgent;
    }

    private static FileLoggingAgent initializeFileLogging( String agentName, String fileName )
        throws InitializationFailedException
    {
        Properties                  agentProperties  = new Properties();
        FileLoggingAgent            localAgent;

        agentProperties.put( "LoggingService.loggingAgent." + agentName + ".absFilePath", fileName );
        agentProperties.put( "LoggingService.loggingAgent." + agentName + ".absFilePathSupplement", fileName + "2" );
        try
        {
            localAgent = new FileLoggingAgent( agentName );
            localAgent.setInitProperties( agentProperties );
            localAgent.initialize();
        }
        catch ( InitializationFailedException ife )
        {
            System.err.print( "Failed to initialize logging agent: " + ife );
            ife.printStackTrace( System.err );
            throw ife;
        }

        return localAgent;
    }

    private static String buildGetPropertyValue( String beanName, String propertyName )
    {
        return( COMMAND_GETPROPERTYVALUE + " " + beanName + " " + propertyName );
    }

    private static String buildGetPropertyValues( String beanName, String propertyName )
    {
        return( COMMAND_GETPROPERTYVALUE + " " + beanName + " " + propertyName );
    }

    private static String buildGetComponents( String beanName )
    {
        return( COMMAND_GETCOMPONENTS + " " + beanName );
    }

    private static void setProcessConfiguredStatus( String processName, boolean configured )
    {
        String      process         = BASE_PROCESSNAME + processName;
        String      ffName[]        = new String[ 1 ];
        String      configField     = null;
        MBean       processBean     = null;
        MBean[]     relatedMBeans   = null;
        MBean       FFBean          = null;

        try
        {
            processBean = getMBean( process );
            relatedMBeans = processBean.getRelatedMBeans();
            if ( ( relatedMBeans != null ) && ( relatedMBeans.length > 0 ) )
            {
                for ( int j = 0; j < relatedMBeans.length; j ++ )
                {
                    Log.debug( "setProcessConfiguredStatus:: ( " + j + " ) " + relatedMBeans[ j ].getMBeanType() );
                    if ( relatedMBeans[ j ].getMBeanType().compareTo( BEAN_NAME_GLOBALFOUNDATIONFRAMEWORK ) == 0 )
                    {
                        FFBean = relatedMBeans[ j ];
                        break;
                    }
                }
                if ( FFBean != null )
                {
                    if ( FFBean.getComponentOfTypeCount( BEAN_NAME_FOUNDATIONFRAMEWORK ) == 1 )
                    {
                        ffName[ 0 ] = FFBean.getComponents( BEAN_NAME_FOUNDATIONFRAMEWORK )[ 0 ].getName();
                    }
                    else
                    {
                        ffName[ 0 ] = "";
                    }
                    configField = MessageFormat.format( BEAN_FIELD_FFCONFIGURED, ffName );
                    Log.debug( "setProcessConfiguredStatus:: setting " + configField + " to : " + ( configured ? "true" : "false" ) );
                    FFBean.setPropertyValue( configField, ( configured ? "true" : "false" ) );
                }
                else
                {
                    Log.exception( "!!! FAILURE !!!" );
                    Log.exception( "Unable to obtain Foundation Framework bean to set configured status." );
                    Log.exception( "" );
                    logConfigurationFailure( "setProccessConfigurationStatus" );
                    System.exit( -1 );
                }
            }
            else
            {
                Log.exception( "!!! FAILURE !!!" );
                Log.exception( "Unable to obtain process information to set configured status." );
                Log.exception( "" );
                logConfigurationFailure( "setProccessConfigurationStatus" );
                System.exit( -1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.err );
            Log.exception( "!!! FAILURE !!!" );
            Log.exception( "Exception communicating with agent." );
            Log.exception( "" );
            logConfigurationFailure( "setProccessConfigurationStatus" );
            System.exit( -1 );
        }
    }

    private static boolean systemPropertyDefined( String    propertyName )
    {
        return ( ! System.getProperty( propertyName, "SwitchNotDefined" ).equalsIgnoreCase( "SwitchNotDefined" ) );
    }

    public String getDescription()
    {
        return "Description";
    }
    public String getExample()
    {
        return "Example";
    }
    public String getUsage()
    {
        return "Usage";
    }
    public void execute()
    {
    }


}
