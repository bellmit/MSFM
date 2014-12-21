package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: ForcedPropertyDescriptorComparator
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 2, 2006 8:44:31 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Comparator that supports forced precedence for known entries. It will ensure that particular known entries are always
 * sorted first before unknown entries.
 */
public class ForcedPropertyDescriptorComparator implements Comparator<PropertyDescriptor>
{
    private List<String> forcedEntries;

    /**
     * Constructor that accepts the known entries that are to have precendence.
     * @param forcedEntries Each element should be the property name of a PropertyDescriptor. The order of precendence
     * in the sort will follow the order of the array elements.
     */
    public ForcedPropertyDescriptorComparator(String[] forcedEntries)
    {
        this.forcedEntries = new ArrayList<String>(0);

        if (forcedEntries != null)
        {
            ((ArrayList) this.forcedEntries).ensureCapacity(forcedEntries.length);
            for(String forcedEntry : forcedEntries)
            {
                this.forcedEntries.add(forcedEntry);
            }
        }
    }

    public int compare(PropertyDescriptor o1, PropertyDescriptor o2)
    {
        int result;

        String o1Name = o1.getName();
        String o2Name = o2.getName();

        if (forcedEntries.contains(o1Name))
        {
            if (forcedEntries.contains(o2Name))
            {
                int o1Index = forcedEntries.indexOf(o1Name);
                int o2Index = forcedEntries.indexOf(o2Name);
                result = (o1Index < o2Index ? -1 : (o1Index == o2Index ? 0 : 1));
            }
            else
            {
                result = -1;
            }
        }
        else if (forcedEntries.contains(o2Name))
        {
            result = 1;
        }
        else
        {
            result = o1Name.compareTo(o2Name);
        }
        return result;
    }
}
