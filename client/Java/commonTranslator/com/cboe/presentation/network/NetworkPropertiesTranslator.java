//
// -----------------------------------------------------------------------------------
// Source file: NetworkPropertiesTranslator.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.network;

import java.util.*;
import java.io.*;

import com.cboe.presentation.environment.EnvironmentProperties;
import com.cboe.presentation.environment.EnvironmentManagerFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.environment.JMSHelper;
import com.cboe.presentation.environment.OrderedProperties;

/**
 * The Monitoring service and other network services rely on system properties to dictate important aspects of the
 * connection (e.g. which machine to connect to). Our properties are stored in an application properties file so that
 * the user can select between multiple environments. Prior to connecting, those application properties have to
 * transferred over to the system properties. The application properties we are connecting to are assumed to be
 * available through EnvironmentManagerFactory.find().getCurrentEnvironment();
 */
public class NetworkPropertiesTranslator
{
    private static NetworkPropertiesTranslator instance = new NetworkPropertiesTranslator();
    private static EnvironmentProperties toConnectTo;

    public static NetworkPropertiesTranslator getInstance()
    {
        return instance;
    }

    public void setServiceProperties()
    {
        Iterator systemProps = toConnectTo.getServiceProperties().keySet().iterator();

        while (systemProps.hasNext())
        {
            String propName = (String) systemProps.next();
            String propValue = (String) toConnectTo.getServiceProperties().get(propName);
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Adding <" + propName + "," + propValue + "> to System properties.",
                                           GUILoggerBusinessProperty.COMMON);
            }

            System.setProperty(propName, propValue);
        }

    }

    /**
     * Write a temp Jms.properties file, based on the template file.  The
     * generated file will be named according to the "Jms.Properties" system
     * property.
     * Override template file is introduced to provide specific overrides of the
     * the base set.  It is probably not required by all apps, yet for SHM to support
     * AMQ split where some processes are not on the AMQ_SERVER_FAILOVER host url but the
     * AMQ_SERVER_OVERRIDE url.
     * @param jmsTemplateFile
     */
    public void createJMSPropertiesFile(File jmsTemplateFile, File jmsOverrideTemplateFile)
    {
        if(jmsTemplateFile != null && jmsTemplateFile.isFile())
        {
            OrderedProperties jmsProperties;
            try
            {
                // load all Jms properties from the template file, maintaining the original
                // order (to make the generated file more easily readable for supporting)
                OrderedProperties templateProps = new OrderedProperties();
                templateProps.load(new FileInputStream(jmsTemplateFile));

                //only include the override file if the property is fully valid and the file exists
                if(toConnectTo.getAMQServerOverrideURL() != null
                        && toConnectTo.getAMQServerOverrideURL().length() > 0
                        && jmsOverrideTemplateFile != null
                        && jmsOverrideTemplateFile.isFile())
                {
                    templateProps.load(new FileInputStream(jmsOverrideTemplateFile));
                }

                // substitute in the prefix and AMQ Server Failover URL
                jmsProperties = JMSHelper.configureJMSProperties(templateProps, toConnectTo.getSBTPrefix(), toConnectTo.getAMQServerFailoverURL(), toConnectTo.getAMQServerOverrideURL());
            }
            catch(IOException e)
            {
                jmsProperties = new OrderedProperties();
                DefaultExceptionHandlerHome.find().process(e, "Could not write JMS Properties template file '"+jmsTemplateFile.getAbsoluteFile()+"'");
            }

            File jmsPropertiesFile;
            // get the name for the generated file
            String jmsPropertiesFilePath = System.getProperty("Jms.Properties");
            if(jmsPropertiesFilePath == null)
            {
                File directory = jmsTemplateFile.getParentFile();
                jmsPropertiesFile = new File(directory, "Jms.properties.generated");
            }
            else
            {
                jmsPropertiesFile = new File(jmsPropertiesFilePath);
            }
            //write the properties to the generated file
            PrintStream ps = null;
            try
            {
                ps = new PrintStream(new FileOutputStream(jmsPropertiesFile));
                for(Object key : jmsProperties.getKeyList())
                {
                    String keyStr = key.toString();
                    String value = jmsProperties.getProperty(keyStr);
                    ps.println(keyStr+"="+value);
                }
            }
            catch(IOException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not write JMS Properties file '"+jmsPropertiesFile.getAbsoluteFile()+"'");
            }
            finally
            {
                if(ps != null)
                {
                    ps.flush();
                    ps.close();
                }
            }
        }
    }

    public void setOrbProperties()
    {
        System.setProperty("ORB.InitRefURL", toConnectTo.getInitIORRef());
    }

    public void reInitialize()
    {
        initialize();
    }

    private NetworkPropertiesTranslator()
    {
        initialize();
    }

    private void initialize()
    {
        toConnectTo = EnvironmentManagerFactory.find().getCurrentEnvironment();
    }


}
