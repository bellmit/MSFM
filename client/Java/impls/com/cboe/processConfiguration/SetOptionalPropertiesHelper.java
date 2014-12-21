package com.cboe.processConfiguration;

/**
 * Title:        Client Application Server
 * Description:  Your description
 * Copyright:    Copyright (c) 1998
 * Company:      Your Company
 * @author Your Name
 * @version
 */

public class SetOptionalPropertiesHelper
{
    public static final int            PROCESS_TYPE_CAS            = 0;
    public static final int            PROCESS_TYPE_SACAS          = 1;
    public static final int            PROCESS_TYPE_FIXCAS         = 2;

    private static final Object[]       casOptionalPropertiesChoices = {"com.sun.CORBA.ORBServerPort 8100"
                                                                        ,"ORB.TIOPTransport.ConnectorExpirationTime 14400"
                                                                        ,"ORB.LocationService.Timeout 330000"
                                                                        ,"ORB.FlowControl none"
                                                                        ,"configVerbose true"
                                                                        ,"prefix SERVERNAME"
                                                                        ,"prefixCASPairName CLIENTNAME_HOSTNAME_NoPair"
                                                                        ,"prefixWebServerPort 8001"
                                                                        ,"prefixCommandHttpPort 3105"
                                                                        ,"prefixRemoteRouteName SERVERNAME_FRONTEND_IDENTIFIER_Frontend"
                                                                        ,"prefixCASLogComponentName CASFile"
                                                                        ,"ORB.TIOPTransport.Recorder.Inclusions TWEDD"
                                                                        ,"prefixAdminServer SERVERNAME_CLIENTNAME_HOSTNAME"
                                                                        ,"ExtentMap.NodeName CLIENTNAME_HOSTNAME_CAS1"
                                                                        };

    private static final Object[]       sacasOptionalPropertiesChoices = {"CurrentlyNotSupported true"
                                                                            };

    private static final Object[]       fixcasOptionalPropertiesChoices = {"CurrentlyNoSupported true"
                                                                            };

    private static final Object[][]     optionalPropertiesChoices       = {casOptionalPropertiesChoices
                                                                            ,sacasOptionalPropertiesChoices
                                                                            ,fixcasOptionalPropertiesChoices
                                                                            };


    public SetOptionalPropertiesHelper()
    {
    }

    public static Object[] getSetOptionalPropertiesChoices( int    processType )
    {
        return ( (processType >= PROCESS_TYPE_CAS) && (processType <= PROCESS_TYPE_FIXCAS) ) ? optionalPropertiesChoices[ processType ] : new Object[0];
    }


}