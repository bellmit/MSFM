//
// -----------------------------------------------------------------------------------
// Source file: SMComponentNameComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.Comparator;

import com.cboe.internalPresentation.sessionManagement.SessionManagementComponent;

/**
 * Compares SessionManagementComponent's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on getName.
 * Note: this comparator imposes orderings that are inconsistent with equals
 */
public class SMComponentNameComparator implements Comparator
{
    /**
     * SMComponentNameComparator constructor comment.
     */
    public SMComponentNameComparator()
    {
        super();
    }

    /**
     * compare method comment.
     */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else
        {
            String s1 = ((SessionManagementComponent)arg1).getName().trim();
            String s2 = ((SessionManagementComponent)arg2).getName().trim();

            return s1.compareToIgnoreCase(s2);
        }
    }
}