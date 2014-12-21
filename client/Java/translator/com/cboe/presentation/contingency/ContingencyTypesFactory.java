//
// -----------------------------------------------------------------------------------
// Source file: ContingencyTypesFactory.java
//
// PACKAGE: com.cboe.presentation.contingency
// 
// Author : Anish A. Pandit 
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.contingency;

import java.util.*;

import com.cboe.presentation.common.formatters.ContingencyTypes;

/**
 * Factory class for creating and obtaining various Contingencies
 */
public class ContingencyTypesFactory
{
    private static Map<Short, Contingency> contingencyTypeCollection = null;

    /**
     * finds and returns the Contingency for the corresponding type if already present or
     * creates, adds to collection store and returns the Contingency 
     * @param contingencyType type of contingency to be obtained
     * @return Contingency corresponding to the type.
     */
    public static Contingency find(short contingencyType)
    {
        Contingency contingency = getContingencyTypes().get(contingencyType);
        if(contingency == null)
        {
            contingency = createCustomizedContingency(ContingencyTypes.toString(contingencyType),
                                          ContingencyTypes.toString(contingencyType),
                                          contingencyType);
            getContingencyTypes().put(contingencyType,contingency);
        }
        return contingency;
    }

    /**
     * Creates customized contingencies with user defined name, display name and type. This method 
     * will not store the created contingency in the collection store. 
     * @param name contingency name
     * @param displayText text to be displayed by widgets or used by toString()
     * @param contingencyType type of contingency
     * @return Contingency created
     */
    public static Contingency createCustomizedContingency(String name, String displayText, short contingencyType)
    {
        Contingency contingency = new Contingency(name, displayText, contingencyType);
        return contingency;
    }

    /**
     * contingency collection store
     * @return Map contingency collection
     */
    private static Map<Short, Contingency> getContingencyTypes()
    {
        if(contingencyTypeCollection == null)
        {
            contingencyTypeCollection = new Hashtable<Short, Contingency>();
        }
        return contingencyTypeCollection;
    }
}
