package com.cboe.application.systemHealth;

import com.cboe.interfaces.application.SystemHealthQueryProcessor;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GIConfigurationRequestType;
import com.cboe.client.xml.bind.GIConfigurationResponse;
import com.cboe.client.xml.bind.GICommandLineArgument;
import com.cboe.client.xml.bind.GICommandLineArgumentType;
import com.cboe.client.xml.bind.impl.GICommandLineArgumentImpl;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Enumeration;
import java.util.Properties;

class ConfigurationQueryProcessorImpl implements SystemHealthQueryProcessor
{
    private String xmlRequest;
    private static String orbName;
    private static String clusterName;

    public static void initialize()
    {
        try
        {
            orbName = System.getProperty("ORB.OrbName");
            clusterName = System.getProperty("prefixCluster");
        }
        catch(Exception e)
        {
            Log.alarm("Error initialization in ConfigurationQueryProcessorImpl.");
        }
    }

    ConfigurationQueryProcessorImpl(String xmlRequest)
    {
        this.xmlRequest = xmlRequest;
    }

    public String processRequest()
    {
        String xmlResult = null;
        try
        {
            if(xmlRequest == null || xmlRequest.equals(EMPTY_STRING))
            {
                throw new IllegalArgumentException("Request string cannot be empty; must be XML.");
            }

            GIConfigurationRequestType configurationRequest = XmlBindingFacade.getInstance().getGIConfigurationRequestType(xmlRequest);
            if (!configurationRequest.getOrbName().equals(orbName))
            {
                throw new IllegalArgumentException("Configuration requested for process with different orbName");
            }
            GIConfigurationResponse configurationResponse = XmlBindingFacade.getInstance().getObjectFactory().createGIConfigurationResponse();
            configurationResponse.setClusterName(clusterName);
            configurationResponse.setOrbName(orbName);
            configurationResponse.setRawXml(EMPTY_STRING);
            configurationResponse.setRequestType(configurationRequest.getRequestType());
            Properties properties = System.getProperties();
            Enumeration keys = properties.keys();
            GICommandLineArgumentType[] commandLineTypes = new GICommandLineArgumentType[properties.size()];
            String key = null;
            String value = null;
            int i = 0;
            while(keys.hasMoreElements())
            {
                GICommandLineArgument argument = new GICommandLineArgumentImpl();
                key = (String)keys.nextElement();
                value = properties.getProperty(key);
                argument.setKey(key);
                argument.setValue(value);
                commandLineTypes[i] = argument;
                i++;
            }
            configurationResponse.setCommandLineArguments(commandLineTypes);
            xmlResult = SystemHealthXMLHelper.marshal(configurationResponse);
        }
        catch(Exception e)
        {
            xmlResult = SystemHealthXMLHelper.logAndConvertException(xmlRequest, "configurationRequest", e);
        }

        return xmlResult;
    }
}
