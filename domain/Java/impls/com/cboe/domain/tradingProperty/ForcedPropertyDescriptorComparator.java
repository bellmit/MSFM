//
// -----------------------------------------------------------------------------------
// Source file: ForcedPropertyDescriptorComparator.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;
import java.beans.PropertyDescriptor;

/**
 * Comparator that supports forced precedence for known entries. It will ensure that particular known entries are always
 * sorted first before unknown entries.
 */
public class ForcedPropertyDescriptorComparator implements Comparator
{
    private List forcedEntries;

    /**
     * Constructor that accepts the known entries that are to have precendence.
     * @param forcedEntries Each element should be the property name of a PropertyDescriptor. The order of precendence
     * in the sort will follow the order of the array elements.
     */
    public ForcedPropertyDescriptorComparator(String[] forcedEntries)
    {
        this.forcedEntries = new ArrayList(0);

        if(forcedEntries != null)
        {
            ((ArrayList)this.forcedEntries).ensureCapacity(forcedEntries.length);
            for(int i = 0; i < forcedEntries.length; i++)
            {
                Object forcedEntry = forcedEntries[i];
                this.forcedEntries.add(forcedEntry);
            }
        }
    }

    public int compare(Object o1, Object o2)
    {
        int result;

        String o1Name = ((PropertyDescriptor) o1).getName();
        String o2Name = ((PropertyDescriptor) o2).getName();

        if(forcedEntries.contains(o1Name))
        {
            if(forcedEntries.contains(o2Name))
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
        else if(forcedEntries.contains(o2Name))
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
