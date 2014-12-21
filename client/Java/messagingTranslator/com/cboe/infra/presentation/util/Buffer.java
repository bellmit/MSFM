//
// -----------------------------------------------------------------------------------
// Source file: Buffer.java
//
// PACKAGE: com.cboe.infra.presentation.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Implements a FIFO Queue with a minimum size (floor) that must be reached
 * before clients are allowed to remove elements from the Buffer.
 * Upon reaching the minimum size however, the first getBuffer() thread to wake
 * claims all elements in the buffer (down to size() == 0 , not size() == floor)
 */
public class Buffer
{
    ArrayList store = new ArrayList();
    int ceiling = Short.MAX_VALUE;
    int floor = 0;

    public Buffer(int upperBounds, int lowerBounds)
    {
        ceiling = upperBounds;
        floor = lowerBounds;
    }

    public Buffer(int lowerBounds)
    {
        this(Short.MAX_VALUE, lowerBounds);
    }


    public Buffer()
    {
        this(Short.MAX_VALUE, 0);
    }

    /**
     * This method blocks until <code>floor</code> elements are in the buffer,
     * or until <code>bufferWaitTimeout</code> is exceeded.
     * bufferWaitTimeout is currently set to 10 seconds (but is configurable)
     * floor is specifed by the user in the class constructor
     * @return The ordered collection of elements as they appear in the buffer (i.e.
     * chronological ordering).
     */
    public Collection getBuffer()
    {
        Collection rv = new ArrayList();
        synchronized (store)
        {
            while (! dataAvailable() )
            {
                try {store.wait(10*1000);} catch (InterruptedException ie) {}
            }
            while ( store.size() > 0 )
            {
                rv.add( store.remove(0) );
            }
        	store.notifyAll();
        }
        return rv;
    }


    /**
     * This method returns immediately if there is room to spare in the buffer.
     * Otherwise, it blocks until enough elements have been removed to put the
     * buffer size less than <code>ceiling</code> - which is specified by the user
     * in the constructor.
     * @param o  The element to add to the buffer.  The buffer maintains a chronological
     * ordering of elements (ie. FIFO).
     */
    public void add(Object o)
    {
        synchronized (store)
        {
            while (! spaceAvailable() )
            {
                try {store.wait();} catch (InterruptedException ie) {}
            }
	        store.add(o);
	        if ( ! spaceAvailable() ) {
	        	store.notifyAll();
	        }
        }
    }

    /**
     * The number of elements in the buffer
     * @return
     */
    public int size() {
    	return store.size();
    }

    /**
     * Have we exceeded the minimum number of elements in the buffer?
     * @return True if <code>this.size() > floor</code>, false otherwise
     */
    protected boolean dataAvailable()
    {
        return store.size() > floor;
    }

    /**
     * Is there still space in the buffer to store elements?
     * @return True if <code>this.size() < ceiling</code>, false otherwise
     */
    protected boolean spaceAvailable()
    {
        return store.size() < ceiling;
    }}
