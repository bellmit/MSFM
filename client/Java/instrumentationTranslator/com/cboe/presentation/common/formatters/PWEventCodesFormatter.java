//
// -----------------------------------------------------------------------------------
// Source file: PWEventCodesFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.lang.reflect.Field;
import java.util.*;

import com.cboe.idl.processWatcher.PWEventCodes;

public class PWEventCodesFormatter
{
    public static final String UNKNOWN_STRING = "Unknown";
    private static Map<Short, String> codesToStringsMap;

    static
    {
        Field[] eventCodeFields = PWEventCodes.class.getFields();
        codesToStringsMap = new HashMap<Short, String>(eventCodeFields.length);
        for(Field f : eventCodeFields)
        {
            if(f.getType() == short.class)
            {
                try
                {
                    codesToStringsMap.put(f.getShort(PWEventCodes.class) ,f.getName());
                }
                catch(IllegalAccessException e)
                {
                }
            }
        }
    }

    private PWEventCodesFormatter()
    {
    }

    public static String toString(short reasonCode)
    {
        String retVal = codesToStringsMap.get(reasonCode);

        if(retVal == null)
        {
            String reasonCodeStr = Short.toString(reasonCode);
            StringBuffer sb = new StringBuffer(UNKNOWN_STRING.length() + reasonCodeStr.length() + 3);
            sb.append(UNKNOWN_STRING).append(" (").append(reasonCodeStr).append(')');
            retVal = sb.toString();
            codesToStringsMap.put(reasonCode, retVal);
        }

        return retVal;
    }

    public static void main(String[] args)
    {
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.Unknown));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.UpByHelpButCommFailureBySelf));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.UpByHelpButTimeoutBySelf));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.UpNoResponseButCommFailureBySelf));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.UpNoResponseButTimeoutBySelf));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.DownCommFailure));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.DownTimeout));
        System.out.println(PWEventCodesFormatter.toString(PWEventCodes.UpNormal));
        System.out.println(PWEventCodesFormatter.toString((short)123));
    }
}
