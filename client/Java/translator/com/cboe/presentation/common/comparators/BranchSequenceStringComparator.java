//
// -----------------------------------------------------------------------------------
// Source file: BranchSequenceStringComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class BranchSequenceStringComparator implements Comparator
{
    public BranchSequenceStringComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int result = 0;

        try
        {
            StringTokenizer tokenizer1 = new StringTokenizer(( String ) o1, ":", false);
            StringTokenizer tokenizer2 = new StringTokenizer(( String ) o2, ":", false);

            String token1 = tokenizer1.nextToken();
            String token2 = tokenizer2.nextToken();

            result = token1.compareTo(token2);

            if( result == 0 )
            {
                Integer intToken1 = new Integer(tokenizer1.nextToken());
                Integer intToken2 = new Integer(tokenizer2.nextToken());

                result = intToken1.compareTo(intToken2);
            }
        }
        catch(Throwable e)
        {
            result = -1;
            GUILoggerHome.find().exception("Invalid comparison attempted.", e);
        }

        return result;
    }
}
