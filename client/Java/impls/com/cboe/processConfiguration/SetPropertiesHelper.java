package com.cboe.processConfiguration;

import java.text.MessageFormat;

/**
 * Title:        Client Application Server
 * Description:  Your description
 * Copyright:    Copyright (c) 1998
 * Company:      Your Company
 * @author Your Name
 * @version
 */

public class SetPropertiesHelper
{
    public static final int            PROCESS_TYPE_CAS            = 0;
    public static final int            PROCESS_TYPE_SACAS          = 1;
    public static final int            PROCESS_TYPE_FIXCAS         = 2;

    private static Object[]             casPropertiesChoices = {"CASAuditLogHome{0}:GlobalAuditLogHome_0.AuditLogHomeImpl_DefaultService enableAudit false"
                                                                ,"CASHeartBeatHome{0}:GlobalHeartBeatHome_0.HeartBeatHome_DefaultService HeartBeatInterval 2000"
                                                                ,"CASHeartBeatHome{0}:GlobalHeartBeatHome_0.HeartBeatHome_DefaultService ConnectionLostFatal true"
                                                                };

    private static Object[]             sacasPropertiesChoices = {"NoHome CurrentlyNotSupported true"
                                                                };

    private static Object[]             fixcasPropertiesChoices = {"NoHome CurrentlyNoSupported true"
                                                                    };

    private static final Object[][]     propertiesChoices       = {casPropertiesChoices
                                                                    ,sacasPropertiesChoices
                                                                    ,fixcasPropertiesChoices
                                                                    };

    public SetPropertiesHelper()
    {
    }

    public static Object[] getSetPropertiesChoices( int processType, String processHomeName )
    {
        Object[]    returnSet = null;

        returnSet = ( (processType >= PROCESS_TYPE_CAS) && (processType <= PROCESS_TYPE_FIXCAS) ) ? propertiesChoices[ processType ] : new Object[0];
        returnSet = formatReturnSet( processHomeName, returnSet );
        return returnSet;
    }

    private static Object[] formatReturnSet( String processHomeName, Object[] origSet )
    {
        int         i;
        String[]    procHomeName = new String[ 1 ];

        procHomeName[ 0 ] = processHomeName;
        for ( i = 0 ; i < origSet.length ; i++ )
        {
            origSet[ i ] = (Object)(MessageFormat.format( (String)origSet[ i ], procHomeName ));
        }

        return origSet;

    }


}