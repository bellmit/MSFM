//
// -----------------------------------------------------------------------------------
// Source file: JMSHelper.java
//
// PACKAGE: com.cboe.presentation.foundationFramework
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.environment;

public class JMSHelper
{
    public static final String ENV_NAME_VARIABLE_STR = "\\%ENV_NAME\\%";
    public static final String AMQ_SERVER_FAILOVER_VARIABLE_STR = "\\%ENV_NAME\\%";

    //public static final String[] JMS_PROP_PREFIXES = {"Jms.", "EventService.Channel."};

    public static OrderedProperties configureJMSProperties(OrderedProperties templateProps, String envName,
                                                           String amqServerFailover, String amqServerOverride )
    {
        OrderedProperties returnProps = new OrderedProperties();
        for(Object key : templateProps.getKeyList())
        {
            String keyStr = key.toString();
            String value = templateProps.getProperty(keyStr);

            keyStr = keyStr.replaceAll(ENV_NAME_VARIABLE_STR, envName);
            keyStr = keyStr.replaceAll("\\%AMQ_SERVER_FAILOVER\\%", amqServerFailover);
            keyStr = keyStr.replaceAll("\\%AMQ_SERVER_OVERRIDE\\%", amqServerOverride);

            value = value.replaceAll("\\%ENV_NAME\\%", envName);
            value = value.replaceAll("\\%AMQ_SERVER_FAILOVER\\%", amqServerFailover);
            value = value.replaceAll("\\%AMQ_SERVER_OVERRIDE\\%", amqServerOverride);

            returnProps.setProperty(keyStr, value);
        }
        return returnProps;
    }

/*
    public static Properties configureJMSProperties(String envName, String amqServerFailover)
    {
        OrderedProperties jmsProps = getAllJMSProperties();
        return configureJMSProperties(jmsProps, envName, amqServerFailover);
    }

    public static OrderedProperties getAllJMSProperties()
    {
        OrderedProperties retVal = new OrderedProperties();
        Properties props = System.getProperties();
        for(Object key : props.keySet())
        {
            if(key instanceof String)
            {
                String keyStr = (String) key;
                for(String jmsPropPrefix : JMS_PROP_PREFIXES)
                {
                    if(keyStr.startsWith(jmsPropPrefix))
                    {
                        retVal.setProperty(keyStr, props.getProperty(keyStr));
                    }
                }
            }
        }

        return retVal;
    }

    public static void main(String[] args)
    {
        System.setProperty("Jms.ActiveMQ.UrlList", "failover:(tcp://%AMQ_SERVER%:%AMQ_SERVER_PORT%)?randomize=false");
        System.setProperty("Jms.ActiveMQ.StatsUrlList", "failover:(tcp://%AMQ_SERVER%:%AMQ_SERVER_PORT%)?randomize=false");
        System.setProperty("Jms.ActiveMQ.AdministratorClassName", "com.cboe.MOMTransport.jms.utils.ActiveMQJmsAdministratorImpl");
        System.setProperty("Jms.ActiveMQ.ProviderUtilsClassName", "com.cboe.MOMTransport.jms.utils.ActiveMQProviderUtils");
        System.setProperty("Jms.ActiveMQ.DestinationOptions", "consumer.prefetchSize=2000&consumer.dispatchAsync=true");
        System.setProperty("Jms.ActiveMQ.UrlPolicyOverrides", "wireFormat.version=2");
        System.setProperty("Jms.%ENV_NAME%ProcessWatcher.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%ProcessWatcher.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%InstrumentationChannel.ProviderName", "ActiveMQ");
        System.setProperty("Jms.ActiveMQ.%ENV_NAME%InstrumentationChannel.DestinationOptions=consumer.prefetchSize", "100");
        System.setProperty("EventService.Channel.%ENV_NAME%InstrumentationChannel.ConnectionPolicy", "1");
        System.setProperty("EventService.Channel.%ENV_NAME%InstrumentationChannel.UseCompression", "true");
        System.setProperty("Jms.%ENV_NAME%CentralLoggingRepository.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%CentralLoggingRepository.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%AdminService.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%AdminService.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%AlarmDefinition.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%AlarmDefinition.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%AlarmNotification.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%AlarmNotification.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%ClusterInfo.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%ClusterInfo.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%InstrumentationDetails.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%InstrumentationDetails.ConnectionPolicy", "1");
        System.setProperty("Jms.%ENV_NAME%InstrumentationSummary.ProviderName", "ActiveMQ");
        System.setProperty("EventService.Channel.%ENV_NAME%InstrumentationSummary.ConnectionPolicy", "1");

        configureJMSProperties(args[0], args[1]);

        Properties jmsProps = getAllJMSProperties();
        for(Object key : jmsProps.keySet())
        {
            String keyStr = key.toString();
            System.out.println(keyStr + "=" + jmsProps.getProperty(keyStr));
        }
    }
*/
}
