//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyParser.java
//
// PACKAGE: com.cboe.internalPresentation.common.logging
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.logging;

import java.util.*;
import java.util.concurrent.*;

/**
 * A parser to identify and extract the changes in the properties
 *
 * @author Anish A Pandit
 */
public class TradingPropertyParser
{
    private Map<String, String> oriKeyValue = new ConcurrentHashMap<String, String>();
    private Map<String, String> modKeyValue = new ConcurrentHashMap<String, String>();

    private List<String> oldList = new ArrayList<String>(32);
    private List<String> modList = new ArrayList<String>(32);

    private StringBuilder oldPropertyValues = new StringBuilder(32);
    private StringBuilder newPropertyValues = new StringBuilder(32);
    private static final String DNF = "DATA NOT FOUND";

    protected TradingPropertyParser()
    {

    }

    /**
     * Validates the input before sending for parsing
     * @param oldProperties old Properties
     * @param newProperties new properties
     * @return String[]
     */
    protected String[] validateAndSendForParsing(String oldProperties, String newProperties)
    {

        if(TradingPropertyCentralLogger.FAILED.equalsIgnoreCase(oldProperties) ||
           TradingPropertyCentralLogger.FAILED.equalsIgnoreCase(newProperties))
        {
            return new String[]{oldProperties, newProperties};
        }

        String[] parsedProperties = new String[2];
        boolean validForParsing = true;

        if(oldProperties == null || oldProperties.trim().length() == 0)
        {
            parsedProperties[0] = DNF;
            validForParsing = false;
        }
        else
        {
            parsedProperties[0] = oldProperties;
        }

        if(newProperties == null || newProperties.trim().length() == 0)
        {
            parsedProperties[1] = DNF;
            validForParsing = false;
        }
        else
        {
            parsedProperties[1] = newProperties;
        }

        if(validForParsing)
        {
            parsedProperties = compareStr(oldProperties, newProperties);
        }

        return parsedProperties;
    }

    /**
     * The method for starting the comparison and parsing
     * @param oldProperties : The old property string
     * @param newProperties : The modified property string
     */
    private String[] compareStr(String oldProperties, String newProperties)
    {
        modList.clear();
        oldList.clear();
        oldPropertyValues.delete(0, oldPropertyValues.length());
        newPropertyValues.delete(0, newPropertyValues.length());

        String[] parsedProperties = new String[2];

        //split with semicolons
        String[] oldPropSplit = oldProperties.split(";");
        String[] modPropSplit = newProperties.split(";");

        //if single tokens direct comparison
        if(oldPropSplit.length == 1 && modPropSplit.length == 1)
        {
            oldList.add(oldPropSplit[0]);
            String key = null;
            if(oldPropSplit[0].indexOf(",") != -1)
            {
                key = oldPropSplit[0].split(",")[0];
            }
            compareValues(key, oldPropSplit[0], modPropSplit[0]);
        }
        else
        {
            parse(oldPropSplit, modPropSplit);
        }
        processProp(oriKeyValue, true);
        processProp(modKeyValue, false);
        formatResults();
        parsedProperties[0] = oldPropertyValues.toString();
        parsedProperties[1] = newPropertyValues.toString();
        return parsedProperties;
    }

    /**
     * Processes the old and new properties
     * @param oldPropSplit old property values
     * @param newPropSplit new property values
     */
    private void parse(String[] oldPropSplit, String[] newPropSplit)
    {
        //Sorts based on tabs
        Arrays.sort(oldPropSplit);
        Arrays.sort(newPropSplit);

        //Extracts comma splits
        preparePropertyTable(oldPropSplit, oriKeyValue);
        preparePropertyTable(newPropSplit, modKeyValue);

        oldList.addAll(oriKeyValue.values());

        //Obtains the key set of old prop values
        Set<String> keysForOldProp = oriKeyValue.keySet();
        for(String key : keysForOldProp)
        {
            //If old key matches with new key, compare the values
            if(modKeyValue.containsKey(key))
            {
                compareValues(key, oriKeyValue.get(key), modKeyValue.get(key));
                oriKeyValue.remove(key);
                modKeyValue.remove(key);
            }
        }
    }

    /**
     * makes property table based on comma
     * @param propertyValue value to be splitted
     * @param propertyMap map for storing the values
     */
    private void preparePropertyTable(String[] propertyValue, Map<String, String> propertyMap)
    {
        String[] commaSplit;
        for(String oldPropValue : propertyValue)
        {
            commaSplit = oldPropValue.split(",");
            if(commaSplit[0].trim().length() != 0)
            {
                propertyMap.put(commaSplit[0].trim(), oldPropValue);
            }
        }
    }

    /**
     * Processes the changed properties
     * @param propertyMap map storing property values
     */
    private void processProp(Map<String, String> propertyMap, boolean isOldProperty)
    {
        if(!propertyMap.isEmpty())
        {
            String originalMatches = TradingPropertyCentralLogger.NONE;
            String newMatches = TradingPropertyCentralLogger.NONE;
            Set<String> keys = propertyMap.keySet();
            for(String key : keys)
            {
                if(isOldProperty)
                {
                    originalMatches = propertyMap.get(key);
                    newMatches = TradingPropertyCentralLogger.NONE;
                }
                else
                {
                    newMatches = propertyMap.get(key);
                    originalMatches = TradingPropertyCentralLogger.NONE;
                }
                if(TradingPropertyCentralLogger.NONE.equals(originalMatches))
                {
                    originalMatches = key + ':' + originalMatches;
                    //add to list only if NONE
                    oldList.add(originalMatches);
                }
                if(TradingPropertyCentralLogger.NONE.equals(newMatches))
                {
                    newMatches = key + ':' + newMatches;
                }
                modList.add(newMatches);
            }
            propertyMap.clear();
        }
    }

    /**
     * Compares the old and new property values for the given key.
     * @param key : the tab name
     * @param oldValue : old property value
     * @param newValue : new property value
     */
    private void compareValues(String key, String oldValue, String newValue)
    {
        //send for match
        int matchResult = oldValue.trim().compareTo(newValue.trim());
        if(matchResult != 0)
        {
            List<String> modValueList = getModValues(oldValue, newValue);
            modList.add(constructProperty(key, modValueList));
        }

    }

    /**
     * Constructs the property string with modified values
     * @return String
     */
    private String constructProperty(String key, List<String> modValueList)
    {
        StringBuilder strBuilder;
        if(key != null)
        {
            strBuilder = new StringBuilder(key);
            strBuilder.append(':');
        }
        else
        {
            strBuilder = new StringBuilder(32);
        }
        for(String modValue : modValueList)
        {
            strBuilder.append(modValue);
            strBuilder.append(',');
        }
        strBuilder.deleteCharAt(strBuilder.lastIndexOf(","));
        return strBuilder.toString();
    }

    /**
     * Extracts the modified values
     * @return List of modified values
     */
    private List<String> getModValues(String oldProp, String newProp)
    {
        List<String> oldPropLst = getValues(oldProp);
        List<String> newPropLst = getValues(newProp);
        newPropLst.removeAll(oldPropLst);
        return newPropLst;
    }

    /**
     * returns the old values corresponding to the new values
     * @return List of values extracted
     */
    private List<String> getValues(String properties)
    {
        String[] propertySplit = properties.split(",");
        List<String> propertyLst = new ArrayList<String>(propertySplit.length);
        for(String propertyValue : propertySplit)
        {
            propertyLst.add(propertyValue);
        }

        return propertyLst;
    }

    /**
     * formats the results
     */
    private void formatResults()
    {
        formatProperties(oldList, oldPropertyValues);
        formatProperties(modList, newPropertyValues);
    }

    /**
     * formats the properties.
     */
    private void formatProperties(List<String> propertyList, StringBuilder formattedProperty)
    {
        if(propertyList.isEmpty())
        {
            formattedProperty.append("NO CHANGE FOUND");
        }
        else
        {
            Collections.sort(propertyList);
            for(String result : propertyList)
            {
                result = result.replaceFirst(",", ":");
                formattedProperty.append(result).append(";");
            }

            formattedProperty.deleteCharAt(formattedProperty.lastIndexOf(";"));
        }
    }
}