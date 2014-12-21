//
// -----------------------------------------------------------------------------------
// Source file: RoutingErrorFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.lang.reflect.Field;
import java.util.*;

import com.cboe.idl.constants.OrderRoutingReasonsOperations;

/**
 * This class translates a Short error or exception code into a description. It first searches a map keyed by the Short
 * code. It returns the value located at that key, if it finds one. Otherwise it uses reflection to grab the list of
 * constants defined in the <code>OrderRoutingReasonsOperations</code> interface. It then loops over each constant and
 * if it matches the Short error code, it adds the contant's name (i.e. as spelled in the interface) to the map, keyed
 * by the Short code. It thus lazily builds a map of codes to descriptions.
 *
 * @author  Shawn Khosravani
 * @since   Nov 9, 2007
 */
public class RoutingErrorFormatter extends ErrorCodeFormatter
{
    private static Map<String, String> codeToNameMap = new TreeMap<String, String>();

    @Override
    public String format(Short errCodeShort, String style)
    {
        String errCodeAsString = String.valueOf(errCodeShort);
        String errDescription  = codeToNameMap.get(errCodeAsString);

        if (errDescription == null)
        {
            errDescription = getErrDescription(errCodeAsString);
        }

        validateStyle(style);

        if (style.equals(UPPER_CASE_FORMAT))
        {
            errDescription = errDescription.toUpperCase();
        }
        else if (style.equals(CAPITALIZED_FORMAT))
        {
            errDescription = errDescription.toLowerCase();
            char[] errDescArray  = errDescription.toCharArray();
            int    errDescLength = errDescArray.length;
            for (int i = 0; i < errDescLength; ++i)
            {
                if (Character.isSpaceChar(errDescArray[i])  &&  (i + 1) < errDescLength)
                {
                    errDescArray[i+1] = Character.toUpperCase(errDescArray[i + 1]);
                    //noinspection AssignmentToForLoopParameter
                    ++i;
                }
            }
            errDescArray[0] = Character.toUpperCase(errDescArray[0]);
            errDescription = new String(errDescArray);
        }

        return errDescription;  // this is already done elsewhere: + " (" + errCodeShort + ')';
    }

    private String getErrDescription(String errString)
    {
        String description = errString;

        //noinspection EmptyClass
        OrderRoutingReasonsOperations dummyRef = new OrderRoutingReasonsOperations() { };

        Class   interfaceClass = OrderRoutingReasonsOperations.class;
        Field[] fields         = interfaceClass.getFields();
        for(Field field : fields)
        {
            String name = field.getName();
            try
            {
                String value = field.get(dummyRef).toString();
                if(value.equals(errString))
                {
                    description = name.replace('_', ' ');
                    codeToNameMap.put(value, description);
                    break;
                }
            }
            catch(IllegalAccessException e)
            {
                ;   // TODO log error ??
            }
        }
        return description;
    }
}