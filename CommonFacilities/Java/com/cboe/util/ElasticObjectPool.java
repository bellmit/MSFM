package com.cboe.util;

import java.util.ArrayList;

/**
 *
 * @version 0.31
 * @author Kevin Park
 */
public final class ElasticObjectPool<T extends Copyable> implements ObjectPool<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_INCREMENT = 10;
    private final T objectTemplate;
    private final int initialCapacity;
    private final int capacityIncrement;
    private final int capacityIncrementX2;
    private final ArrayList<T> inList;
    private int maxNumSegments;
    private int poolSize;	// number of objects in pool
    static boolean logOverflow = System.getProperty("ObjectPool.logPoolOverflow", "false").equals("true");
    static boolean traceMode = System.getProperty("ObjectPool.traceMode", "false").equals("true");

    /**
     * @author Kevin Park
     * @param initialCapacity int
     * @param capacityIncrement int
     */
    public ElasticObjectPool(T objectTemplate, int initialCapacity, int capacityIncrement) {
        this.initialCapacity = initialCapacity;
        this.capacityIncrement = capacityIncrement;
        this.capacityIncrementX2 = capacityIncrement*2;
        this.inList = new ArrayList<T>(initialCapacity+capacityIncrementX2);
        this.objectTemplate = objectTemplate;
        for ( int i = 0; i < this.initialCapacity; i++ ) {
            this.inList.add( newObject() );
        }
        this.poolSize = this.initialCapacity;
    }

    @SuppressWarnings("unchecked")
    private T newObject()
    {
        return (T)this.objectTemplate.copy();
    }
    /**
     * @author Kevin Park
     * @param returnedObject java.lang.Object
     */
    public synchronized void checkIn(T returnedObject) {
        if (returnedObject==null)
        {
            return;
        }
        returnedObject.clear();
        this.inList.add( returnedObject );

        // reduce size of the pool when available size becomes twice the
        // capacity increment.  we will never reduce below initial size.

        if ( (this.poolSize > this.initialCapacity) &&
                (this.inList.size() > this.capacityIncrementX2) ) {
            int idx=this.inList.size();
            for ( int i=0; i < this.capacityIncrement; i++ ) {
                this.inList.remove(--idx);
            }
            this.poolSize = this.poolSize - this.capacityIncrement;
        }	 
    }
    /**
     * @author Kevin Park
     * @return java.lang.Object
     */
    public synchronized T checkOut() {
        T availableItem;

        // increase pool size by capacity increment when no objects are
        // available in pool.
        final int size = this.inList.size();
        if ( size==0 ) {
            for ( int i = 1; i < this.capacityIncrement; i++ ) {
                this.inList.add( newObject() );
            }
            this.poolSize = this.poolSize + this.capacityIncrement;
            availableItem = newObject();
        }
        else
        {
            availableItem = this.inList.remove(size-1);
        }

        return availableItem;
    }

    public boolean isLogOverflow()
    {
        return logOverflow;
    }

    public void setLogOverflow(boolean p_logOverflow)
    {
        logOverflow = p_logOverflow;
    }

    public boolean isTraceMode()
    {
        return traceMode;
    }

    public void setTraceMode(boolean p_traceMode)
    {
        traceMode = p_traceMode;
    }
    
    
    public int getMaxNumSegments()
    {
        return maxNumSegments;
    }

    public void setMaxNumSegments(int p_maxNumSegments)
    {
        maxNumSegments = p_maxNumSegments;
    }
    
    public void undoObjectPooling(StringBuffer returnValue)
    {
    }
    
    public void addMoreSegments(StringBuffer returnValue, int numberOfSegmentsToBeAdded)
    {
    }
}
