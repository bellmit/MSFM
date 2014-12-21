package com.cboe.processConfiguration;

// XML imports
import com.ibm.xml.parser.TXDocument;
import com.ibm.xml.parser.DTD;
import com.ibm.xml.parser.*;

import java.lang.String;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;

import org.w3c.dom.*;

import com.cboe.systemsManagementService.managementFramework.repositoryService.*;


public class ConfigureProcessGUIHelper extends XMLFileDBUtil
{

    private static String           APPLICATION_ARGS_TAG                = "ApplicationArgument";
    private static String           PROCESS_PROPS_TAG                   = "ProcessProperties";

    private static String           SWITCH_CHAR                         = "-";
    private static String           PROCESS_CONFIGURATION_NAME_TAG      = "ConfigureProcessName";
    private static String           SET_PROPERTY_TAG                    = "SetPropertyValue";
    private static String           SET_OPTIONAL_PROPERTY_TAG           = "SetOptionalPropertyValue";

    private static TXDocument       processConfigurationDoc             = null;
    private static boolean          processConfigurationLoaded          = false;
    private String                  processConfigurationFilename        = "";
    private String                  processName                         = "";
    private ArrayList               setPropertiesList                   = null;
    private ArrayList               setOptionalPropertiesList           = null;

    private Node                    applicationArgsParentNode           = null;



    public ConfigureProcessGUIHelper()
    {
        super();
        setPropertiesList = new ArrayList();
        setOptionalPropertiesList = new ArrayList();
    }

    public boolean loadProcessConfigurationInfo( String  filename )
    {
        File            fileInfo = new File( filename );

        try
        {
            initialize( "." + File.separator, "" );
            processConfigurationFilename = filename;
            processConfigurationDoc = getDocument( filename );
            saveDocument( processConfigurationFilename + "~", processConfigurationDoc );
            processConfigurationLoaded = parseProcessConfiguration();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            processConfigurationDoc = null;
            processConfigurationLoaded = false;
        }

        return processConfigurationLoaded;

    }

    private boolean parseProcessConfiguration()
    {
        boolean             parsed = true;
        String              nodeValue;
        NodeList            nodeList;
        NodeList            childNodes;
        Node                parentNode;
        Node                childNode;
        int                 numNodes;
        int                 numChildNodes;
        int                 i;
        int                 j;
        StringTokenizer     strtok;

        try
        {
            nodeList = processConfigurationDoc.getElementsByTagName( APPLICATION_ARGS_TAG );
            numNodes = nodeList.getLength();
            // build XML scripts garantee that at least 1 application argument is present
            //
            applicationArgsParentNode = nodeList.item( 0 ).getParentNode();

            setPropertiesList.clear();
            setOptionalPropertiesList.clear();

            for ( j = 0 ; j < numNodes ; j++ )
            {
                childNodes = nodeList.item( j ).getChildNodes();

                System.out.println( "ChildNodes::" + nodeList.item( j ).getNodeName() + "::" + nodeList.item( j ).getNodeValue() );

                numChildNodes = childNodes.getLength();
                for ( i = 0 ; i < numChildNodes ; i++ )
                {
                    childNode = childNodes.item( i );

                    System.out.println( "   + ChildNode::" + childNode.getNodeName() + "::" + childNode.getNodeValue() );
                    nodeValue = childNode.getNodeValue();
                    strtok = new StringTokenizer( nodeValue, " " );
                    if ( nodeValue.startsWith( SWITCH_CHAR + PROCESS_CONFIGURATION_NAME_TAG ) )
                    {
                        strtok.nextElement();
                        processName = (String)strtok.nextElement();
                    }
                    else if ( nodeValue.startsWith( SWITCH_CHAR + SET_PROPERTY_TAG ) )
                    {
                        strtok.nextElement();
                        nodeValue   = "";
                        while ( strtok.hasMoreElements() )
                        {
                            nodeValue = nodeValue.concat( (String)strtok.nextElement() ) ;
                            nodeValue = nodeValue.concat( " " );
                        }
                        setPropertiesList.add( nodeValue );
                    }
                    else if ( nodeValue.startsWith( SWITCH_CHAR + SET_OPTIONAL_PROPERTY_TAG ) )
                    {
                        strtok.nextElement();
                        nodeValue   = "";
                        while ( strtok.hasMoreElements() )
                        {
                            nodeValue = nodeValue.concat( (String)strtok.nextElement() ) ;
                            nodeValue = nodeValue.concat( " " );
                        }
                        setOptionalPropertiesList.add( nodeValue );
                    }
                    else
                    {
                        parsed = false;
                        break;
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            parsed = false;
        }

        return parsed;

    }

    private void removeApplicationArguments()
        throws Exception
    {
        NodeList            nodeList;
        int                 numNodes;
        int                 i;

        nodeList = processConfigurationDoc.getElementsByTagName( APPLICATION_ARGS_TAG );
        numNodes = nodeList.getLength();

        for ( i = 0 ; i < numNodes ; i++ )
        {
            applicationArgsParentNode.removeChild( nodeList.item( i ) );
        }
    }

    private boolean updateProcessConfiguration()
    {
        boolean             updated = true;
        String              newValue;
        int                 i;

        try
        {
            removeApplicationArguments();

            insertApplicationArgument( SWITCH_CHAR + PROCESS_CONFIGURATION_NAME_TAG + " " + processName );
            System.out.println( "Updating ConfigureProcessName..." );

            for ( i = 0 ; i < setPropertiesList.size() ; i++ )
            {
                newValue = (String)setPropertiesList.get(i);
                insertApplicationArgument( SWITCH_CHAR + SET_PROPERTY_TAG + " " + newValue );
                System.out.println( "Updating SetPropertyNode..." );
            }
            for ( i = 0 ; i < setOptionalPropertiesList.size() ; i++ )
            {
                newValue = (String)setOptionalPropertiesList.get(i);
                insertApplicationArgument( SWITCH_CHAR + SET_OPTIONAL_PROPERTY_TAG + " " + newValue );
                System.out.println( "Updating SetOptionalPropertyNode..." );
            }

        }
        catch( Exception e )
        {
            e.printStackTrace();
            updated = false;
        }

        return updated;
    }

    private void insertApplicationArgument( String  argValue )
        throws Exception
    {
        Element             parentElement;
        Node                childNode;

        parentElement = processConfigurationDoc.createElement(APPLICATION_ARGS_TAG);
        childNode = (Node)processConfigurationDoc.createTextNode( argValue );
        parentElement.appendChild( childNode );
        applicationArgsParentNode.appendChild( (Node)parentElement );
        System.out.println( "   To  : " + argValue );
    }


    public String getProcessConfigurationName()
    {
        return processName;
    }

    public Object[] getSetProperties()
    {
        return setPropertiesList.toArray();
    }

    public Object[] getSetOptionalProperties()
    {
        return setOptionalPropertiesList.toArray();
    }

    public void setSetProperties( String[]  properties )
    {
        setPropertiesList.clear();
        for ( int i = 0; i < properties.length; i++ )
        {
            setPropertiesList.add( properties[ i ] );
        }
    }

    public void setSetOptionalProperties( String[] properties )
    {
        setOptionalPropertiesList.clear();
        for ( int i = 0; i < properties.length; i++ )
        {
            setOptionalPropertiesList.add( properties[ i ] );
        }
    }

    public boolean writeProcessConfigurationInfo()
    {
        boolean         success = true;

        if ( processConfigurationLoaded && updateProcessConfiguration() )
        {
            try
            {
                saveDocument( processConfigurationFilename, processConfigurationDoc );
            }
            catch( Exception e )
            {
                System.out.println( "!!! EXCEPTION !!! writing process configuration doc" );
                e.printStackTrace( System.out );
                success = false;
            }
        }
        else
        {
            System.out.println( "!!! updateProcessConfiguration failed or no document loaded !!! " );
            success = false;
        }

        return success;
    }

}

