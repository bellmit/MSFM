//
// -----------------------------------------------------------------------------------
// Source file: CommonTranslatorProperties.java
//
// PACKAGE: com.cboe.presentation.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.properties;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

/**
 * Contains constants and static getter/setters for common properties defined in GUI property files
 *
 * @author  Shawn Khosravani
 * @version Feb 25, 2005
 */

public class CommonTranslatorProperties
{

    ////////////////////////////////////////////////// constants ///////////////////////////////////////////////////////

    public static final String  PROP_SECTION_NAME_DEFAULTS     = "Defaults";
    public static final String  PROP_KEY_CLEAN_CHANNEL_ADAPTER = "CleanChannelAdapter";     // boolean value
    public static final boolean DEFAULT_CLEAN_CHANNEL_ADAPTER  = false;
    public static final String  TRUE_STRING                    = Boolean.toString(true);
    public static final String  FALSE_STRING                   = Boolean.toString(false);


    ///////////////////////////////////////////////// constructors /////////////////////////////////////////////////////

    /**
     * Hidden (private) constructor. Currently contains static methods only.
     */
    private CommonTranslatorProperties()
    {
    }


    //////////////////////////////////////////// public methods ////////////////////////////////////////////////////////

    /**
     * @return  boolean - setting of Clean Channel Adapter property.
     *                    {@link #DEFAULT_CLEAN_CHANNEL_ADAPTER} if not specified. false if value is incorrect.
     * @see     #DEFAULT_CLEAN_CHANNEL_ADAPTER
     */
    public static boolean isCleanChannelAdapterEnabled()
    {
        return isCleanChannelAdapterEnabled(DEFAULT_CLEAN_CHANNEL_ADAPTER);
    }
    /**
     * @param   defaultValue boolean - the default value to use if property is not defined
     * @return  boolean - setting of Clean Channel Adapter property. defaultValue if not specified. false if value is incorrect.
     */
    public static boolean isCleanChannelAdapterEnabled(boolean defaultValue)
    {
        return getBooleanProperty(PROP_SECTION_NAME_DEFAULTS, PROP_KEY_CLEAN_CHANNEL_ADAPTER, defaultValue, "isCleanChannelAdapterEnabled");
    }

    /**
     * Adds the Clean Channel Adapter boolean property
     * @param cleanChannelAdapterEnabled boolean - if true, channel adapter will be cleaned if it receives exceptions
     */
    public static void setCleanChannelAdapterEnabled(boolean cleanChannelAdapterEnabled)
    {
        setBooleanProperty(PROP_SECTION_NAME_DEFAULTS, PROP_KEY_CLEAN_CHANNEL_ADAPTER, cleanChannelAdapterEnabled);
    }

    /**
     * Adds the Clean Channel Adapter boolean property
     * @param cleanChannelAdapterEnabled String - if "true", channel adapter will be cleaned if it receives exceptions
     */
    public static void setCleanChannelAdapterEnabled(String cleanChannelAdapterEnabled)
    {
        setBooleanProperty(PROP_SECTION_NAME_DEFAULTS, PROP_KEY_CLEAN_CHANNEL_ADAPTER, cleanChannelAdapterEnabled);
    }


    //////////////////////////////////////////// private methods ///////////////////////////////////////////////////////

    /**
     * @return  boolean - setting of a property with the name propKey within the section propSection. defaultValue is
     *                    returned if property is not specified
     */
    private static boolean getBooleanProperty(String propSection, String propKey, boolean defaultValue, String caller)
    {

        boolean      value  = defaultValue;                        // use default value if property not defined
        String       defMsg = ". Defaulting to " + value;
        StringBuffer msg    = new StringBuffer();

        msg.append(propKey + " property ");

        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String prop = getProperty(propSection, propKey);
            if (prop != null)
            {
                // value will be false if value is anything other than (case insensitive) true, including false
                value = Boolean.valueOf(prop).booleanValue();
                msg.append("is defined as " + value);
            }
            else
            {
                msg.append("is not defined" + defMsg);
            }
        }
        else
        {
            msg.append("is not available" + defMsg);
        }

        logDebug("CommonTranslatorProperties." + caller + ":", msg.toString());

        return value;

    } // end method getBooleanProperty

    /**
     * @param   propSection String - name of the property section that the property appears in
     * @param   propKey String - name (key) of the property within the property section
     * @return  String - the value of the propKey within the propSection, or null if not present
     */
    private static String getProperty(String propSection, String propKey)
    {
        return AppPropertiesFileFactory.find().getValue(propSection, propKey);
    }

    /**
     * @param   propSection String - name of the property section that the property appears in
     * @param   propKey String - name (key) of the property within the property section
     * @param   propValue boolean - value of the property
     */
    private static void setBooleanProperty(String propSection, String propKey, boolean propValue)
    {
        String stringValue = propValue ? TRUE_STRING : FALSE_STRING;
        setBooleanProperty(propSection, propKey, stringValue);
    }

    /**
     * @param   propSection String - name of the property section that the property appears in
     * @param   propKey String - name (key) of the property within the property section
     * @param   propValue String - value of the property as case insensitive "true" or "false"
     */
    private static void setBooleanProperty(String propSection, String propKey, String propValue)
    {
        if (propValue.equalsIgnoreCase(TRUE_STRING)  ||  propValue.equalsIgnoreCase(FALSE_STRING))
        {
            if (AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                AppPropertiesFileFactory.find().addValue(propSection, propKey, propValue);
            }
        }
        logDebug("CommonTranslatorProperties setBooleanProperty:",
                 "Did not find boolean property " + propKey + " in the " + propSection + " section");
    }

    // TODO replace USER_SESSION with a better fit, if there is one
    private static void logDebug(String title, String msg)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION ))
        {
            GUILoggerHome.find().debug(title, GUILoggerBusinessProperty.USER_SESSION, msg);
        }
    }

} // end class CommonTranslatorProperties
