//
// ------------------------------------------------------------------------
// FILE: SoftValue.java
// 
// PACKAGE: com.cboe.presentation.collector
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.collector;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * This code is based on an original implementation by Dr. Heinz Kabutz (h.kabutz@computer.org).
 *
 * @author torresl@cboe.com
 */
public class SoftValue extends SoftReference
{
    private final Object key; // always make data member final

    public SoftValue(Object referent, Object key, ReferenceQueue referenceQueue)
    {
        super(referent, referenceQueue);
        this.key = key;
    }

    public Object getKey()
    {
        return key;
    }
}
