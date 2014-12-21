//
// ------------------------------------------------------------------------
// FILE: SeverityFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.SessionKeyUserDataHelper;
import com.cboe.interfaces.domain.UserData;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.instrumentation.common.formatters.UserDataFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.UserDataTypes;
import org.omg.CORBA.UserException;

import java.text.ParseException;

/**
 * @author torresl@cboe.com
 */
public class UserDataFormatter  extends Formatter implements UserDataFormatStrategy
{
    private final String VALUE_DELIMITER = ",";
    private final String VALUES_TERMINATOR = ";";
    private final String SESSION_CLASS_DELIMITER = ":";
    private final String KEY_VALUE_DELIMITER = "=";

    public UserDataFormatter()
    {
        super();
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        setDefaultStyle(FULL_INFO_NAME);
    }

    public String format(String userData)
    {
        return format(userData, getDefaultStyle());
    }

    public String format(UserData userData)
    {
        return format(userData, getDefaultStyle());
    }

    public String format(String userData, String style)
    {
        String returnValue;
        try
        {
            UserData instrumentorUserData = new InstrumentorUserData(userData);
            returnValue = format(instrumentorUserData, style);
        }
        catch(ParseException e)
        {
            GUILoggerHome.find().exception("Unable to parse UserData", e);
            returnValue = "";
        }
        return returnValue;
    }

    public String format(UserData userData, String style)
    {
        StringBuffer buffer = new StringBuffer();
        String[] keys = userData.getAllKeys();
        for(int i = 0; i < keys.length; i++)
        {
            String[] values = userData.getValues(keys[i]);
            buffer.append(format(keys[i],values, style));
        }

        return buffer.toString();
    }

    private String format(String key, String[] values, String style)
    {
        StringBuffer buffer = new StringBuffer(formatKey(key, style));
        int count = values == null ? 0 : values.length;
        buffer.append('[').append(count).append(']');
        buffer.append(KEY_VALUE_DELIMITER);
        for(int i = 0; i < values.length; i++)
        {
            buffer.append(formatValue(key, values[i]));
            if (i < values.length - 1)
            {
                buffer.append(VALUE_DELIMITER);
            }
        }
        buffer.append(VALUES_TERMINATOR);

        return buffer.toString();
    }

    private String formatValue(String key, String value)
    {
        String retVal;
        if (key.equals(UserDataTypes.CAS_VERSION))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.CLASS))
        {
            retVal = formatClass(value);
        }
        else if (key.equals(UserDataTypes.EXCEPTION))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.FIRMS))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.HOST_NAME))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.INBOUND_SEQ))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.INFO))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.NUMBER_OF_USERS))
        {
            retVal = value;
        } else if (key.equals(UserDataTypes.OUTBOUND_SEQ))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.PORT))
        {
            retVal = value;
        }
        else if (key.equals(UserDataTypes.SESSION_CLASS))
        {
            retVal = formatSessionClass(value);
        }
        else if (key.equals(UserDataTypes.TYPE_SESSION_CLASS))
        {
            retVal = formatTypeSessionClass(value);
        }
        else
        {
            retVal = value;
            GUILoggerHome.find().alarm("Unknown User Data Key. Key = " + key);
        }

        return retVal;
    }


    private String formatKey(String key, String style)
    {
        String retVal = key;
        if (style.equals(UserDataFormatStrategy.FULL_INFO_NAME))
        {
            if ( key.equals(UserDataTypes.CAS_VERSION))
            {
                retVal = "CAS_VERSION";
            }
            else if (key.equals(UserDataTypes.CLASS))
            {
                retVal = "CLASS";
            }
            else if (key.equals(UserDataTypes.EXCEPTION))
            {
                retVal = "EXCEPTION";
            }
            else if (key.equals(UserDataTypes.FIRMS))
            {
                retVal = "FIRMS";
            }
            else if (key.equals(UserDataTypes.HOST_NAME))
            {
                retVal = "HOSTNAME";
            }
            else if (key.equals(UserDataTypes.INBOUND_SEQ))
            {
                retVal = "INBOUND_SEQ";
            }
            else if (key.equals(UserDataTypes.INFO))
            {
                retVal = "INFO";
            }
            else if (key.equals(UserDataTypes.NUMBER_OF_USERS))
            {
                retVal = "NUMBER_OF_USERS";
            }
            else if (key.equals(UserDataTypes.OUTBOUND_SEQ))
            {
                retVal = "OUTBOUND_SEQ";
            }
            else if (key.equals(UserDataTypes.PORT))
            {
                retVal = "PORT";
            }
            else if (key.equals(UserDataTypes.SESSION_CLASS))
            {
                retVal = "SESSION_CLASS";
            }
            else if (key.equals(UserDataTypes.TYPE_SESSION_CLASS))
            {
                retVal = "TYPE_SESSION_CLASS";
            }
            else
            {
                retVal = key;
                GUILoggerHome.find().alarm("Unknown User Data Key. Key = "+key);
            }
        }
        return retVal;
    }

    private String formatTypeSessionClass(String value)
    {
        String retVal = value;
        StringBuffer buffer = new StringBuffer(20);

        value = value.trim();
        int splitIndex = value.indexOf(SESSION_CLASS_DELIMITER);

        if (splitIndex != -1)
        {
            String type = (value.substring(0, splitIndex)).trim();
            String sessionClass = (value.substring(splitIndex + 1, value.length())).trim();
            buffer.append(type);
            buffer.append(SESSION_CLASS_DELIMITER);
            buffer.append(formatSessionClass(sessionClass));
        }
        else
        {
            GUILoggerHome.find().alarm(this.getClass().getName()+".formatTypeSessionClass()","No session/key delimiter character found in field: "+ value);
        }

        return retVal;
    }

    private String formatSessionClass(String value)
    {
        String retVal = value;
        StringBuffer buffer = new StringBuffer(12);
        try
        {
            SessionKeyContainer sessionKey = SessionKeyUserDataHelper.decode(value);
            int classKey = sessionKey.getKey();
            buffer.append(sessionKey.getSessionName());
            buffer.append(SESSION_CLASS_DELIMITER);
            buffer.append(formatClass(classKey));
            retVal = buffer.toString();
        }
        catch(ParseException e)
        {
            GUILoggerHome.find().exception(this.getClass().getName() + ".formatSessionClass()", "Unable to parse Session Class Key string; string=" + value, e);
        }

        return retVal;
    }

    private String formatClass(int classKey)
    {
        String returnValue = Integer.toString(classKey);
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();
        // If ProductQuery is not initialized return the original key string
        if (api.isProductQueryServiceInitialized())
        {
//            ProductClassFormatStrategy productClassFormatter = com.cboe.presentation.common.formatters.FormatFactory.getProductClassFormatStrategy();
            ProductClassFormatStrategy productClassFormatter = CommonFormatFactory.getProductClassFormatStrategy();

            try
            {
                ProductClass productClass = api.getProductClassByKey(classKey);
                returnValue = productClassFormatter.format(productClass,ProductClassFormatStrategy.CLASS_TYPE_NAME);
            }
            catch (UserException e)
            {
                GUILoggerHome.find().exception(this.getClass().getName() + ".getProductClass()", "Unable to get ProductClass for a key=" + classKey, e);
            }
        }
        return returnValue;
    }

    private String formatClass(String classKey)
    {
        String returnValue = classKey;
        try
        {
            int key = Integer.parseInt(classKey);
            returnValue = formatClass(key);
        }
        catch (NumberFormatException e)
        {
            GUILoggerHome.find().exception(this.getClass().getName() + ".formatClass()", "Unable to parse Class Key string; key=" + classKey, e);
        }

        return returnValue;
    }

}
